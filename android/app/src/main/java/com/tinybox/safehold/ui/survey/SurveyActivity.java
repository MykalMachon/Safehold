package com.tinybox.safehold.ui.survey;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.tinybox.safehold.R;

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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.tinybox.safehold.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

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
        sendFormDataPost();
        onBackPressed();
      }
    });
  }

  private void sendFormDataPost(){
    MultiAutoCompleteTextView questionOne = findViewById(R.id.Survey_AnswerOne);
    Spinner questionTwo = findViewById(R.id.Survey_AnswerTwo);
    Spinner questionThree = findViewById(R.id.Survey_AnswerThree);

    RequestQueue queue = Volley.newRequestQueue(this);
    String url ="https://v2-api.sheety.co/mykal/safeholdApi/survey";

    try{
      JSONObject rootJson = new JSONObject();
      JSONObject requestJson = new JSONObject();

      Context c = getApplicationContext();

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
      JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, rootJson,
              new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                  Log.d("Verbose", "Survey Post Worked " + response.toString());
                  Toast toast=Toast.makeText(getApplicationContext(),"Survey Sent! Thank you 😊",Toast.LENGTH_SHORT);
                  toast.show();
                }
              },
              new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                  Toast toast=Toast.makeText(getApplicationContext(),"Survey failed to send 😢",Toast.LENGTH_SHORT);
                  toast.show();
                  Log.d("Error", "Survey Post Broke");
                  Log.d("Error", "Survey Post error " + error.toString());
                }
              }
      ){
        // test
      };
      queue.add(jsonRequest);
    }catch(JSONException e){
      Log.d("Error", "Json Exception in sending POST request");
    }
  }

  public boolean onSupportNavigateUp() {
    onBackPressed();
    return true;
  }
}
