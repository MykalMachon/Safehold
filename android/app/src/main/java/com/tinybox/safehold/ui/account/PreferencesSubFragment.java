package com.tinybox.safehold.ui.account;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.tinybox.safehold.R;
import com.tinybox.safehold.ui.FeedbackActivity;
import com.tinybox.safehold.ui.account.emergency_contact_preference.EmergencyContactsActivity;

public class PreferencesSubFragment extends PreferenceFragmentCompat  {
    Context c;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        c = this.getContext();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
        c = this.getContext();
        bindOnClickListenersForPreferences();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Binds onClickListeners to the Preferences in Other Section
     *
     * OnClickListeners for both the preferences create intent that take user to Tinybox website
     *
     */
    private void bindOnClickListenersForPreferences(){
        findPreference("config_contacts").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(c, EmergencyContactsActivity.class);
                startActivityForResult(i,1);
                return true;
            }
        });

        findPreference("feedback").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent i = new Intent(c, FeedbackActivity.class);
                startActivity(i);
                return true;
            }
        });
        findPreference("about").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //TODO: Replace the URLs with the required
                String url = "https://tinybox.dev/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));

                startActivity(i);
                return true;
            }
        });
    }

}