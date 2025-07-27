package wget.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

    public static String timestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
}
