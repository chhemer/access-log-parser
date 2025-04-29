import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Statistics {
    private long totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private Set<String> pages = new HashSet<>();
    private Set<String> missingPages = new HashSet<>();
    private Map<String, Integer> osCounter = new HashMap<>();
    private Map<String, Integer> browserCounter = new HashMap<>();

    private int realUserVisits = 0;
    private int errorResponses = 0;
    private Set<String> realUserIps = new HashSet<>();

    public Statistics() {
        this.totalTraffic = 0L;
        this.minTime = null;
        this.maxTime = null;
    }

    public void addEntry(LogEntry entry) {
        totalTraffic += entry.getResponseSize();
        LocalDateTime time = entry.getTime();
        if (minTime == null || time.isBefore(minTime)) minTime = time;
        if (maxTime == null || time.isAfter(maxTime)) maxTime = time;

        if (entry.getResponseCode() == 200) {
            pages.add(entry.getUrl());
        }

        if (entry.getResponseCode() == 404) {
            missingPages.add(entry.getUrl());
        }

        String os = entry.getOperatingSystem();
        osCounter.put(os, osCounter.getOrDefault(os, 0) + 1);

        String browser = new UserAgent(entry.getUserAgent()).getBrowser();
        browserCounter.put(browser, browserCounter.getOrDefault(browser, 0) + 1);

        if (!entry.getUserAgent().toLowerCase().contains("bot")) {
            realUserVisits++;
            realUserIps.add(entry.getIpAddr());
        }

        int code = entry.getResponseCode();
        if (code >= 400 && code < 600) {
            errorResponses++;
        }
    }

    public long getTrafficRate() {
        if (minTime == null || maxTime == null || minTime.equals(maxTime)) return 0L;
        long hours = Duration.between(minTime, maxTime).toHours();
        return (hours == 0) ? totalTraffic : totalTraffic / hours;
    }

    public long getTotalTraffic() {
        return totalTraffic;
    }

    public LocalDateTime getMinTime() {
        return minTime;
    }

    public LocalDateTime getMaxTime() {
        return maxTime;
    }

    public Set<String> getPages() {
        return pages;
    }

    public Set<String> getMissingPages() {
        return missingPages;
    }
    public Map<String, Double> getOsStatistics() {
        Map<String, Double> result = new HashMap<>();
        int total = osCounter.values().stream().mapToInt(Integer::intValue).sum();

        for (Map.Entry<String, Integer> entry : osCounter.entrySet()) {
            result.put(entry.getKey(), entry.getValue() / (double) total);
        }
        return result;
    }

    public Map<String, Double> getBrowserStatistics() {
        Map<String, Double> result = new HashMap<>();
        int total = browserCounter.values().stream().mapToInt(Integer::intValue).sum();

        for (Map.Entry<String, Integer> entry : browserCounter.entrySet()) {
            result.put(entry.getKey(), entry.getValue() / (double) total);
        }
        return result;
    }

    public double getAverageRealVisitsPerHour() {
        if (minTime == null || maxTime == null || minTime.equals(maxTime)) return 0.0;
        long hours = Duration.between(minTime, maxTime).toHours();
        return (hours == 0) ? realUserVisits : (double) realUserVisits / hours;
    }

    public double getAverageErrorsPerHour() {
        if (minTime == null || maxTime == null || minTime.equals(maxTime)) return 0.0;
        long hours = Duration.between(minTime, maxTime).toHours();
        return (hours == 0) ? errorResponses : (double) errorResponses / hours;
    }

    public double getAverageVisitsPerUser() {
        return realUserIps.isEmpty() ? 0.0 : (double) realUserVisits / realUserIps.size();
    }
}