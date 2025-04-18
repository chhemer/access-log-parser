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

            int totalLines = 0;
            int tooLongLines = 0;
            int skippedLines = 0;
            int googlebotCount = 0;
            int yandexbotCount = 0;

            Statistics stats = new Statistics();

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                int lineNumber = 0;

                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    totalLines++;

                    if (line.length() > 1024) {
                        tooLongLines++;
                        continue;
                    }

                    try {
                        LogEntry entry = new LogEntry(line);
                        stats.addEntry(entry);

                        String ua = entry.getUserAgent();
                        if (ua.contains("Googlebot")) googlebotCount++;
                        if (ua.contains("YandexBot")) yandexbotCount++;

                    } catch (IllegalArgumentException e) {
                        skippedLines++;
                    }
                }

                System.out.println("Всего строк в файле: " + totalLines);
                System.out.println("Строк превышающих 1024 символа: " + tooLongLines);
                System.out.println("Пропущено строк из-за ошибок разбора: " + skippedLines);
                System.out.println("Googlebot: " + googlebotCount + " ("
                        + (totalLines > 0 ? (100 * googlebotCount / totalLines) : 0) + "%)");
                System.out.println("YandexBot: " + yandexbotCount + " ("
                        + (totalLines > 0 ? (100 * yandexbotCount / totalLines) : 0) + "%)");
                System.out.println("Общий трафик: " + stats.getTotalTraffic() + " байт");
                System.out.println("Средний трафик за час: " + stats.getTrafficRate() + " байт/час");

            } catch (IOException e) {
                System.out.println("Ошибка чтения: " + e.getMessage());
            }
        }
    }
}