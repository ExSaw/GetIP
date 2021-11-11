package com.rickrip.myapplication;

// uses android:usesCleartextTraffic="true" in Manifest

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {

    public TextView myTextView;
    private Button myButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // disable night theme

        myButton = findViewById(R.id.button_one);
        myTextView = findViewById(R.id.textView_one);

        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyAsyncTask myAsyncTask = new MyAsyncTask();
                myAsyncTask.execute();
            }
        });


    }


    public class MyAsyncTask extends AsyncTask<String, String, String> {

        final String myUrl = "http://awstest-balancer-1233234915.us-east-2.elb.amazonaws.com/awstest-service/";
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //displaying progress
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Getting your IP from Server");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            String value = "";

            // Create URL
            URL myEndpoint = null;
            try {
                myEndpoint = new URL(myUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            // Create connection
            try {
                assert myEndpoint != null;
                HttpURLConnection myConnection =
                        (HttpURLConnection) myEndpoint.openConnection();

                if (myConnection.getResponseCode() == 200) { //timeout for requests
                    // if success
                    InputStream responseBody = myConnection.getInputStream();
                    InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");

                    JsonReader jsonReader = new JsonReader(responseBodyReader);

                    jsonReader.beginObject(); // Start processing the JSON object
                    while (jsonReader.hasNext()) { // Loop through all keys
                        String key = jsonReader.nextName(); // Fetch the next key
                        if (key.equals("ip")) { // Check if desired key
                            // Fetch the value as a String
                            value = jsonReader.nextString();

                            System.out.println(value);

                            break; // Break out of the loop
                        } else {
                            jsonReader.skipValue(); // Skip values of other keys
                        }
                    }

                    jsonReader.close();
                    myConnection.disconnect();


                } else {
                    //error connection
                    System.out.println("Error connection timeout");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return value;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            myTextView.setText(s);
        }
    }
}