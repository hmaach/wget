package download;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Downloader {

    public void downloadFile(String urlStr) {
        try {
            System.out.println("Start at " + now());

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            int status = conn.getResponseCode();
            System.out.println("Status: " + status);
            if (status != 200) return;

            String file = fileName(urlStr);
            System.out.println("Saving to: " + file);

            try (InputStream in = conn.getInputStream(); FileOutputStream out = new FileOutputStream(file)) {
                byte[] buf = new byte[8192];
                int n;
                while ((n = in.read(buf)) != -1) out.write(buf, 0, n);
            }

            System.out.println("Downloaded: " + urlStr);
            System.out.println("Finished at " + now());

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private String now() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    private String fileName(String url) {
        return url.substring(url.lastIndexOf('/') + 1).split("\\?")[0];
    }
}
