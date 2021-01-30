package com.example.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends Activity {
    TextView textInfo;
    TextView textContact;
    Button btnCheck;
    Button btnGetContact;
    HidBridge hidBridge;
    ListView list;
    ArrayList<ContactPhone> mobileArray;
    int currentContactIndex = 0;
    ContactPhone currentContact;

    String[] PERMISSIONS = {
            android.Manifest.permission.READ_CONTACTS,
            Manifest.permission.CALL_PHONE,
    };

    private String currentPhone = "0389919130";

    public static final int REQUEST_READ_CONTACTS = 79;
    private static Activity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!hasPermissions(getContext(), PERMISSIONS)) {
            requestPermission();
        }


        setContext(this);

        this.setupComposer();

    }

    private static void setContext(Activity mContext) {
        MainActivity.mContext = mContext;
    }

    public static Activity getContext() {
        return MainActivity.mContext;
    }

    //    Function setup interface composer on screen Activity Main
    private void setupComposer() {
        btnCheck = (Button) findViewById(R.id.btCheck);
        textInfo = (TextView) findViewById(R.id.tvInfo);
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        // Hai
        btnGetContact = (Button) findViewById(R.id.btGetContact);
        textContact = (TextView) findViewById(R.id.tvContact);
        Button btnUpScroll = (Button) findViewById(R.id.btScrollUp);
        Button btnDownScroll = (Button) findViewById(R.id.btScrollDown);

        FloatingActionButton btnFab = (FloatingActionButton) findViewById(R.id.fabCall);

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                checkInfo();
            }
        });

        if (hasPermissions(getContext(), Manifest.permission.READ_CONTACTS)) {
            mobileArray = getAllContacts();

            // Set current contact with contact at first in list
            currentContact = new ContactPhone(mobileArray.get(0).getName(), mobileArray.get(0).getPhone());
            setCurrentContact(0);

        } else {
            requestPermission();
        }

        btnFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasPermissions(getContext(), Manifest.permission.CALL_PHONE)) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + currentContact.getPhone()));
                    startActivity(intent);
                } else {
                    requestPermission();
                }
            }
        });

        btnUpScroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentContactIndex > 0) {
                    currentContactIndex--;
                    setCurrentContact(currentContactIndex);
                }
            }
        });

        btnDownScroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentContactIndex < mobileArray.size()) {
                    currentContactIndex++;
                    setCurrentContact(currentContactIndex);
                }
            }
        });
    }

    private void setCurrentContact(int index) {
        currentContact = mobileArray.get(index);
        textContact.setText(currentContact.getName() + " (" + currentContact.getPhone() + ")");
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void requestPermission() {
        int PERMISSION_ALL = 1;
        ActivityCompat.requestPermissions((Activity) getContext(), PERMISSIONS, PERMISSION_ALL);
    }

    private ArrayList getAllContacts() {
        ArrayList<ContactPhone> ContactList = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));
                String phoneNo = null;
                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }
                    pCur.close();
                }
                if (phoneNo != null) {
                    ContactList.add(new ContactPhone(name, phoneNo));
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
        return ContactList;
    }

    private void checkInfo() {
        Vibrator v = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }

        HidBridge hidBridge = new HidBridge(this, 390, 1112, textInfo);
        // hidBridge = new HidBridge(this,22352,1155,textInfo);
        hidBridge.OpenDevice();
        hidBridge.StartReadingThread();
    }
}