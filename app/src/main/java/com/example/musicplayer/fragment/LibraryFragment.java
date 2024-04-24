package com.example.musicplayer.fragment;

import static com.example.musicplayer.activity.MainActivity.getAllPlaylist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.musicplayer.R;
import com.example.musicplayer.adapter.LibraryAdapter;
import com.example.musicplayer.model.PlaylistModel;
import com.example.musicplayer.tool.DatabaseHelper;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

public class LibraryFragment extends Fragment {
    View view;
    RecyclerView recyclerView;
    LibraryAdapter libraryAdapter;
    ImageView addPlaylist;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_library, container, false);
        recyclerView = view.findViewById(R.id.recyclerview_library);
        libraryAdapter = new LibraryAdapter(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(libraryAdapter);
        addPlaylist = view.findViewById(R.id.add_playlist);
        addPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCreatePlaylistDialog();
            }
        });
        return view;
    }

    public void showCreatePlaylistDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        View viewDialog = getLayoutInflater().inflate(R.layout.add_playlist_dialog, null);
        EditText eName = viewDialog.findViewById(R.id.edt_playlist_name);
        Button btnCancel = viewDialog.findViewById(R.id.btn_cancel);
        Button btnCreate = viewDialog.findViewById(R.id.btn_create);
        builder.setView(viewDialog);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String playlistName = String.valueOf(eName.getText()).trim();
                if(playlistName.equals("")){
                    playlistName=String.valueOf(eName.getHint()).trim();
                }
                PlaylistModel newPlaylist = new PlaylistModel(playlistName);
                DatabaseHelper myDB = new DatabaseHelper(getActivity());
                myDB.InsertPlaylist(newPlaylist);
                libraryAdapter.addPlaylist(newPlaylist);
                libraryAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
