package dev.tinybox.safehold.utility;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public abstract class FormActivity extends AppCompatActivity {

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
    super.onCreate(savedInstanceState, persistentState);
  }

  protected void POSTFormToServer(JSONObject data, String formUrl){
    RequestQueue queue = Volley.newRequestQueue(this);
    JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, formUrl, data,
            new Response.Listener<JSONObject>() {
              @Override
              public void onResponse(JSONObject response) {
                Log.d("Verbose", "Survey Post Worked " + response.toString());
                Toast toast=Toast.makeText(getApplicationContext(),"Response Sent! Thank you 😊",Toast.LENGTH_SHORT);
                toast.show();
              }
            },
            new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {
                Toast toast=Toast.makeText(getApplicationContext(),"Response failed to send 😢",Toast.LENGTH_SHORT);
                toast.show();
                Log.d("Error", "Survey Post Broke");
                Log.d("Error", "Survey Post error " + error.toString());
              }
            }
    ){};
    queue.add(jsonRequest);
  }

  @Override
  public boolean onSupportNavigateUp() {
    onBackPressed();
    return true;
  }
}
