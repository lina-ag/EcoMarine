package tn.edu.esprit.api;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CalendarService {

    private OkHttpClient client = new OkHttpClient();

    public String getEvents() {
        String jsonData = "";
        try {
            Request request = new Request.Builder()
                    .url("https://calendarific.com/api/v2/holidays?api_key=LeyhzQy2tc6siJTYJ7w7NspdeMGCm6A4bW&country=FR&year=2025") // remplace par ton API
                    .header("Authorization", "Bearer TON_TOKEN") 
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    jsonData = response.body().string();
                } else {
                    System.out.println("API : " + response.code());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonData;
    }
}