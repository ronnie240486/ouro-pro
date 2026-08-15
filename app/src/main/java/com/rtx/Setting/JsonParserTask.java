package com.rtx.Setting;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes2.dex */
public class JsonParserTask extends AsyncTask<String, Void, String> {
    /* JADX INFO: Access modifiers changed from: protected */
    public String doInBackground(String... params) {
        String url = params[0];
        String jsonData = "";
        try {
            URL apiUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                while (true) {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    response.append(line);
                }
                reader.close();
                jsonData = response.toString();
            } else {
                System.out.println("HTTP GET request failed with response code: " + responseCode);
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonData;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onPostExecute(String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String rtxSetting = jsonObject.getString("RTXSetting");
                String panalData = jsonObject.getString("PanalData");
                Prefs.putString(rtxSetting, panalData);
                System.out.println("RTXSetting: " + rtxSetting);
                System.out.println("PanalData: " + panalData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
