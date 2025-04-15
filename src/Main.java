import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;
import java.io.File;

public class Main {

    public static void main(String[] args) {
        int fileCount = 0;
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Введите путь к файлу:");
            String path = scanner.nextLine();

            File file = new File(path);
            if (!file.exists() || file.isDirectory()) {
                System.out.println("Указанный файл не существует или путь является папкой. Повторите ввод.");
                continue;
            }

            System.out.println("Путь указан верно");
            fileCount++;
            System.out.println("Это файл номер " + fileCount);

            try {
                FileReader fileReader = new FileReader(path);
                BufferedReader reader = new BufferedReader(fileReader);

                int totalLines = 0;
                int maxLength = Integer.MIN_VALUE;
                int minLength = Integer.MAX_VALUE;

                String line;
                while ((line = reader.readLine()) != null) {
                    int length = line.length();
                    if (length > 1024) {
                        throw new LineTooLongException("Обнаружена строка длиннее 1024 символов: " + length);
                    }

                    totalLines++;
                    if (length > maxLength) maxLength = length;
                    if (length < minLength) minLength = length;
                }

                reader.close();

                System.out.println("Общее количество строк в файле: " + totalLines);
                System.out.println("Длина самой длинной строки: " + maxLength);
                System.out.println("Длина самой короткой строки: " + minLength);

            } catch (LineTooLongException e) {
                System.out.println("Ошибка: " + e.getMessage());
                break;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        scanner.close();
    }
}
