package org.runebot;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
public class DownloadUtils {
    public static void downloadFile(String url, String path, LoadingWindow loadingWindow) {
        System.out.println("Downloading " + url + " to " + path);

        try {
            URL downloadURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) downloadURL.openConnection();
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                int contentLength = connection.getContentLength();
                BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
                FileOutputStream out = new FileOutputStream(path);

                byte[] buffer = new byte[1024];
                int bytesRead;
                int totalBytesRead = 0;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                    loadingWindow.setProgress((int) ((totalBytesRead * 100) / contentLength));
                    loadingWindow.setStatusText("Downloading RuneBot Plugin: " + (totalBytesRead * 100) / contentLength + "%");
                    // Update progress here if needed
                    // For example, you can calculate the percentage of completion based on totalBytesRead and contentLength
                }

                out.close();
                in.close();
                System.out.println("Download completed successfully.");
            } else {
                System.out.println("Failed to download. HTTP response code: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error occurred during download: " + e.getMessage());
        }
    }
}
