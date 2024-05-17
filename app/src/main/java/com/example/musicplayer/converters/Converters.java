package com.example.musicplayer.converters;

import com.example.musicplayer.api.shazam.models.Metadata;
import com.example.musicplayer.api.shazam.models.Section;
import com.example.musicplayer.api.shazam.models.ShazamResponse;
import com.example.musicplayer.api.shazam.models.Track;
import com.example.musicplayer.model.Music;

public class Converters {
    public static short[] toShortArray(byte[] byteArray) {
        short[] result = new short[byteArray.length / 2];
        for (int i = 0; i < byteArray.length; i += 2) {
            result[i / 2] = (short) ((byteArray[i] & 0xFF) | (byteArray[i + 1] << 8));
        }
        return result;
    }
    public static Music toMusic(ShazamResponse shazamResponse) {
        Track track = shazamResponse.getTrack();
        String album = null;
        String label = null;
        String released = null;
        String lyrics = null;

        if (track != null) {
            for (Section section : track.getSections()) {
                if ("SONG".equals(section.getType().toUpperCase())) {
                    for (Metadata metadata : section.getMetadata()) {
                        if ("ALBUM".equals(metadata.getTitle().toUpperCase())) {
                            album = metadata.getText();
                        } else if ("LABEL".equals(metadata.getTitle().toUpperCase())) {
                            label = metadata.getText();
                        } else if ("RELEASED".equals(metadata.getTitle().toUpperCase())) {
                            released = metadata.getText();
                        }
                    }
                } else if ("LYRICS".equals(section.getType().toUpperCase())) {
                    lyrics = String.join("\n", section.getText());
                }
            }
        }

        return new Music(
                track != null ? track.getTitle() : null,
                track != null ? track.getSubtitle() : null,
                track != null ? track.getImages().getCoverarthq() : null,
                album,
                label,
                released,
                lyrics
        );
    }
}
