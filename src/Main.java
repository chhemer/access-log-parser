import java.util.Scanner;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        int fileCount = 0;

        while (true) {
            System.out.println("Введите путь к файлу:");
            String path = new Scanner(System.in).nextLine();
            File file = new File(path);
            boolean fileExists = file.exists();
            boolean isDirectory = file.isDirectory();

            if (!fileExists || isDirectory) {
                System.out.println("Указанный файл не существует или путь является папкой. Повторите ввод.");
            } else {
                System.out.println("Путь указан верно");
                fileCount++;
                System.out.println("Это файл номер " + fileCount);
            }
        }
    }
}