import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogEntry {
    final String ipAddr;
    final LocalDateTime time;
    final HttpMethod method;
    final int responseCode;
    final int responseSize;
    final String refer;
    final UserAgent userAgent;

    public LogEntry(String line) {
        String regex = "^([\\d.]+)\\s+" +                     // IP (группа 1)
                "(\\S+)\\s+" +                               // Логин (группа 2)
                "(\\S+)\\s+" +                               // Пользователь (группа 3)
                "\\[(.+?)\\]\\s+" +                          // Время (группа 4)
                "\"([A-Z]+)\\s([^\\s?]+)(?:\\?[^\\s]*)?\\s([^\"]+)\"\\s+" + // Метод, путь, протокол (5-7)
                "(\\d+)\\s+" +                               // Код ответа (группа 8)
                "(\\d+)\\s+" +                               // Размер ответа (группа 9)
                "\"([^\"]*)\"\\s+" +                         // Referer (группа 10)
                "\"([^\"]*)\"";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);

        if (matcher.find()) {
            this.userAgent = new UserAgent(matcher.group(11));
            this.refer = matcher.group(10);
            this.responseSize = Integer.parseInt(matcher.group(9));
            this.responseCode = Integer.parseInt(matcher.group(8));
            this.method = HttpMethod.valueOf(matcher.group(5));
            this.time = parseTime(matcher.group(4));
            this.ipAddr = matcher.group(1);
        } else {
            throw new IllegalArgumentException("Неправильный формат строки: " + line);
        }
    }

    private static LocalDateTime parseTime(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.US);
        return LocalDateTime.parse(dateTime, formatter);
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public int getResponseSize() {
        return responseSize;
    }

    public String getRefer() {
        return refer;
    }

    public UserAgent getUserAgent() {
        return userAgent;
    }

    @Override
    public String toString() {
        return "LogEntry{" +
                "ipAddr='" + ipAddr + '\'' +
                ", time=" + time +
                ", method=" + method +
                ", responseCode=" + responseCode +
                ", responseSize=" + responseSize +
                ", refer='" + refer + '\'' +
                ", userAgent='" + userAgent.toString() + '\'' +
                '}';
    }
}
