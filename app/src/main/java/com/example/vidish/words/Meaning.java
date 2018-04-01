package com.example.vidish.words;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Vidish on 01-04-2018.
 */

public class Meaning extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String[] strings) {
        if(strings.length == 0)
            return null;
        final String app_id = "8c98d8e3";
        final String app_key = "6e4bebbbcb2ab2f03f702f8653c005e9";
        try {
            URL url = new URL("https://od-api.oxforddictionaries.com:443/api/v1/entries/en/" +strings[0].toLowerCase());
            HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Accept","application/json");
            urlConnection.setRequestProperty("app_id",app_id);
            urlConnection.setRequestProperty("app_key",app_key);

            // read the output from the server
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }

            return stringBuilder.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            return e.toString();
        }
    }

    @Override
    protected void onPostExecute(String s) {
        try {
            JSONArray results = new JSONObject(s).getJSONArray("results");
            for (int i = 0; i < results.length(); i++)
            {
                JSONArray lexicalEntries = results.getJSONObject(i).getJSONArray("lexicalEntries");
                for (int j=0; j<lexicalEntries.length();j++)
                {
                    JSONArray entries = lexicalEntries.getJSONObject(j).getJSONArray("entries");
                    String lexicalCategory = lexicalEntries.getJSONObject(j).getString("lexicalCategory");
                    String audio = lexicalEntries.getJSONObject(j).getJSONArray("pronunciations").getJSONObject(0).getString("audioFile");
                    Log.e("^^^^^^^^^^^^^^^^", audio);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
