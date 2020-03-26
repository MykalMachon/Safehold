package com.tinybox.safehold;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.tinybox.safehold.data.Contact;
import com.tinybox.safehold.data.EmergencyContactDataHandler;
import com.tinybox.safehold.ui.home.HomeFragment;
import com.tinybox.safehold.ui.map.PermissionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

// code for this class was derived from https://deepshikhapuri.wordpress.com/2016/11/07/android-countdown-timer-run-in-background/
public class TimerService extends Service {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 11;

    public static String str_receiver = "com.tinybox.receiver";


    private Handler mHandler;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    String strDate;
    Date date_current, date_diff;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPrefEditor;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Timer timer = null;
    public static Timer timer1 = null;
    public static final long NOTIFY_INTERVAL = 1000;
    public static final long NOTIFY_LIVE_TRACKER = 1000 * 60;
    Intent intent;

    private double latitude;
    private double longitude;
    boolean tracking_switch;

    List<Long> contactIDs;
    private List<Contact> contactList;
    private EmergencyContactDataHandler contactDataHandler;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public static final String CHANNEL_ID = "ForegroundServiceChannel";

    //// code for this method was derived from https://androidwave.com/foreground-service-android-example/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        enableMyLocation();
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, HomeFragment.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("SafeHold is running")
                .setContentText("Your location is shared to your emergency contact")
                .setSmallIcon(R.drawable.ic_my_location_white_24dp)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);

        return START_STICKY;
    }

    //// code for this method was derived from https://androidwave.com/foreground-service-android-example/
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        sharedPrefEditor = sharedPreferences.edit();
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 5, NOTIFY_INTERVAL);
        mHandler = new Handler();
        timer1 = new Timer();
        intent = new Intent(str_receiver);

        contactDataHandler = new EmergencyContactDataHandler(getApplication());
        contactIDs = contactDataHandler.getContactIDs();
        this.contactList = getContacts(contactIDs);

    }
    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    public void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission((FragmentActivity) getApplicationContext(), LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else {
            locationManager = (LocationManager) getApplicationContext().getSystemService(getApplicationContext().LOCATION_SERVICE);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();
                    setLongitude(longitude);
                    setLatitude(latitude);
                    Log.d("Coordinates", "Timing: " + "Latitude: " + getLatitude() + " Longitude: " + getLongitude());

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {

                @Override
                public void run() {

                    calendar = Calendar.getInstance();
                    simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                    strDate = simpleDateFormat.format(calendar.getTime());
                    Log.e("strDate", strDate);
                    twoDatesBetweenTime();

                }

            });
        }

    }

    class LiveTracker extends TimerTask {
        LiveTracker() {

        }

        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                  sendSMS();
                }
            });
        }

    }

    public String twoDatesBetweenTime() {


        try {
            date_current = simpleDateFormat.parse(strDate);
        } catch (Exception e) {

        }

        try {
            date_diff = simpleDateFormat.parse(sharedPreferences.getString("date_time", ""));
        } catch (Exception e) {

        }

        try {


            long diff = date_current.getTime() - date_diff.getTime();
            int int_hours = Integer.valueOf(sharedPreferences.getInt("config_lock_timeout", 30));


            long int_timer = TimeUnit.SECONDS.toMillis(int_hours);
            long long_hours = int_timer - diff;
            long diffSeconds2 = long_hours / 1000 % 60;
            long diffMinutes2 = long_hours / (60 * 1000) % 60;
            long diffHours2 = long_hours / (60 * 60 * 1000) % 24;


            if (long_hours > 0) {
                String str_testing = ((diffHours2 >= 9) ? diffHours2 : "0" + diffHours2) +
                        ":" + ((diffMinutes2 >= 9) ? diffMinutes2 : "0" + diffMinutes2) +
                        ":" + ((diffSeconds2 >= 9) ? diffSeconds2 : "0" + diffSeconds2);

                Log.e("TIME", str_testing);

                fn_update(str_testing);
            } else {
                sharedPrefEditor.putBoolean("finish", true).commit();

                fn_update("00:00:00");
                Log.wtf("End", "SMS sent");

                // Check if live location is ON if it is ON then send SMS using a timer every minute
                tracking_switch = sharedPreferences.getBoolean("config_resend_alert", false);
                if (tracking_switch == true) {
                    try {
                        if (!contactList.isEmpty()) {
                            timer1.scheduleAtFixedRate(new LiveTracker(), 0, NOTIFY_LIVE_TRACKER);
                        }
                        Toast.makeText(getApplicationContext(), getString(R.string.sms_sent), Toast.LENGTH_LONG).show();


                    }

                    catch (Exception e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.empty_contact_list), Toast.LENGTH_SHORT).show();
                        dialEmergencyServices();
                        timer1.cancel();
                    }
                }

                else {
                    try {
                        if (!contactList.isEmpty()) {
                            sendSMS();
                            Toast.makeText(getApplicationContext(), getString(R.string.sms_sent), Toast.LENGTH_LONG).show();
                        } else
                            throw new Exception();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.empty_contact_list), Toast.LENGTH_SHORT).show();
                        dialEmergencyServices();
                    }
                }
                timer.cancel();
            }
        } catch (Exception e) {
            timer.cancel();
            timer.purge();


        }

        return "";

    }


    public void sendSMS() {
        SmsManager smsManager = SmsManager.getDefault();
        for (Contact contact : contactList) {
            StringBuilder sms_content = new StringBuilder(getString(R.string.safe_hold_alert));
            String message = getString(R.string.sms_content) + " " + getString(R.string.map_link) + getLatitude() + "," + getLongitude();
            sms_content.append(" " + contact.getName());
            sms_content.append(message);
            smsManager.sendTextMessage(contact.getPhoneNumber(), null, sms_content.toString(), null, null);
            Log.d("Contact", "Service: " + contact.getPhoneNumber());
        }
    }

    public void dialEmergencyServices() {
        Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        dialIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dialIntent.setData(Uri.parse("tel:" + getString(R.string.emergency_services)));
        startActivity(dialIntent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        locationManager.removeUpdates(locationListener);
        stopSelf();
        Log.e("Service finish", "Finish");
    }

    private void fn_update(String str_time) {

        intent.putExtra("safehold_timer_time", str_time);
        sendBroadcast(intent);
    }


    public List<Contact> getContacts(List<Long> cIds) {
        List<Contact> contacts = new ArrayList<>();

        for (long id : cIds) {
            Cursor contactLookupCursor = queryPhoneNumbers(id);

            try {
                if (contactLookupCursor.getCount() > 0) {
                    //contactLookupCursor.moveToFirst();
                    contacts.add(new Contact(
                            id,
                            contactLookupCursor.getString(contactLookupCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)),
                            contactLookupCursor.getString(contactLookupCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    ));

                } else {
                    Log.wtf("EXECPTION", "Something went wrong!");
                }
            } catch (Exception e) {
                e.printStackTrace();
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


