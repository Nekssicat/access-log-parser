import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
//        System.out.println("Введите путь к файлу");
//        String path = new Scanner(System.in).nextLine();
        String path = "C:\\Users\\HomeLaptop\\Downloads\\access.log";
        File file = new File(path);
        List<LogEntry> logLines = new ArrayList<>(191076);
        Statistics stat = new Statistics();

        boolean fileExists = file.exists();
        if (!fileExists) {
            System.out.println("Такого файла не существует");
        }

        boolean isDirectory = file.isDirectory();
        if (isDirectory) {
            System.out.println("Указана директория, а не файл");
        }

        try (FileReader fileReader = new FileReader(path);
             BufferedReader reader = new BufferedReader(fileReader)) {
            int totalLines = 0;
            int GoogleBotCounter = 0;
            int YandexBotCounter = 0;
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.length() > 1024) {
                    throw new LongLineException(
                            "Строка №" + (totalLines + 1) + " превышает 1024 символа");
                }
                logLines.add(new LogEntry(line));

                String botFinder = userAgentFinder(line);
                if (botFinder.equals("Googlebot")) GoogleBotCounter++;
                if (botFinder.equals("YandexBot")) YandexBotCounter++;
                totalLines++;
            }
        } catch (Exception ex) {
            System.out.println("Ошибка: " + ex.getMessage());
        }

        for (LogEntry logEntry : logLines) {
            stat.addEntry(logEntry);
        }
        System.out.println("Статистика:" + stat);
        System.out.println("Объем часового трафика = " + stat.getTrafficRate());
        System.out.println("Страницы:" + stat.getPages());
        System.out.println("Несуществующие страницы:" + stat.getNonExistPages());
        System.out.println("Статистика ОС: " + stat.getOsStat());
        System.out.println("Статистика бразеров: " + stat.getBrowserStat());
        System.out.println("Статистика среднего количества посещений сайта за час: " + stat.getAverageHumanTraffic());
        System.out.println("Статистика среднего количества ошибочных запросов в час: " + stat.getAverageErrorTraffic());
        System.out.println("Статистика средней посещаемости одним пользователем: " + stat.getAverageUniqueHumanTraffic());

//        String test = "72.118.143.231 - - [25/Sep/2022:06:25:06 +0300] \"GET /parliament/november-reports/content/6377/58/?n=13 HTTP/1.0\" 200 8983 \"-\" \"Mozilla/5.0 (Linux; Android 6.0.1; Nexus 5X Build/MMB29P) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.5195.125 Mobile Safari/537.36 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)\"";
//        String test2 = "44.135.240.229 - - [25/Sep/2022:06:25:08 +0300] \"GET /housekeeping/?lg=2&p=506&rss=1&t=2 HTTP/1.0\" 200 1368 \"https://rosinform.ru/rubric/top/maks2015/\" \"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36 Edge/18.18362\"";

    }

    public static String userAgentFinder(String logLine) {
        Pattern pattern = Pattern.compile("\"([^\"]*)\"\\s*$");
        Matcher matcher = pattern.matcher(logLine);
        String userAgentLine = logLine;

        if (matcher.find()) {
            userAgentLine = matcher.group(1);
        } else {
            return "-";
        }

        String result;
        int firstBracket = userAgentLine.lastIndexOf('(');
        int latBracket = userAgentLine.lastIndexOf(')');

        if (firstBracket != -1 && latBracket != -1 && firstBracket < latBracket) {
            String firstBrackets = userAgentLine.substring(firstBracket + 1, latBracket);
            String[] parts = firstBrackets.split(";");

            for (int i = 0; i < parts.length; i++) {
                parts[i] = parts[i].trim();
            }

            if (parts.length >= 2) {
                String fragment = parts[1];
                result = fragment;
                int slashIndex = fragment.indexOf('/');
                if (slashIndex > 0) {
                    result = fragment.substring(0, slashIndex);
                }
            } else {
                return "-";
            }
        } else {
            return "-";
        }
        return result;
    }
}
