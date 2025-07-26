package wget.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

    /**
     * Get current timestamp formatted as yyyy-MM-dd HH:mm:ss.
     */
    public static String timestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

}
