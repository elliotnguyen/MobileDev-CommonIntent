package com.example.commonintent;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements TextMessage.HandlerDialogListener {
    static final int REQUEST_CONTACT = 1;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imv_anh);

        Button btnCamera = findViewById(R.id.button_camera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Camera camera = new Camera();
                camera.openCamera(MainActivity.this, cameraActivityResultLauncher);
            }
        });

        Button btnContact = findViewById(R.id.button_contact);
        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSubMenu(view);
            }

            private void showSubMenu(View view) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.sub_menu_contact, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.action_insert_contact) {
                            Contact.insertContact(MainActivity.this);
                        } else if (item.getItemId() == R.id.action_edit_contact) {
                            Toast.makeText(MainActivity.this, "Select Contact", Toast.LENGTH_SHORT).show();
                            Contact.selectContact(MainActivity.this, selectContactResult);
                        } else if (item.getItemId() == R.id.action_select_contact) {
                            Toast.makeText(MainActivity.this, "Edit Contact", Toast.LENGTH_SHORT).show();
                            Contact.selectContact(MainActivity.this, editContactResult);
                        }
                        return true;
                    }
                });

                popupMenu.setGravity(Gravity.END);
                popupMenu.show() ;
            }
        });

        Button btnTextMessage = findViewById(R.id.button_text_messaging);
        btnTextMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //openTextMessageDialog();
                showSubMenu(view);
            }

            private void showSubMenu(View view) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.sub_menu_textmessage, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.action_using_sms) {
                            TextMessage.openTextMessageDialog(MainActivity.this);
                        } else if (item.getItemId() == R.id.action_using_email) {
                            ImageMessage.selectImage(MainActivity.this, selectImageResult);
                        }
                        return true;
                    }
                });

                popupMenu.setGravity(Gravity.END);
                popupMenu.show() ;
            }
        });
    }

    /*private void openTextMessageDialog() {
        TextMessage textMessage = new TextMessage();
        textMessage.show(getSupportFragmentManager(), "Text Message");
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CONTACT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Contact.insertContact(this);
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    ActivityResultLauncher<Intent> selectImageResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @SuppressLint("MissingPermission")
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        if (intent != null) {
                            Uri imageUri = intent.getData();
                            ImageMessage.sendImage(MainActivity.this, imageUri);
                        }
                    }
                }
    });
    ActivityResultLauncher<Intent> selectContactResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @SuppressLint("MissingPermission")
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                    }
                }
    });

    ActivityResultLauncher<Intent> editContactResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @SuppressLint("MissingPermission")
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        Uri contactUri = intent.getData();
                        Contact.editContact(MainActivity.this, contactUri);
                    }
                }
    });

    ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        set_Pic();
                        gallery_Add_img();
                    }
                }
    });

    private void gallery_Add_img(){
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(Camera.currentPhotoPATH);
        Uri uri_Content = Uri.fromFile(file);
        intent.setData(uri_Content);
        this.sendBroadcast(intent);
    }

    private void set_Pic(){
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(Camera.currentPhotoPATH);

        int photoW = options.outWidth;
        int photoH = options.outHeight;

        int scaleFactor = 1;

        options.inJustDecodeBounds = false;
        options.inSampleSize = scaleFactor;
        options.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(Camera.currentPhotoPATH , options);
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public void handle(String phone, String message) {
        if (phone.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, "Please enter phone number and message", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("sms:" + phone));
        intent.putExtra("sms_body", message);
        startActivity(intent);
    }
}