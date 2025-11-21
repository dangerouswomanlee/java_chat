package view.panel;

import org.json.JSONObject;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WeatherPanel extends JPanel {

    private JLabel timeLabel;
    private JLabel tempLabel;
    private JLabel iconLabel;

    private final String API_KEY = "0596c8d6bacfabd408ab673000296947";
    private final String CITY = "Seoul"; 

    public WeatherPanel() {
        setLayout(new FlowLayout(FlowLayout.RIGHT));
        setOpaque(false); 

        timeLabel = new JLabel();
        tempLabel = new JLabel();
        iconLabel = new JLabel();

        add(timeLabel);
        add(iconLabel);
        add(tempLabel);

        updateWeather();
        startClock();
    }

    private void updateWeather() {
        new Thread(() -> {
            try {
                String urlStr = "https://api.openweathermap.org/data/2.5/weather?q="
                        + CITY + "&appid=" + API_KEY + "&units=metric";
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) result.append(line);

                JSONObject json = new JSONObject(result.toString());
                double temp = json.getJSONObject("main").getDouble("temp");
                String iconCode = json.getJSONArray("weather").getJSONObject(0).getString("icon");

                String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
                ImageIcon icon = new ImageIcon(new URL(iconUrl));
                Image img = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                iconLabel.setIcon(new ImageIcon(img));

                tempLabel.setText(String.format("%.1f°C", temp));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void startClock() {
        new Timer(1000, e -> {
            String currentTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
            timeLabel.setText(currentTime + "  ");
        }).start();
    }
}
