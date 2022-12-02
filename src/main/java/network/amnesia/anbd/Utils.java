package network.amnesia.anbd;

public class Utils {
    public static String formatTime(long duration) {
        if (duration == Long.MAX_VALUE) {
            return "LIVE";
        }
        long seconds = Math.round(duration / 1000.0);
        long hours = seconds / (60 * 60);
        seconds %= 60 * 60;
        long minutes = seconds / 60;
        seconds %= 60;
        return (hours > 0 ? hours + ":" : "")
                + (minutes < 10 ? "0" + minutes : minutes)
                + ":" + (seconds < 10 ? "0" + seconds : seconds);
    }

    public static long stringToTime(String string) throws NumberFormatException {
        String[] times = string.split(":");

        if (times.length == 1) return Long.parseLong(times[0]) * 1000;
        if (times.length == 2) return (Long.parseLong(times[0]) * 60 + Long.parseLong(times[1])) * 1000;
        return (Long.parseLong(times[0]) * 60 * 60 + Long.parseLong(times[1]) * 60 + Long.parseLong(times[2])) * 1000;
    }

    public static String formatTableForLogging(String title, String[] headers, String[][] data) {
        int longest = Math.max(title.length(), 15);
        for (String header : headers) {
            longest = Math.max(longest, header.length());
        }
        for (String[] dataRow : data) {
            if (headers.length != dataRow.length) throw new IllegalArgumentException("data row should be the same length as headers");

            for (String dataEntry : dataRow) {
                longest = Math.max(longest, dataEntry.length());
            }
        }
        longest += longest % 2;

        String template = String.format("%%-%ss", longest).repeat(headers.length);

        StringBuilder sb = new StringBuilder();
        sb.append(" ".repeat((longest * headers.length - title.length()) / 2)).append(title).append("\n");
        sb.append("=".repeat(longest * headers.length)).append("\n");
        sb.append(String.format(template, (Object[]) headers)).append("\n");
        for (String[] dataRow : data) {
            sb.append(String.format(template, (Object[]) dataRow)).append("\n");
        }
        sb.append("\n");

        return sb.toString();
    }

    @SafeVarargs
    public static <T> T[] arrayOf(T... objects) {
        return objects;
    }
}
