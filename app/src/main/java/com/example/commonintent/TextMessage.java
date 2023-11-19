package com.example.commonintent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public class TextMessage extends DialogFragment {
    private EditText editPhoneNumber;
    private EditText editTextMessage;
    private HandlerDialogListener listener;

    public static void openTextMessageDialog(AppCompatActivity activity) {
        TextMessage textMessage = new TextMessage();
        textMessage.show(activity.getSupportFragmentManager(), "Text Message");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activitiy_main_dialog, null);

        builder.setView(view)
                .setTitle("Text Message")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TextMessage.this.getDialog().cancel();
                    }
                })
                .setPositiveButton("send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String phoneNumber = editPhoneNumber.getText().toString();
                        String textMessage = editTextMessage.getText().toString();
                        listener.handle(phoneNumber, textMessage);
                    }
                });

        editPhoneNumber = view.findViewById(R.id.phone_edit);
        editTextMessage = view.findViewById(R.id.message_edit);

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (HandlerDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    public interface HandlerDialogListener {
        void handle(String username, String password);
    }
}
