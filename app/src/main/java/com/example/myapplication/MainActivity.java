package com.example.myapplication;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

public class MainActivity extends Activity {
    TextView textInfo;
    Button btnCheck;
    Button btnGetContact;
    HidBridge hidBridge;
    ListView list;
    ArrayList mobileArray;
    public static final int REQUEST_READ_CONTACTS = 79;
    private static Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setContext(this);

        this.setupComposer();

    }

    private static void setContext(Context mContext) {
        MainActivity.mContext = mContext;
    }

    public static Context getContext() {
        return MainActivity.mContext;
    }


    //    Function setup interface composer on screen Activity Main
    private void setupComposer() {
        btnCheck = (Button) findViewById(R.id.btCheck);
        btnGetContact = (Button) findViewById(R.id.btGetContact);
        textInfo = (TextView) findViewById(R.id.tvInfo);
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                checkInfo();
            }
        });

        btnGetContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_CONTACTS)
                        == PackageManager.PERMISSION_GRANTED) {
                    mobileArray = getAllContacts();
                } else {
                    requestPermission();
                }
                list = findViewById(R.id.lvContact);
                ArrayAdapter adapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_list_item_1, android.R.id.text1, mobileArray);
                list.setAdapter(adapter);
            }
        });
    }


    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
            // show UI part if you want here to show some rationale !!!

        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mobileArray = getAllContacts();
                } else {
                    // permission denied,Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    private ArrayList getAllContacts() {
        ArrayList<String> nameList = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));
                nameList.add(name);
                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
        return nameList;
    }

    private void checkInfo() {
//        Vibrator v = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
//        } else {
//            //deprecated in API 26
//            v.vibrate(500);
//        }

        HidBridge hidBridge = new HidBridge(this, 390, 1112, textInfo);
        // hidBridge = new HidBridge(this,22352,1155,textInfo);
        hidBridge.OpenDevice();
        hidBridge.StartReadingThread();
    }
}