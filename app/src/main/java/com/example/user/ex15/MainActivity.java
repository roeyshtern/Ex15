package com.example.user.ex15;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.*;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URI;

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
        int per = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS);
        if(per!=PackageManager.PERMISSION_GRANTED)
        {
            requestPermission(REQUEST_READ_CONTACT);
        }
        per = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE);
        if(per!=PackageManager.PERMISSION_GRANTED)
        {
            requestPermission(REQUEST_READ_PERMISSION);
        }
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
                DisplayContactList();
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

            }
            else if(requestCode == REQUEST_READ_CONTACT && resultCode == RESULT_OK
                    && null != data)
            {
                ContactInfo ci = getContactInfo(data.getData());
                TextView fn = (TextView)findViewById(R.id.tvFillFirstName);
                TextView ln = (TextView)findViewById(R.id.TVFillLastName);
                TextView cn = (TextView)findViewById(R.id.tvFillPhone);
                TextView an = (TextView)findViewById(R.id.tvFillAddress);

                fn.setText(ci.firstName);
                ln.setText(ci.lastName);
                cn.setText(ci.cellNumer);
                an.setText(ci.address);
            }
            else {
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
    private ContactInfo getContactInfo(Uri uriContact)
    {
        long contactID = ContentUris.parseId(uriContact);
        ContactInfo ci = new ContactInfo();

        Cursor cursorDetails = getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                new String[]{ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                        ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
                        ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS},
                ContactsContract.Data.CONTACT_ID + " = ?",
                new String[]{Long.toString(contactID)},
                null);
        while (cursorDetails.moveToNext())
        {
            String rowType = cursorDetails.getString(cursorDetails.getColumnIndex(ContactsContract.Data.MIMETYPE));
            switch (rowType)
            {
                case Phone.CONTENT_ITEM_TYPE:
                {
                    int phoneType = cursorDetails.getInt((cursorDetails.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)));
                    ci.cellNumer = cursorDetails.getString(cursorDetails.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    break;
                }
                case StructuredPostal.CONTENT_ITEM_TYPE:
                {
                    if (ci.address == null || ci.address.isEmpty())
                        ci.address = cursorDetails.getString(cursorDetails.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS));
                    break;
                }
                case StructuredName.CONTENT_ITEM_TYPE:
                {
                    if (ci.firstName == null || ci.firstName.isEmpty())
                        ci.firstName = cursorDetails.getString(cursorDetails.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
                    if (ci.lastName == null || ci.firstName.isEmpty())
                        ci.lastName = cursorDetails.getString(cursorDetails.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));

                    break;
                }
            }

        }
        cursorDetails.close();

        return ci;
    }
    private class ContactInfo{
        String cellNumer;
        String firstName;
        String lastName;
        String address;
    }
}
