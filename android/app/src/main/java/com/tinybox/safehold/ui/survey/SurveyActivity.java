package com.tinybox.safehold.ui.survey;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.tinybox.safehold.R;

public class SurveyActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_survey);

    // Set Toolbar at the top of the activity
    Toolbar toolbar = findViewById(R.id.survey_toolbar);
    toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp,null));
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle("Mind Sharing What Happened?");

    // Set the spinner for question 2
    Spinner qTwoSpinner = (Spinner) findViewById(R.id.Survey_AnswerTwo);
    ArrayAdapter<CharSequence> qTwoAdapter = ArrayAdapter.createFromResource(this, R.array.survey_question_two_options, android.R.layout.simple_spinner_dropdown_item);
    qTwoSpinner.setAdapter(qTwoAdapter);

    // Set the spinner for question 3
    Spinner qThreeSpinner = (Spinner) findViewById(R.id.Survey_AnswerThree);
    ArrayAdapter<CharSequence> qThreeAdapter = ArrayAdapter.createFromResource(this, R.array.survey_question_three_options, android.R.layout.simple_spinner_dropdown_item);
    qThreeSpinner.setAdapter(qThreeAdapter);
  }

  public boolean onSupportNavigateUp() {
    onBackPressed();
    return true;
  }
}
