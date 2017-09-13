package com.example.user.ex15;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final int RESULT_LOAD_IMG = 12;
    private static final int REQUEST_READ_PERMISSION = 786;
    private static final int REQUEST_READ_CONTACT = 3;

    Button pickImage;
    Button pickContact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission(REQUEST_READ_PERMISSION);
        requestPermission(REQUEST_READ_CONTACT);
        pickImage = (Button)findViewById(R.id.button_img);
        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });
        pickContact = (Button)findViewById(R.id.button_contact);
        pickContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DisplayContactList();
            }
        });
    }

    public void openFilePicker()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMG);
    }
    public void DisplayContactList()
    {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, REQUEST_READ_CONTACT);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                ImageView imgView = (ImageView) findViewById(R.id.imageView);
                // Set the Image in ImageView after decoding the String
                imgView.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_READ_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //
        }
        else if(requestCode ==REQUEST_READ_CONTACT && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            //DisplayContactList();
        }

    }
    private void requestPermission(int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            switch (requestCode)
            {
                case REQUEST_READ_CONTACT:
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACT);
                    break;
                case REQUEST_READ_PERMISSION:
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_PERMISSION);
                    break;
            }
        } else {
            switch (requestCode)
            {
                case REQUEST_READ_PERMISSION:
                    openFilePicker();
                    break;
                case REQUEST_READ_CONTACT:
                    DisplayContactList();
                    break;
            }
        }
    }
}
