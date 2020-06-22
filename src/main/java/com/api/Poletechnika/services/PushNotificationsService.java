package com.api.Poletechnika.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class PushNotificationsService {

    private static final String FIREBASE_API_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String FIREBASE_SERVER_KEY = "AAAA9v-zdQ4:APA91bEVGoYSgEntWvRToVd3Pf_6m6kW4ua6stq8lBxX6Na0cgaCrD1wb8MUdY7pGqmCs7NPPS0X4J42bwuFqcg0_4PrEmXz9qknsHHqskq-4gAGcHt1Ff03IxCsnG2lpQn0VFytkKJf";

    public static String sendPushNotification(String[] deviceToken, String title, String body, String type) throws IOException {

        String result = "";
        URL url = new URL(FIREBASE_API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setUseCaches(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "key=" + FIREBASE_SERVER_KEY);
        conn.setRequestProperty("Content-Type", "application/json");

        JSONObject json = new JSONObject();

        try {


            //json.put("to", deviceToken.trim());

            JSONArray jsonArray = new JSONArray(deviceToken);

            json.put("registration_ids", new JSONArray(Arrays.asList(deviceToken)));


            JSONObject data = new JSONObject();
            data.put("title", title);
            data.put("body", body);
            data.put("type", type);
            json.put("data", data);
//            JSONObject info = new JSONObject();
//            info.put("title", title); // Notification title
//            info.put("body", body); // Notification
//            info.put("message", "hello user"); // body
//            json.put("notification", info);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        try {
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(json.toString());
            wr.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }
            result = "succcess";
        } catch (Exception e) {
            e.printStackTrace();
            result = "failure";
        }
        System.out.println("GCM Notification is sent successfully");

        return result;

    }
}

