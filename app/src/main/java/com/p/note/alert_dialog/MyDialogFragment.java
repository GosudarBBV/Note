package com.p.note.alert_dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class MyDialogFragment extends DialogFragment {


    DialogInterface.OnClickListener dialogInterfacePB, dialogInterfaceNB;

    public void setDialogInterfacePB(DialogInterface.OnClickListener dialogInterfacePB) {
        this.dialogInterfacePB = dialogInterfacePB;
    }

    public void setDialogInterfaceNB(DialogInterface.OnClickListener dialogInterfaceNB) {
        this.dialogInterfaceNB = dialogInterfaceNB;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String title = "are you sure?";
        String message = "choose note to delete";
        String btn_ok= "Ok";
        String btn_cancel = "Cancel";

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);  // заголовок
        builder.setMessage(message); // сообщение
        builder.setPositiveButton(btn_ok, dialogInterfacePB);
        builder.setNegativeButton(btn_cancel,  dialogInterfaceNB);

        builder.setCancelable(true);

        return builder.create();

    }
}
