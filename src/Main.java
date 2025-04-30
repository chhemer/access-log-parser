import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.io.File;
import java.util.Map;

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

                System.out.println("Статистика операционных систем:");
                for (Map.Entry<String, Double> entry : stats.getOsStatistics().entrySet()) {
                    System.out.printf("%s: %.2f%%\n", entry.getKey(), entry.getValue() * 100);
                }

                System.out.println("Количество уникальных страниц с кодом 404: " + stats.getMissingPages().size());

                System.out.println("Статистика браузеров:");
                for (Map.Entry<String, Double> entry : stats.getBrowserStatistics().entrySet()) {
                    System.out.printf("%s: %.2f%%%n", entry.getKey(), entry.getValue() * 100);
                }

                System.out.printf("Среднее количество посещений реальными пользователями за час: %.2f%n", stats.getAverageRealVisitsPerHour());
                System.out.printf("Среднее количество ошибочных запросов за час: %.2f%n", stats.getAverageErrorsPerHour());
                System.out.printf("Среднее количество посещений одним пользователем: %.2f%n", stats.getAverageVisitsPerUser());
                System.out.println("Пиковая посещаемость сайта (в секунду): " + stats.getPeakTrafficPerSecond());
                System.out.println("Максимальное количество посещений от одного пользователя: " + stats.getMaxVisitsBySingleUser());

                int totalVisits = stats.getRealUserVisits() + stats.getBotVisits();
                double botPercent = stats.getBotVisitRatio() * 100.0;
                System.out.printf("\n--- Статистика по ботам ---%n");
                System.out.printf("Общее количество посещений: %d%n", totalVisits);
                System.out.printf("Посещений от ботов: %d (%.2f%%)%n", stats.getBotVisits(), botPercent);
                System.out.printf("Посещений от пользователей: %d (%.2f%%)%n", stats.getRealUserVisits(), 100.0 - botPercent);

            } catch (IOException e) {
                System.out.println("Ошибка чтения: " + e.getMessage());
            }
        }
    }
}