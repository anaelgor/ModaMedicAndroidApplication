package View.ViewUtils;

public class DateUtils {

    public static long changeDateTo00AM(long dateInMS) {
        return (dateInMS / 10000) * 10000;
    }
}
