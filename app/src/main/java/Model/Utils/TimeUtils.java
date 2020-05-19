package Model.Utils;

public class TimeUtils {

    public static long ONE_MINUTE = 1 * 60 * 1000;

    public static long randomTime() {
        long min = -600000;
        long max = 600000;
        return min + (long) (Math.random() * (max - min));
    }
}
