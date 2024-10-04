public class TimeConverter {
    public static ClockTime getHourMinutesSecondsFromSeconds(int numberOfSeconds) {
        int hours = numberOfSeconds / 3600;
        int minutes = (numberOfSeconds % 3600) / 60;
        int seconds = numberOfSeconds % 60;

        return new ClockTime(hours, minutes, seconds);
    }

    public static String getTimeString(int hours, int minutes, int seconds) {
        return  String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
