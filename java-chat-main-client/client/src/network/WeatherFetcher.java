package network;

import domain.CurrentWeather;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherFetcher {

    private static final String API_KEY = "0596c8d6bacfabd408ab673000296947";
    private static final String CITY_NAME = "Seoul"; 
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    public CurrentWeather getCurrentWeather() throws Exception {

        String urlStr = String.format(
                "%s?q=%s&units=metric&appid=%s",
                BASE_URL, CITY_NAME, API_KEY
        );
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            JSONObject json = new JSONObject(sb.toString());
            JSONArray weatherArray = json.getJSONArray("weather");
            JSONObject weatherObj = weatherArray.getJSONObject(0);

            String description = weatherObj.getString("description");
            String iconCode = weatherObj.getString("icon");

            JSONObject mainObj = json.getJSONObject("main");
            String temperature = String.valueOf(Math.round(mainObj.getDouble("temp")));

            return new CurrentWeather(description, temperature, iconCode);
        }
    }
}
