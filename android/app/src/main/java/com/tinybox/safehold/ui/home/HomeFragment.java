package com.tinybox.safehold.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.AndroidResources;

import com.tinybox.safehold.R;
import com.tinybox.safehold.receivers.DeviceAdmin;
import com.tinybox.safehold.ui.map.PermissionUtils;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private View fragmentView;
    ComponentName compName;
    DevicePolicyManager deviceManger;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 11;
    private static final int CONTACT_PERMISSION_REQUEST_CODE = 12;
    private static final int LOCK_PERMISSION_REQUEST_CODE = 13;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        fragmentView = root;

        deviceManger = (DevicePolicyManager) getActivity().getSystemService(Context.DEVICE_POLICY_SERVICE );
        compName = new ComponentName( getContext(), DeviceAdmin.class ) ;

        checkPermissions();
        setOnClickListenersForPermissionsScreen();


        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setOnClickListenerForSafeHoldButton();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setOnClickListenerForSafeHoldButton(){
        final Button holdButton = (Button) getView().findViewById(R.id.hold_button);
        holdButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        holdButton.setText("ABORT");
                        holdButton.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                        return true; // if you want to handle the touch event
                    case MotionEvent.ACTION_UP:
                        deviceManger.lockNow();
                        return true; // if you want to handle the touch event
                }
                return false;
            }

        });




    }


    public void setOnClickListenersForPermissionsScreen(){
        fragmentView.findViewById(R.id.bvGrantContactPermission).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions(new String[] {android.Manifest.permission.READ_CONTACTS}, CONTACT_PERMISSION_REQUEST_CODE);

            }
        });

        fragmentView.findViewById(R.id.bvGrantLockPermission).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN ) ;
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN , compName ) ;
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION , "In order for Safe Hold to work properly, the app should be able to lock the device when hold button is released." ) ;
                startActivityForResult(intent , LOCK_PERMISSION_REQUEST_CODE ) ;
            }
        });

        fragmentView.findViewById(R.id.bvGrantLocationPermission).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissions(new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
        });
    }



    public boolean[] checkPermissions(){
        boolean atleastOnePermissionNotGranted = false;
        boolean[] results = new boolean[3];
        String[] permissions = {Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_FINE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            results[0] = true;
            ((ImageView)fragmentView.findViewById(R.id.contact_permission_status_indicator)).setImageDrawable(getActivity().getDrawable(R.drawable.ic_check_green_24dp));
            fragmentView.findViewById(R.id.bvGrantContactPermission).setVisibility(View.INVISIBLE);
        }
        else {
            results[0] = false;
            atleastOnePermissionNotGranted = true;
            ((ImageView)fragmentView.findViewById(R.id.contact_permission_status_indicator)).setImageDrawable(getActivity().getDrawable(R.drawable.ic_cross_red_24dp));
            fragmentView.findViewById(R.id.bvGrantContactPermission).setVisibility(View.VISIBLE);
            //((Button)fragmentView.findViewById(R.id.bvGrantContactPermission)).setEnabled(true);
        }
        if (ContextCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            results[1]=true;
            ((ImageView)fragmentView.findViewById(R.id.location_permission_status_indicator)).setImageDrawable(getActivity().getDrawable(R.drawable.ic_check_green_24dp));
            fragmentView.findViewById(R.id.bvGrantLocationPermission).setVisibility(View.INVISIBLE);
            //((Button)fragmentView.findViewById(R.id.bvGrantLocationPermission)).setEnabled(false);
        }
        else{
            results[1] = false;
            atleastOnePermissionNotGranted = true;
            ((ImageView)fragmentView.findViewById(R.id.location_permission_status_indicator)).setImageDrawable(getActivity().getDrawable(R.drawable.ic_cross_red_24dp));
            fragmentView.findViewById(R.id.bvGrantLocationPermission).setVisibility(View.VISIBLE);
            //((Button)fragmentView.findViewById(R.id.bvGrantLocationPermission)).setEnabled(true);
        }

        deviceManger = (DevicePolicyManager) getActivity().getSystemService(Context.DEVICE_POLICY_SERVICE ) ;
        compName = new ComponentName( getContext(), DeviceAdmin.class ) ;
        results[2] = deviceManger.isAdminActive(compName) ;

        // if admin rights are granted
        if(results[2]){
            ((ImageView)fragmentView.findViewById(R.id.lock_permission_status_indicator)).setImageDrawable(getActivity().getDrawable(R.drawable.ic_check_green_24dp));
            fragmentView.findViewById(R.id.bvGrantLockPermission).setVisibility(View.INVISIBLE);
        }
        else {
            ((ImageView)fragmentView.findViewById(R.id.lock_permission_status_indicator)).setImageDrawable(getActivity().getDrawable(R.drawable.ic_cross_red_24dp));
            fragmentView.findViewById(R.id.bvGrantLockPermission).setVisibility(View.VISIBLE);
        }





        if(results[2]==false){
            atleastOnePermissionNotGranted = true;
        }

        if(!atleastOnePermissionNotGranted){
            fragmentView.findViewById(R.id.safehold_home_permission_not_granted).setVisibility(View.INVISIBLE);
            fragmentView.findViewById(R.id.safehold_home_permission_granted).setVisibility(View.VISIBLE);
        }
        else {
            fragmentView.findViewById(R.id.safehold_home_permission_not_granted).setVisibility(View.VISIBLE);
            fragmentView.findViewById(R.id.safehold_home_permission_granted).setVisibility(View.INVISIBLE);
        }

        return results;

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        this.checkPermissions();
    }

    @Override
    public void onActivityResult ( int requestCode , int resultCode, @Nullable Intent
            data) {
        super.onActivityResult(requestCode, resultCode, data) ;
        switch (requestCode) {
            case LOCK_PERMISSION_REQUEST_CODE :
                checkPermissions();
        }
    }
}