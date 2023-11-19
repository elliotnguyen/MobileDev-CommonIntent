package com.example.commonintent;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Camera {
    public static String currentPhotoPATH;
    public void openCamera(AppCompatActivity activity, ActivityResultLauncher<Intent> launcher) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            File file = create_imageFile(activity);
            if (file != null){
                Uri photoURI = FileProvider.getUriForFile(
                        activity,
                        "com.example.commonintent.fileprovider",
                        file
                );
                intent.putExtra(MediaStore.EXTRA_OUTPUT , photoURI);
                launcher.launch(intent);
            }
        }catch (ActivityNotFoundException e) {
            Toast.makeText(activity, "ko thay phan mem chup anh nao",
                    Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File create_imageFile(AppCompatActivity activity) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        String file_Name = "JPGE_" + timeStamp + "_";
        File storage = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                file_Name,
                ".jpg",
                storage
        );
        currentPhotoPATH = image.getAbsolutePath();
        return image;
    }
}
