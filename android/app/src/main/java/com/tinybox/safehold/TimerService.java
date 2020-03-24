package com.tinybox.safehold;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

// code for this class was derived from https://deepshikhapuri.wordpress.com/2016/11/07/android-countdown-timer-run-in-background/
public class TimerService extends Service {

    public static String str_receiver = "com.tinybox.receiver";

    private Handler mHandler = new Handler();
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    String strDate;
    Date date_current, date_diff;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPrefEditor;

    private Timer timer = null;
    public static final long NOTIFY_INTERVAL = 1000;
    Intent intent;

    private double latitude;
    private double longitude;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
        intent = new Intent(str_receiver);
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
                String str_testing =  ((diffHours2>=9)?diffHours2:"0"+diffHours2)  +
                        ":" + ((diffMinutes2>=9)?diffMinutes2:"0"+diffMinutes2) +
                        ":" + ((diffSeconds2>=9)?diffSeconds2:"0"+diffSeconds2) ;

                Log.e("TIME", str_testing);

                fn_update(str_testing);
            } else {
                sharedPrefEditor.putBoolean("finish", true).commit();

                fn_update("00:00:00");
                Log.wtf("End","SMS sent");

                //TODO: send SMS here
                // Check if live location is ON if it is ON then send SMS using a timer every minute
                latitude = Double.valueOf(sharedPreferences.getString("Latitude", ""));
                longitude = Double.valueOf(sharedPreferences.getString("Longitude", ""));
                Log.d("Timer", "Timeup: " + latitude + "," + longitude);
                String message = getString(R.string.sms_content) + " " + getString(R.string.map_link) + latitude + "," + longitude;
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage("98765", null, message, null, null);
                Toast.makeText(getApplicationContext(), "SMS sent successfully!", Toast.LENGTH_SHORT).show();
//                // else just send once

                timer.cancel();
            }
        }catch (Exception e){
            timer.cancel();
            timer.purge();


        }

        return "";

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();

        //TODO: cancel Live timer for SMS here

        Log.e("Service finish","Finish");
    }

    private void fn_update(String str_time){

        intent.putExtra("safehold_timer_time",str_time);
        sendBroadcast(intent);
    }
}
