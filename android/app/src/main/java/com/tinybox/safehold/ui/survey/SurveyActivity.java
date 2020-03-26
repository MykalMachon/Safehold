package com.tinybox.safehold.ui.survey;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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

    // Add Click Listener to the Button
    Button sendSurveyButton = (Button) findViewById(R.id.send_survey);
    sendSurveyButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        sendFormDataPost();
        Toast toast=Toast.makeText(getApplicationContext(),"Survey Sent! Thank you 😊",Toast.LENGTH_SHORT);
        toast.show();
        onBackPressed();
      }
    });
  }

  private void sendFormDataPost(){
    TextView questionOne = (TextView) findViewById(R.id.Survey_AnswerOne);
    Spinner questionTwo = (Spinner) findViewById(R.id.Survey_AnswerTwo);
    Spinner questionThree = (Spinner) findViewById(R.id.Survey_AnswerThree);

    RequestQueue queue = Volley.newRequestQueue(this);
    String url ="https://v2-api.sheety.co/mykal/seattleItinerary/locations";

// Request a string response from the provided URL.
    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            new Response.Listener<String>() {
              @Override
              public void onResponse(String response) {
                Log.d("Survey", "Response is: "+ response.substring(0,250));
              }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
              Log.d("error", "Survey request did not work!");
            }
    });

    queue.add(stringRequest);
  }

  public boolean onSupportNavigateUp() {
    onBackPressed();
    return true;
  }
}
