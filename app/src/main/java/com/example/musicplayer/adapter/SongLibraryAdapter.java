//package com.example.musicplayer.adapter;
//
//import android.app.AlertDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.musicplayer.R;
//import com.example.musicplayer.model.LibraryModel;
//
//import java.util.ArrayList;
//
//public class SongLibraryAdapter extends RecyclerView.Adapter<SongLibraryAdapter.ViewHolder> {
//    Context context;
//    View view;
//    ArrayList<LibraryModel> mangbaihatthuvienplaylist;
//    public SongLibraryAdapter(Context context, ArrayList<LibraryModel> mangbaihatthuvienplaylist) {
//        this.context = context;
//        this.mangbaihatthuvienplaylist = mangbaihatthuvienplaylist;
//    }
//    @NonNull
//    @Override
//    public SongLibraryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(context);
//        view = inflater.inflate(R.layout.fragment_song, parent, false);
//        return new SongLibraryAdapter.ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull SongLibraryAdapter.ViewHolder holder, int position) {
//        LibraryModel baiHatThuVienPlayList = mangbaihatthuvienplaylist.get(position);
//        holder.txttenbaihat.setText(baiHatThuVienPlayList.getTitle());
//        holder.txttencasi.setText(baiHatThuVienPlayList.getArtist());
//
//        view.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                AlertDialog alertDialog = new AlertDialog.Builder(context)
//                        .setTitle("Xóa bài hát")
//                        .setMessage("Bạn có muốn xóa bài hát "+baiHatThuVienPlayList.getTitle()+" ?")
//                        .setPositiveButton("Xóa", null)
//                        .setNegativeButton("Hủy", null)
//                        .show();
//
//                Button pos = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
//                Button neg = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
//                pos.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        deletemotbaihatthuvien(baiHatThuVienPlayList.getIdBaiHatThuVienPlayList());
//                        mangbaihatthuvienplaylist.remove(position);
//                        if (mangbaihatthuvienplaylist.size() <= 0){
//                            UpdateHinhThuVien(baiHatThuVienPlayList.getIdThuVienPlayList(), "https://music4b.000webhostapp.com/icon_thuvien.jpg");
//                        }else {
//                            if (position == mangbaihatthuvienplaylist.size()){
//                                UpdateHinhThuVien(baiHatThuVienPlayList.getIdThuVienPlayList(), mangbaihatthuvienplaylist.get(mangbaihatthuvienplaylist.size()-1).getHinhBaiHat());
//                            }
//                        }
//                        alertDialog.dismiss();
//                    }
//                });
//                neg.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        alertDialog.dismiss();
//                    }
//                });
//                return false;
//            }
//        });
//    }
//
//    private void UpdateHinhThuVien(int idThuVienPlayList, String url) {
//    }
//
//    private void deletemotbaihatthuvien(int idBaiHatThuVienPlayList) {
//
//    }
//
//    @Override
//    public int getItemCount() {
//        return mangbaihatthuvienplaylist.size();
//    }
////    public class ViewHolder extends RecyclerView.ViewHolder {
////        TextView txttenbaihat, txttencasi;
////        ImageView hinhbaihat, tim;
////        public ViewHolder(@NonNull View itemView) {
////            super(itemView);
////            txttenbaihat = itemView.findViewById(R.id.textViewtenbaihat);
////            txttencasi = itemView.findViewById(R.id.textViewtencasi);
////            hinhbaihat = itemView.findViewById(R.id.imageViewhinhbaihat);
////            tim = itemView.findViewById(R.id.imageViewtimdanhsachbaihat);
////
////            itemView.setOnClickListener(new View.OnClickListener() {
////                @Override
////                public void onClick(View view) {
////                    Intent intent = new Intent(context, PlayNhacActivity.class);
////                    intent.putExtra("cakhucthuvien", mangbaihatthuvienplaylist.get(getAdapterPosition()));
////                    context.startActivity(intent);
////
////                }
////            });
////        }
////    }
//}
