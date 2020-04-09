package dev.tinybox.safehold.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.tinybox.safehold.R;

public class AboutActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_about);

    // Set Toolbar at the top of the activity
    Toolbar toolbar = findViewById(R.id.about_toolbar);
    toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp, null));
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle("About SafeHold");
  }
}
