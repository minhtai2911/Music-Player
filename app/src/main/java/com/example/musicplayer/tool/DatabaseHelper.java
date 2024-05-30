package com.example.musicplayer.tool;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;
import android.database.Cursor;
import androidx.annotation.Nullable;
import static com.example.musicplayer.activity.MainActivity.songList;
import static com.example.musicplayer.activity.PlayingActivity.isPlayable;
import static com.example.musicplayer.tool.NetworkChangeReceiver.checkConnected;

import com.example.musicplayer.model.PlaylistModel;
import com.example.musicplayer.model.SongModel;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "Music.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createPlaylistTable = "CREATE TABLE IF NOT EXISTS Playlist(" +
                "PlaylistID VARCHAR(4000) PRIMARY KEY NOT NULL," +
                "PlaylistName VARCHAR(4000) NOT NULL" +
                ")";
        db.execSQL(createPlaylistTable);
        String createPlaylistSongTable = "CREATE TABLE IF NOT EXISTS PlaylistSong(" +
                "SongPath VARCHAR(4000)," +
                "PlaylistID VARCHAR(4000)," +
                "FOREIGN KEY (PlaylistID) REFERENCES Playlist(PlaylistID)" +
                "PRIMARY KEY(SongPath, PlaylistID)" +
                ")";
        db.execSQL(createPlaylistSongTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Playlist");
        onCreate(db);
    }

    public void InsertPlaylist(PlaylistModel insertPlaylist)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String playlistId = insertPlaylist.getPlaylistId();
        String playlistName = insertPlaylist.getPlaylistName();
        String insertQuery = String.format("INSERT INTO Playlist VALUES('%s', '%s')", playlistId, playlistName);
        db.execSQL(insertQuery);
        Toast.makeText(context, "Create successfully", Toast.LENGTH_SHORT).show();
    }


    public ArrayList<PlaylistModel> QueryAllPlaylists(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<PlaylistModel> playlistsInDatabase = new ArrayList<PlaylistModel>();
        ArrayList<SongModel> songsInPlaylist = new ArrayList<SongModel>();
        String queryPlaylists = "SELECT PlaylistID, PlaylistName FROM Playlist";
        Cursor cursor = null;
        if(db!=null){
            cursor = db.rawQuery(queryPlaylists, null);
        }
        ArrayList<PlaylistModel> playlists = new ArrayList<>();
        if(cursor.getCount()!=0){
            while(cursor.moveToNext()){
                String playlistId = cursor.getString(0);
                String playlistName = cursor.getString(1);
                ArrayList<SongModel> listSong = QueryAllSongInGivenPlaylist(playlistId);
                PlaylistModel playlist = new PlaylistModel(playlistId, playlistName, listSong);
                playlists.add(playlist);
            }
        }
        return playlists;
    }

    public boolean UpdatePlaylist(PlaylistModel updatePlaylist)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        String playlistId = updatePlaylist.getPlaylistId();
        String playlistName = updatePlaylist.getPlaylistName();
        ContentValues cv = new ContentValues();
        cv.put("PlaylistName",playlistName);
        long result = db.update("Playlist", cv, "PlaylistID=?", new String[]{playlistId});
        if(result==-1) {
            Toast.makeText(context, "Fail to update", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Toast.makeText(context, "Change saved", Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    public void InsertSongIntoPlaylistSong(String songPath, PlaylistModel playlistContainsSong)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        String playlistId = playlistContainsSong.getPlaylistId();
        String insertQuery = String.format("INSERT INTO PlaylistSong VALUES('%s', '%s')", songPath, playlistId);
        db.execSQL(insertQuery);
        Toast.makeText(context, "Added to "+playlistContainsSong.getPlaylistName(), Toast.LENGTH_SHORT).show();
    }

    public ArrayList<SongModel> QueryAllSongInGivenPlaylist(String playlistID)
    {
        ArrayList<SongModel> mediasInPlaylist = new ArrayList();
        SQLiteDatabase db = this.getReadableDatabase();
        String queryMedia = String.format("SELECT SongPath FROM PlaylistSong WHERE PlaylistID = '%s'", playlistID);

        Cursor cursor = null;
        if(db!=null){
            cursor = db.rawQuery(queryMedia, null);
        }
        if(cursor.getCount()!=0){
            while(cursor.moveToNext()){
                String songPath = cursor.getString(0);
                for(int i=0;i<songList.size();i++){
                    SongModel song = songList.get(i);
                    if(isPlayable(song,checkConnected) && song.getPath().equals(songPath)){
                        mediasInPlaylist.add(song);
                    }
                }
            }
        }
        return mediasInPlaylist;
    }

    public boolean DeletePlaylist(PlaylistModel playlist)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        long result = db.delete("Playlist", "PlaylistID=?", new String[]{playlist.getPlaylistId()});
        if(result==-1) {
            Toast.makeText(context, "Fail to delete", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Toast.makeText(context, "Deleted "+playlist.getPlaylistName(), Toast.LENGTH_SHORT).show();
            return true;
        }
    }

    public boolean DeleteMediaInAPlaylist(SongModel song, String playlistID)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        long result = db.delete("PlaylistSong", "PlaylistID=? AND SongPath=?", new String[]{playlistID, song.getPath()});
        if(result==-1) {
            Toast.makeText(context, "Fail to delete", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            Toast.makeText(context, "Deleted "+song.getTitle(), Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}
