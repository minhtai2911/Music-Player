package com.example.musicplayer.viewmodel;

import static kotlinx.coroutines.future.FutureKt.await;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.musicplayer.data.ShazamIdentifyDataSource;
import com.example.musicplayer.model.Music;
import com.example.musicplayer.repository.IdentifyRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import kotlin.jvm.Throws;
import kotlinx.coroutines.Deferred;
import kotlinx.coroutines.flow.MutableSharedFlow;


public class MusicRecognitionViewModel extends ViewModel {

    public static final int SAMPLE_RATE = 16000;
    public static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    public static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    // AudioFormat.CHANNEL_IN_MONO = 1
    // AudioFormat.CHANNEL_IN_STEREO = 2
    public static final int CHANNEL_COUNT = 1;

    // AudioFormat.ENCODING_PCM_8BIT = 2
    // AudioFormat.ENCODING_PCM_16BIT = 2
    public static final int SAMPLE_WIDTH = 2;
    public static final int IDENTIFY_RECORD_DURATION_MINIMUM = 5;
    public static final int IDENTIFY_RECORD_DURATION_MAXIMUM = 12;

    public static final int BUFFER_SIZE_MULTIPLIER = 8;
    public static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(
            SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT
    ) * BUFFER_SIZE_MULTIPLIER;


    private MutableLiveData<Boolean> _idle = new MutableLiveData<>(true);
    private MutableLiveData<Integer> _duration = new MutableLiveData<>(0);
    private MutableSharedFlow<Music> _music;
    private MutableSharedFlow<String> _error;
    private Music _identifyMusic;
    private Integer _identifyDuration;
    private AudioRecord instance = null;
    private CompletableFuture<Future<byte[]>> job = null;
    private IdentifyRepository repository = new IdentifyRepository(new ShazamIdentifyDataSource());
    private final Object lock = new Object();
    private final java.util.concurrent.locks.Lock mutex = new java.util.concurrent.locks.ReentrantLock();
    public void start(){
        createAudioRecord();
        synchronized (lock){
            if (_idle.getValue() != Boolean.FALSE) {
                _idle.postValue(false);
            }
            _identifyMusic = null;
            if (job == null){
                new Thread(() -> {
                    Log.d("Record", " AudioRecord.startRecording()");

                    instance.startRecording();

                    job = CompletableFuture.supplyAsync(() -> record());; // This should be a method that returns a Future<ByteArray>
                    try {
                        Future<byte[]> data = job.get();

                        Log.d("Record", "IdentifyFragmentViewModel: repository.identify=" + IDENTIFY_RECORD_DURATION_MAXIMUM);

                        synchronized(mutex) {
                            Music music = repository.identify(data, IDENTIFY_RECORD_DURATION_MAXIMUM);
                            if (!_identifyMusic.equals(music)) {
                                _identifyMusic = music;
                                _music.emit(music, null);
                            }
                        }
                    } catch (CancellationException e) {
                        e.printStackTrace();
                    } catch (Throwable e) {
                        e.printStackTrace();
                        _error.emit(e.getMessage(),null);
                    }

                    stop();
                }).start();
            }
        }
    }


        private Future<byte[]> record() {
            return Executors.newSingleThreadExecutor().submit(() -> {
                List<Byte> result = new ArrayList<>();
                while (!Thread.currentThread().isInterrupted()) {
                    // Calculate the currently recorded duration from number of samples:
                    // DURATION = NUMBER_OF_SAMPLES / (SAMPLE_RATE * SAMPLE_WIDTH * CHANNEL_COUNT)
                    int current = result.size() / (SAMPLE_RATE * SAMPLE_WIDTH * CHANNEL_COUNT);

                    // Notify LiveData for UI updates.
                    if (_duration.getValue() != current) {
                        _duration.postValue(current);
                    }

                    // The recorded duration exceeds the required duration... exit the polling loop.
                    if (current >= IDENTIFY_RECORD_DURATION_MAXIMUM) {
                        Log.d("Record", "IdentifyFragmentViewModel: Record complete");
                        break;
                    }

                    byte[] buffer = new byte[BUFFER_SIZE];
                    instance.read(buffer, 0, buffer.length);
                    for (byte b : buffer) {
                        result.add(b);
                    }

                    // Attempt to identify partial samples.
                    if (current > IDENTIFY_RECORD_DURATION_MINIMUM && _identifyDuration != current) {
                        _identifyDuration = current;

                        try {
                            Log.d("Record", "IdentifyFragmentViewModel: repository.identify=" + result.size());
                            CompletableFuture<byte[]> futureData = CompletableFuture.completedFuture(toByteArray(result));
                            Music music = repository.identify(futureData, current);

                            // NOTE: Only allow "well known" music (with album available in it) in partial samples.
                            if (music.getAlbum() != null) {
                                if (!_identifyMusic.equals(music)) {
                                    _identifyMusic = music;
                                    _music.emit(music,null);

                                    stop();
                                }
                            }

                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }

                    Log.d("Record", "IdentifyFragmentViewModel: current = " + current);
                }

                byte[] byteArray = new byte[result.size()];
                for (int i = 0; i < result.size(); i++) {
                    byteArray[i] = result.get(i);
                }
                return byteArray;
            });
        }

    private byte[] toByteArray(List<Byte> byteList) {
        byte[] byteArray = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) {
            byteArray[i] = byteList.get(i);
        }
        return byteArray;
    }

    private void createAudioRecord() throws SecurityException {
        synchronized(lock) {
            if (instance == null) {
                instance = new AudioRecord(
                        AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE
                );
                Log.d("create record", "IdentifyFragmentViewModel: AudioRecord");
            }
        }
    }

    public void stop(){
        createAudioRecord();
        synchronized (lock){
            if (job != null){
                instance.stop();
                job.cancel(true);
                job = null;
            }
            if(_idle.getValue() != Boolean.TRUE){
                _idle.postValue(true);
            }
            Log.d("Record", "IdentifyFragmentViewModel");
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        job.cancel(true);
        instance.stop();
        instance.release();
    }



    public MutableLiveData<Boolean> idle() {
        return _idle;
    }

    public MutableLiveData<Integer> duration() {
        return _duration;
    }

    public MutableSharedFlow<Music> music() {
        return _music;
    }

    public MutableSharedFlow<String> error() {
        return _error;
    }
}
