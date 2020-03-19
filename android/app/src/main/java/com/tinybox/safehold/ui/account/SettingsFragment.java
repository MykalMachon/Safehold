package com.tinybox.safehold.ui.account;


import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tinybox.safehold.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences preferences;
    public SettingsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_settings, container, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(this.getContext() );

        preferences.registerOnSharedPreferenceChangeListener(this);
        ((TextView)fragmentView.findViewById(R.id.name_header_pref)).setText(getString(R.string.greetings_preference_page, getUserFirstName()));


        return fragmentView;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.contentEquals("user_name"))
            ((TextView) this.getActivity().findViewById(R.id.name_header_pref)).setText(getString(R.string.greetings_preference_page, getUserFirstName()));
    }

    /**
     * Gets the first name of user from the shared preferences
     *
     * @return First Name of the User
     */
    private String getUserFirstName(){
        String name = preferences.getString("user_name",null);
        if(name==null){
            name = "SafeHold User";
        }
        else{
            name = name.split(" ")[0];
        }
        return name;
    }
}
