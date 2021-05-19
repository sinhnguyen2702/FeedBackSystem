package com.gaf.project.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.gaf.project.R;
import com.gaf.project.adapter.ClassAdapter;
import com.gaf.project.model.Class;

public class YesNoDialog extends DialogFragment {
    private String message;
    private YesNoDialog.IClickItem iClickItem;
    public interface IClickItem{
        void update();
    }
    public YesNoDialog(YesNoDialog.IClickItem iClickItem,String message) {
        this.iClickItem = iClickItem;
        this.message = message;
    }
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View viewBuilder = LayoutInflater.from(getContext()).inflate(R.layout.warning_dialog, null);

        TextView warningContent = viewBuilder.findViewById(R.id.txt_warning_content);
        warningContent.setText(message);

        Button btnOke = viewBuilder.findViewById(R.id.btn_yes);

        btnOke.setOnClickListener(v->{
            iClickItem.update();
        });

        Button btnCancel = viewBuilder.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(v -> dismiss());
        builder.setView(viewBuilder);

        return builder.create();
    }
}
