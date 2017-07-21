package com.theCreatorLabs.SampleFacebook.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.FacebookSdk;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by User2 on 21-07-2017.
 */

public class InsertActivity extends AppCompatActivity {
    private EditText editTextName;
    private EditText editTextAdd;
    Button btnInsert;
    TextView response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert);
        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextAdd = (EditText) findViewById(R.id.editTextAddress);
        btnInsert = (Button) findViewById(R.id.button);
        response = (TextView) findViewById(R.id.textViewResult);
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(InsertActivity.this);
                //this is the url where you want to send the request
                String url = "http://akshathkotegar.website/Notification/notify.php";
                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the response string.
                                InsertActivity.this.response.setText(response);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        response.setText("That didn't work!");
                    }
                }) {
                    //adding parameters to the request
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("name", editTextName.getText().toString());
                        params.put("address", editTextAdd.getText().toString());
                        return params;
                    }
                };
                // Add the request to the RequestQueue.
                queue.add(stringRequest);
            }
        });
    }
}
/*
<?php
  define('HOST','mysql.hostinger.in');
  define('USER','u813815354_user');
  define('PASS','bhaq2010');
  define('DB','u813815354_db');
  $con = mysqli_connect(HOST,USER,PASS,DB);
  $name = $_POST['name'];
  $address = $_POST['address'];
  $sql = "insert into Persons (name,address) values ('$name','$address')";
  if(mysqli_query($con,$sql)){
    echo 'success';
  }
  else{
    echo 'failure';
  }
  mysqli_close($con);
?>
*/