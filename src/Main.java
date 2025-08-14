import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Введите путь к файлу");
        String path = new Scanner(System.in).nextLine();
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
            int maxLen = 0;
            int minLen = 1025;
            String line = "";

            while ((line = reader.readLine()) != null) {
                int length = line.length();
                if (line.length() > 1024) {
                    throw new LongLineException(
                            "Строка №" + (totalLines + 1) + " превышает 1024 символа");
                }
                totalLines++;
                maxLen = Math.max(maxLen, length);
                minLen = Math.min(minLen, length);
            }
            System.out.println("Общее количество строк в файле = " + totalLines);
            System.out.println("Длина самой длинной строки в файле = " + maxLen);
            System.out.println("Длина самой короткой строки в файле = " + minLen);
        } catch (FileNotFoundException ex) {
            System.out.println("Файл не найден: " + ex);
        } catch (IOException ex) {
            System.out.println("Ошибка чтения файла" + ex);
        } catch (LongLineException ex) {
            System.out.println("Ошибка: " + ex.getMessage());
        }
    }
}
