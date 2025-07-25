
import download.Downloader;

public class WgetApplication {

    public void run(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: ./run.sh <url>");
            return;
        }

        Downloader downloader = new Downloader();
        for (String url : args) {
            downloader.downloadFile(url);
        }
    }
}
