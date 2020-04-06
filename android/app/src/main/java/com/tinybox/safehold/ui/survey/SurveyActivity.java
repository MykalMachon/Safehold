package com.tinybox.safehold.ui.survey;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;

import androidx.appcompat.widget.Toolbar;

import com.tinybox.safehold.R;
import com.tinybox.safehold.utility.FormActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class SurveyActivity extends FormActivity {

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
    Spinner qTwoSpinner = findViewById(R.id.Survey_AnswerTwo);
    ArrayAdapter<CharSequence> qTwoAdapter = ArrayAdapter.createFromResource(this, R.array.survey_question_two_options, android.R.layout.simple_spinner_dropdown_item);
    qTwoSpinner.setAdapter(qTwoAdapter);

    // Set the spinner for question 3
    Spinner qThreeSpinner = findViewById(R.id.Survey_AnswerThree);
    ArrayAdapter<CharSequence> qThreeAdapter = ArrayAdapter.createFromResource(this, R.array.survey_question_three_options, android.R.layout.simple_spinner_dropdown_item);
    qThreeSpinner.setAdapter(qThreeAdapter);

    // Add Click Listener to the Button
    Button sendSurveyButton = findViewById(R.id.send_survey);
    sendSurveyButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        JSONObject data = createJsonObject();
        POSTFormToServer(data, "https://v2-api.sheety.co/mykal/safeholdApi/survey");
        onBackPressed();
      }
    });
  }

  private JSONObject createJsonObject() {
    JSONObject rootJson = new JSONObject();
    JSONObject requestJson = new JSONObject();
    try{
      MultiAutoCompleteTextView questionOne = findViewById(R.id.Survey_AnswerOne);
      Spinner questionTwo = findViewById(R.id.Survey_AnswerTwo);
      Spinner questionThree = findViewById(R.id.Survey_AnswerThree);
      LocationManager lm = (LocationManager)getSystemService(LOCATION_SERVICE);
      Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
      double longitude = location.getLongitude();
      double latitude = location.getLatitude();
      requestJson.put("createTime", new Date().toString());
      requestJson.put("reasonForEvent", questionOne.getText());
      requestJson.put("areYouSafe", questionTwo.getSelectedItem().toString());
      requestJson.put("wouldYouLikeToShareLocation", questionThree.getSelectedItem().toString());
      if(questionThree.getSelectedItem().toString().compareToIgnoreCase("Yes") == 0){
        requestJson.put("lat", latitude);
        requestJson.put("long", longitude);
      }
      rootJson.put("survey", requestJson);
    }catch(JSONException e){
      Log.d("Error", "JSON Exception in creating form data");
    }
    return rootJson;
  }

  public boolean onSupportNavigateUp() {
    onBackPressed();
    return true;
  }
}
