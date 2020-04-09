package dev.tinybox.safehold.ui.account.custom_preferences;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.SeekBarPreference;

public class TimerDurationSeekBar extends SeekBarPreference {
    public TimerDurationSeekBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        int maxSeconds = attrs.getAttributeIntValue("http://schemas.android.com/apk/res-auto","max",100);
        this.setMax(maxSeconds);
        this.setUpdatesContinuously(true);

    }

    public TimerDurationSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        int maxSeconds = attrs.getAttributeIntValue("http://schemas.android.com/apk/res-auto","max",100);
        this.setMax(maxSeconds);
        this.setUpdatesContinuously(true);

    }

    public TimerDurationSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        int maxSeconds = attrs.getAttributeIntValue("http://schemas.android.com/apk/res-auto","max",100);
        this.setMax(maxSeconds);
        this.setUpdatesContinuously(true);
    }

    public TimerDurationSeekBar(Context context) {
        super(context);
    }


}
