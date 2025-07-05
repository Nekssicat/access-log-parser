import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        int fileNumber = 0;
        while (true){
            System.out.println("Введите путь к файлу");
            String path = new Scanner(System.in).nextLine();
            File file = new File(path);

            boolean fileExists = file.exists();
            if (!fileExists) {
                System.out.println("Такого файла не существует");
                continue;
            }

            boolean isDirectory = file.isDirectory();
            if (isDirectory) {
                System.out.println("Указана директория, а не файл");
                continue;
            }

            fileNumber++;
            System.out.println("Путь указан верно");
            System.out.println("Это файл номер "+ fileNumber);
        }
    }
}
