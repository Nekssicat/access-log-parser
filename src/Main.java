import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.round;

public class Main {
    public static void main(String[] args) {
        System.out.println("Введите путь к файлу");
        String path = new Scanner(System.in).nextLine();
//        String path = "C:\\Users\\HomeLaptop\\Downloads\\access.log";
        File file = new File(path);

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
                String botFinder = userAgentFinder(line);
                if (botFinder.equals("Googlebot")) GoogleBotCounter++;
                if (botFinder.equals("YandexBot")) YandexBotCounter++;
                totalLines++;
            }
            System.out.println("Общее количество строк в файле = " + totalLines);
            System.out.println("Доля запросов от YandexBot = " + round((double) YandexBotCounter / totalLines * 100) + "%");
            System.out.println("Доля запросов от Googlebot = " + round((double) GoogleBotCounter / totalLines * 100) + "%");
        } catch (FileNotFoundException ex) {
            System.out.println("Файл не найден: " + ex);
        } catch (IOException ex) {
            System.out.println("Ошибка чтения файла" + ex);
        } catch (LongLineException ex) {
            System.out.println("Ошибка: " + ex.getMessage());
        }
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
