package com.messes.panorama.bang;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.util.Log;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Random;


public class BangActivity extends Activity {

    private TextView mOutput;
    ArrayList<Integer> contacts;
    ContentResolver cr;
    Cursor cursor;
    private static MySQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bang);
        mOutput = (TextView) findViewById(R.id.output);

        contacts = new ArrayList<Integer>();

        cr = getContentResolver();
        cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        dbHelper = new MySQLiteHelper(this);
        Log.i("BangActivity", "Created MySQLiteHelper object");

        while (cursor.moveToNext()) {
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);

            while (phones.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                number = number.replaceAll("[^\\d.]", "");
                int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                switch (type) {
                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                        // do something with the Home number here...
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                        // do something with the Mobile number here...
                        if (number.length() >= 10) {
                            contacts.add(cursor.getPosition());
                        }
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                        // do something with the Work number here...
                        break;
                }
            }
            phones.close();
        }
    }

    public void bang(View view) {
        Random r = new Random();
        Integer idx = r.nextInt(contacts.size());

        if (!cursor.moveToPosition(contacts.get(idx))) {
            return;
        }

        String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);

        while (phones.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            number = number.replaceAll("[^\\d.]", "");
            int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
            switch (type) {
                case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                    // do something with the Home number here...
                    break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                    // do something with the Mobile number here...
                    String pickup = getPickupLine();
                    sendMessage(pickup, "2032463012");
                    printMessage(name, pickup);
                    break;
                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                    // do something with the Work number here...
                    break;
            }
        }
        phones.close();
    }

    public void share(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "You should go Bang! your friends!");
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, getResources().getText(R.string.send_to)));
    }

    private void sendMessage(String message, String number) {
        try {
            PendingIntent pi = PendingIntent.getBroadcast(BangActivity.this, 0, new Intent("SMS_SENT"), 0);
            SmsManager.getDefault().sendTextMessage(number, null, message, pi, null);
            Log.i("BangActivity", "sendMessage end of try block");
        } catch (Exception e) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(BangActivity.this);
            AlertDialog dialog = alertDialogBuilder.create();
            dialog.setMessage(e.getMessage());
            dialog.show();
        }
    }

    private String getPickupLine() {
        Random r = new Random();
        Log.i("BangActivity", "SIZE IS " + Long.toString(dbHelper.size()));
        long idx = r.nextInt((int) dbHelper.size());

        return dbHelper.getLine(idx);
    }

    private void printMessage(String name, String message) {
        mOutput.setText(name + "\n\n" + message);
        Animation anim = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        anim.setDuration(2000);
        mOutput.startAnimation(anim);
    }
}