package com.example.drew.homework02;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText textID;
    private Button fetchButton;

    private List<Monopoly> monopolyList = new ArrayList<>();
    private ListView itemsListView;

    private static String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textID = (EditText) findViewById(R.id.id_value);
        fetchButton = (Button) findViewById(R.id.fetchButton);
        itemsListView = (ListView) findViewById(R.id.weatherListView);

        // See comments on this formatter above.
        //numberFormat.setMaximumFractionDigits(0);

        fetchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissKeyboard(textID);
                new GetWeatherTask().execute(createURL(textID.getText().toString()));
            }
        });
    }

    /**
     * Formats a URL for the webservice specified in the string resources.
     * Sends all info or just the one identified
     *
     * @param id the target city
     * @return URL
     */
    private URL createURL(String id) {
        String urlString;
        Log.i("Current ID: ", id);
        try {
            if(id.equals("")){
                urlString = "http://cs262.cs.calvin.edu:8089/monopoly/players";
            }
            else{
                urlString = "http://cs262.cs.calvin.edu:8089/monopoly/player/" + id;
            }
            return new URL(urlString);
        } catch (Exception e) {
            Toast.makeText(this, getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
        }

        return null;
    }

    /**
     * Deitel's method for programmatically dismissing the keyboard.
     *
     * @param view the TextView currently being edited
     */
    private void dismissKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    /**
     * Inner class for GETing the current weather data from openweathermap.org asynchronously
     */
    private class GetWeatherTask extends AsyncTask<URL, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(URL... params) {
            HttpURLConnection connection = null;
            StringBuilder result = new StringBuilder();
            try {
                connection = (HttpURLConnection) params[0].openConnection();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    return new JSONObject(result.toString());
                } else {
                    throw new Exception();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                connection.disconnect();
            }
            return null;
        }
        @Override
        protected void onPostExecute(JSONArray weather) {
            if (weather != null) {
                //Log.d(TAG, weather.toString());
                convertJSONtoArrayList(weather);
                MainActivity.this.updateDisplay();
            } else {
                Toast.makeText(MainActivity.this, "invalid id", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Converts the JSON weather forecast data to an arraylist suitable for a listview adapter
     *
     * @param players
     */
    private void convertJSONtoArrayList(JSONArray players) {
        monopolyList.clear(); // clear old weather data

        for ( int i = 0 ; i < players.length(); i++ )
        {
            try
            {
                JSONObject player = players.getJSONObject(i);
                monopolyList.add(new Monopoly(
                        player.getInt( "id" ),
                        player.getString("email"),
                        player.getString("name")));
            }
            catch (JSONException e) {

                try {
                    JSONObject player = players.getJSONObject(i);
                    monopolyList.add(new Monopoly(
                            player.getInt("id"),
                            player.getString("email"),
                            "no name given"));
                }
                catch(JSONException e2){
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Refresh the weather data on the forecast ListView through a simple adapter
     */
    private void updateDisplay() {
        if (monopolyList == null) {
            Toast.makeText(MainActivity.this, getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
        }
        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
        for (Monopoly item : monopolyList) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("id", Integer.toString( item.getID() ));
            map.put("email", item.getEmail());
            map.put("name", item.getName());
            data.add(map);
        }

        int resource = R.layout.monopoly;
        String[] from = {"day", "description", "min", "max"};
        int[] to = {R.id.numberTextView, R.id.emailTextView, R.id.nameTextView};

        SimpleAdapter adapter = new SimpleAdapter(this, data, resource, from, to);
        itemsListView.setAdapter(adapter);
    }

}