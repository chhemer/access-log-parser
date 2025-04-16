import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                int totalLines = 0;
                int googlebotCount = 0;
                int yandexbotCount = 0;

                while ((line = reader.readLine()) != null) {
                    int length = line.length();
                    if (length > 1024) {
                        throw new LineTooLongException("Обнаружена строка длиннее 1024 символов: " + length);
                    }

                    totalLines++;
                    String[] parts = line.split("\"");
                    if (parts.length >= 6) {
                        String userAgent = parts[5];
                        if (userAgent.contains("Googlebot")) {
                            googlebotCount++;
                        } else if (userAgent.contains("YandexBot")) {
                            yandexbotCount++;
                        }
                    }
                }

                System.out.println("Общее количество строк в файле: " + totalLines);
                System.out.println("Googlebot: " + googlebotCount + " (" +
                        (totalLines > 0 ? (100 * googlebotCount / totalLines) : 0) + "%)");
                System.out.println("YandexBot: " + yandexbotCount + " (" +
                        (totalLines > 0 ? (100 * yandexbotCount / totalLines) : 0) + "%)");

            } catch (IOException e) {
                System.err.println("Ошибка при чтении файла: " + e.getMessage());
            } catch (LineTooLongException e) {
                System.out.println(e.getMessage());
                break;
            }
        }
        scanner.close();
    }
}


