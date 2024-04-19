package com.example.musicplayer.fragment;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.musicplayer.R;
public class InsertLibraryFragment extends AppCompatDialogFragment{
    ExampleDialogListenerthuvien listener;
    private EditText edttenthuvien;
    private Button btntao;
    private Button btnhuy;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_insertlibrarymodal, null);
        edttenthuvien = view.findViewById(R.id.edtTenthuvien);
        btntao = view.findViewById(R.id.btninsertthuvienplaylist);
        btnhuy= view.findViewById(R.id.btncancelthuvienplaylist);
        builder.setView(view);

        btntao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tenthuvien = edttenthuvien.getText().toString().trim();
                if (tenthuvien.trim().length() < 1 || tenthuvien.trim().length() > 36){
                    Toast.makeText(getActivity(), "Độ dài tên playlist từ 1 -> 36 ký tự", Toast.LENGTH_LONG).show();
                }else {
                    listener.apply(tenthuvien);
                    dismiss();
                }
            }
        });
        btnhuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return builder.create();

    }
    @Override
    public void onAttach(@NonNull Context context) {
        try {
            listener = (ExampleDialogListenerthuvien) getTargetFragment();
        }catch (Exception e){

        }
        super.onAttach(context);
    }

    public interface ExampleDialogListenerthuvien{
        void apply(String tenthuvien);
    }
}
