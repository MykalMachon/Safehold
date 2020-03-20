package com.tinybox.safehold.ui.account.emergency_contact_preference;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.tinybox.safehold.R;
import com.tinybox.safehold.data.Contact;
import com.tinybox.safehold.data.EmergencyContactDataHandler;
import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class EmergencyContactsActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_PICK_CONTACT = 1;
    public static final int  MAX_PICK_CONTACT= 15;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS =25;
    private static final int CONTACT_PICKER_REQUEST=20;
    private Activity currentActivity;
    private EmergencyContactDataHandler contactDataHandler;
    private EmergencyContactAdapter adapter;
    private List<Contact> contactList;
    List<Long> contactIDs;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contacts);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp,null));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Emergency Contact List");
        contactDataHandler = new EmergencyContactDataHandler(this);

        recyclerView=(RecyclerView)findViewById(R.id.emergency_contact_recycler);
        contactIDs = contactDataHandler.getContactIDs();
        contactList=getContacts(contactIDs);


        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter =new EmergencyContactAdapter(contactList,this.contactDataHandler);
        recyclerView.setAdapter(adapter);

        currentActivity = this;
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // code derived from https://developer.android.com/training/permissions/requesting
                if (ContextCompat.checkSelfPermission(currentActivity,
                        Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(currentActivity,
                            Manifest.permission.READ_CONTACTS)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(currentActivity,
                                new String[]{Manifest.permission.READ_CONTACTS},
                                MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    // code derived from https://github.com/broakenmedia/MultiContactPicker
                    new MultiContactPicker.Builder(EmergencyContactsActivity.this) //Activity/fragment context
                            .hideScrollbar(false) //Optional - default: false
                            .showTrack(true) //Optional - default: true
                            .searchIconColor(Color.WHITE) //Option - default: White
                            .setChoiceMode(MultiContactPicker.CHOICE_MODE_MULTIPLE) //Optional - default: CHOICE_MODE_MULTIPLE
                            .bubbleTextColor(Color.WHITE) //Optional - default: White
                            .theme(R.style.ContactPickerTheme)
                            .setTitleText("Select Contacts") //Optional - default: Select Contacts
                            .setLoadingType(MultiContactPicker.LOAD_ASYNC) //Optional - default LOAD_ASYNC (wait till all loaded vs stream results)
                            .setActivityAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                                    android.R.anim.fade_in,
                                    android.R.anim.fade_out) //Optional - default: No animation overrides
                            .handleColor(ContextCompat.getColor(EmergencyContactsActivity.this, R.color.colorAccent)) //Optional - default: Azure Blue
                            .bubbleColor(ContextCompat.getColor(EmergencyContactsActivity.this, R.color.colorAccent)) //Optional - default: Azure Blue
                            .showPickerForResult(CONTACT_PICKER_REQUEST);

                }


                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //       .setAction("Action", null).show();


            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    Snackbar.make(findViewById(R.id.fab), "Replace with your own action", Snackbar.LENGTH_LONG)
                           .setAction("Action", null).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // code derived from https://github.com/broakenmedia/MultiContactPicker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CONTACT_PICKER_REQUEST){
            if(resultCode == RESULT_OK) {
                List<ContactResult> results = MultiContactPicker.obtainResult(data);
                for(ContactResult result: results){

                    contactDataHandler.addContactWithID(Long.parseLong(result.getContactID()));
                }

                contactIDs = contactDataHandler.getContactIDs();
                contactList=getContacts(contactIDs);
                recyclerView.setAdapter(new EmergencyContactAdapter(contactList,contactDataHandler));

                Snackbar.make(findViewById(R.id.fab), results.size()+" new Contact added!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            } else if(resultCode == RESULT_CANCELED){
                Snackbar.make(findViewById(R.id.fab), "No new Contact added!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            }
        }
    }

    public List<Contact> getContacts(List<Long> cIds){
        List<Contact> contacts = new ArrayList<>();

        for(long id: cIds){
            Cursor contactLookupCursor =  queryPhoneNumbers(id);
            if (contactLookupCursor.getCount()>0){
                //contactLookupCursor.moveToFirst();
                contacts.add(new Contact(
                        id,
                        contactLookupCursor.getString(contactLookupCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)),
                        contactLookupCursor.getString(contactLookupCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                ));

            }
            else{
                Log.wtf("EXECPTION","Something went wrong!");
            }
        }

        return contacts;
    }


    private Cursor queryPhoneNumbers(long contactId) {
        ContentResolver cr = getContentResolver();
        Uri baseUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,
                contactId);
        Uri dataUri = Uri.withAppendedPath(baseUri,
                ContactsContract.Contacts.Data.CONTENT_DIRECTORY);

        Cursor c = cr.query(dataUri, new String[]{ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.IS_SUPER_PRIMARY, ContactsContract.RawContacts.ACCOUNT_TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.LABEL},
                ContactsContract.Data.MIMETYPE + "=?",
                new String[]{ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE}, null);
        if (c != null && c.moveToFirst()) {
            return c;
        }
        return null;
    }

}
