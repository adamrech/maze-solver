package org.example;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class MazeApiClient {

    private static final OkHttpClient client = new OkHttpClient();

    public static MazeConfig fetchConfig() {
        String url = "https://backend-qcf9.onrender.com/fm1/get-render-config";

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {

                String responseData = response.body().string();

                JSONObject json = new JSONObject(responseData);

                MazeConfig config = new MazeConfig();
                config.wallCellColor = MazeConfig.parseColor(json.getString("wallCellColor"));
                config.pathColor = MazeConfig.parseColor(json.getString("pathColor"));
                config.drawGrid = json.getBoolean("drawGrid");
                config.gridColor = MazeConfig.parseColor(json.getString("gridColor"));
                config.animationDelayMs = json.getInt("animationDelayMs");

                return config;
            }
        } catch (Exception e) {
            System.out.println("Error fetching config from server.");
            e.printStackTrace();
        }

        return null;
    }

    public static BufferedImage fetchMazeImage(int width, int height) {
        String url = "https://backend-qcf9.onrender.com/fm1/get-maze-image?width=" + width + "&height=" + height;

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                InputStream inputStream = response.body().byteStream();
                return ImageIO.read(inputStream);
            }
        } catch (Exception e) {
            System.out.println("Error fetching maze image.");
            e.printStackTrace();
        }
        return null;
    }
}