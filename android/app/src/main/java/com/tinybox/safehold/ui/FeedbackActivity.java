package com.tinybox.safehold.ui;

import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;

import com.tinybox.safehold.R;
import com.tinybox.safehold.utility.FormActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class FeedbackActivity extends FormActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_feedback);

    // Set Toolbar at the top of the activity
    Toolbar toolbar = findViewById(R.id.feedback_toolbar);
    toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp,null));
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle("Send Feedback");

    Button submitButton = (Button) findViewById(R.id.send_feedback);
    submitButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        JSONObject data = createJsonObject();
        POSTFormToServer(data, "https://v2-api.sheety.co/mykal/safeholdApi/feedback");
        onBackPressed();
      }
    });
  }

  private JSONObject createJsonObject(){
    MultiAutoCompleteTextView emailField = (MultiAutoCompleteTextView) findViewById(R.id.feedback_answer_one);
    MultiAutoCompleteTextView feedbackField = (MultiAutoCompleteTextView) findViewById(R.id.feedback_answer_two);
    JSONObject root = new JSONObject();
    JSONObject internals = new JSONObject();
    try{
      internals.put("createTime", new Date().toString());
      internals.put("email",  emailField.getText());
      internals.put("feedback", feedbackField.getText());
      root.put("feedback", internals);
    }catch(JSONException e){
      Log.d("error", "JSON error in submitting feedback");
    }
    return root;
  }

  public boolean onSupportNavigateUp() {
    onBackPressed();
    return true;
  }
}
