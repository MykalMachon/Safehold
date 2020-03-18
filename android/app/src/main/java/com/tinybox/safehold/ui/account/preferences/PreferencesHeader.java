package com.tinybox.safehold.ui.account.preferences;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.tinybox.safehold.R;

public class PreferencesHeader extends Preference {
    TextView headerTextView;
    public PreferencesHeader(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.setLayoutResource(R.layout.layout_preferences_header);
    }

    public PreferencesHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setLayoutResource(R.layout.layout_preferences_header);
    }

    public PreferencesHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setLayoutResource(R.layout.layout_preferences_header);
    }

    public PreferencesHeader(Context context) {
        super(context);
        this.setLayoutResource(R.layout.layout_preferences_header);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        this.headerTextView = (TextView) holder.findViewById(R.id.name_header_pref);
    }

    public TextView getHeaderTextView() {
        return headerTextView;
    }
}
