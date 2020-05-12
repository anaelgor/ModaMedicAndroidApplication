package Model.Utils;

public class TimeUtils {

    public static long randomTime() {
        long min = -600000;
        long max = 600000;
        return min + (long) (Math.random() * (max - min));
    }
}
