package com.example.commonintent;


import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

public class ImageMessage {
    public static void selectImage(AppCompatActivity activity, ActivityResultLauncher<Intent> launcher) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        launcher.launch(intent);
    }

    public static void sendImage(AppCompatActivity activity, Uri attachment) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, attachment);
        intent.putExtra(Intent.EXTRA_TEXT, "Image to send");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Image Subject");
        activity.startActivity(Intent.createChooser(intent, "Share image"));
    }
}
