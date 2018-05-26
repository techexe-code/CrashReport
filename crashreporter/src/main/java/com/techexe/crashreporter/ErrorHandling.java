package com.techexe.crashreporter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import static com.android.volley.Request.Method.POST;

public class ErrorHandling extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bug_view_activity);

        sendReport();


        findViewById(R.id.btnCrashRestart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
                System.exit(0);
            }
        });
    }
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        System.exit(0);
    }
    private void sendReport(){
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null){

            if (bundle.containsKey("message")){
                sendReport(bundle.getString("message"),bundle.getString("packageName"));
            }

        }
    }
    private void sendReport(String message,String packageName){

        String Url = "http://techexe.com/beta/crashreport/Report.php";

        final HashMap<String,String> asd = new HashMap<>();
        asd.put("message",message);
        asd.put("packageName",packageName);
        StringRequest request = new StringRequest(POST, Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Error Reporting","Done");

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error Reporting","Error");

            }
        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return asd;
            }
        };
        Volley.newRequestQueue(ErrorHandling.this).add(request);
    }

}
