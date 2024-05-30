package com.example.musicplayer.adapter;
import static com.example.musicplayer.activity.MainActivity.currPlayedPlaylistID;
import static com.example.musicplayer.activity.MainActivity.currPlayedSong;
import static com.example.musicplayer.activity.MainActivity.getAllPlaylist;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.example.musicplayer.activity.PlaylistActivity;
import com.example.musicplayer.model.PlaylistModel;
import com.example.musicplayer.tool.DatabaseHelper;

import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.MyViewHolder> {
    Context context;
    View view;
    public static ArrayList<PlaylistModel> libraryList = new ArrayList<>();
    public static DatabaseHelper myDB;


    public LibraryAdapter( Context context){
        this.context = context;
        myDB = new DatabaseHelper(context);
        libraryList = getAllPlaylist(myDB);
    }

    @NonNull
    @Override
    public LibraryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.library_row, parent, false);
        return new LibraryAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryAdapter.MyViewHolder holder, int position) {
        PlaylistModel playlistModel = libraryList.get(position);
        holder.playlistName.setText(playlistModel.getPlaylistName());
        int count = playlistModel.getListSong().size();
        if(count > 1){
            String countStr = count +" songs";
            holder.playlistCount.setText(countStr);
        } else {
            String countStr = count +" song";
            holder.playlistCount.setText(countStr);

        }
        byte [] img = libraryList.get(position).getPlaylistImage();
        if (img != null) {
             Glide.with(context).asBitmap().load(img).into(holder.playlistImg);
        }
        else {
            Glide.with(context).asBitmap().load(R.drawable.default_playlist_image).into(holder.playlistImg);
        }

        int greenColor = android.graphics.Color.parseColor("#1ED760");
        int whiteColor = android.graphics.Color.parseColor("#FFFFFF");

        if(currPlayedPlaylistID!=null&&currPlayedPlaylistID.equals(playlistModel.getPlaylistId()))
        {
            holder.playlistName.setTextColor(greenColor);
        }else {
            holder.playlistName.setTextColor(whiteColor);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String playlistId = libraryList.get(position).getPlaylistId();
                Log.d("this is playlistId", playlistId);
                Intent intent = new Intent(context, PlaylistActivity.class);
                intent.putExtra("playlistId", playlistId);
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDialog(position);
                return false;
            }
        });

        holder.threeDotImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(position);
            }
        });
    }


    public void showDialog(int position) {
        final Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout);
        LinearLayout editLayout = dialog.findViewById(R.id.layoutEdit);
        LinearLayout deleteLayout = dialog.findViewById(R.id.layoutDelete);
        ImageView closeIcon = dialog.findViewById(R.id.layout_close);
        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit(position);
                dialog.dismiss();
            }
        });

        deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePlaylist(position);
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    public void deletePlaylist(int position){
        if(currPlayedPlaylistID!=null&&currPlayedPlaylistID.equals(libraryList.get(position).getPlaylistId())){
            Toast.makeText(context, "Can not delete playing playlist", Toast.LENGTH_SHORT).show();
            notifyItemChanged(position);
            return;
        }
        String playlistName = libraryList.get(position).getPlaylistName();
        AlertDialog alertDialogDelete = new AlertDialog.Builder(context, R.style.MyDialogTheme)
                .setTitle("Delete playlist")
                .setMessage("Are you sure to delete "+playlistName+" ?")
                .setPositiveButton("DELETE", null)
                .setNegativeButton("CANCEL", null)
                .show();
        int colorTextButton = android.graphics.Color.parseColor("#36CB67");
        alertDialogDelete.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(colorTextButton);
        alertDialogDelete.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(colorTextButton);
        Button pos = alertDialogDelete.getButton(AlertDialog.BUTTON_POSITIVE);
        Button neg = alertDialogDelete.getButton(AlertDialog.BUTTON_NEGATIVE);

        neg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogDelete.dismiss();
            }
        });

        pos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaylistModel playlist = libraryList.get(position);
                boolean isDeleted = myDB.DeletePlaylist(playlist);
                if(isDeleted){
                    libraryList.remove(position);
                    notifyItemRemoved(position);
                }
                alertDialogDelete.dismiss();
            };
        });
    }

    public void edit(int position){
        String playlistName = libraryList.get(position).getPlaylistName();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View viewDialog = inflater.inflate(R.layout.add_playlist_dialog, null);
        EditText eName = viewDialog.findViewById(R.id.edt_playlist_name);
        TextView title = viewDialog.findViewById(R.id.dialogTitle);
        title.setText("Edit playlist");
        eName.setHint(playlistName);
        Button btnCancel = viewDialog.findViewById(R.id.btn_cancel);
        Button btnSave = viewDialog.findViewById(R.id.btn_create);
        btnSave.setText("Save");
        builder.setView(viewDialog);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlaylistModel playlist = libraryList.get(position);
                String playlistName = String.valueOf(eName.getText()).trim();
                if(playlistName.equals("")){
                    playlistName=String.valueOf(eName.getHint()).trim();
                }
                PlaylistModel updatePlaylist = new PlaylistModel(playlist.getPlaylistId(), playlistName, playlist.getListSong());
                boolean isUpdated = myDB.UpdatePlaylist(updatePlaylist);
                if(isUpdated){
                    libraryList.set(position, updatePlaylist);
                    notifyItemChanged(position);
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void addPlaylist(PlaylistModel playlist){
        this.libraryList.add(playlist);
    }

    @Override
    public int getItemCount() {
        return libraryList != null ? libraryList.size() : 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView playlistImg, threeDotImg;
        TextView playlistName, playlistCount;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            playlistImg = itemView.findViewById(R.id.img_playlist);
            playlistName = itemView.findViewById(R.id.playlist_name);
            playlistCount = itemView.findViewById(R.id.count);
            threeDotImg = itemView.findViewById(R.id.three_dot);
        }
    }

    public void setLibraryList(){
        if(myDB==null){
            return;
        }
        libraryList = getAllPlaylist(myDB);
    }
}
