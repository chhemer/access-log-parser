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

    private Map<Long, Integer> secondVisitsCounter = new HashMap<>();
    private Set<String> refererDomains = new HashSet<>();
    private Map<String, Integer> realUserVisitsCounter = new HashMap<>();

    private int realUserVisits = 0;
    private int botVisits = 0;
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

        UserAgent agent = new UserAgent(entry.getUserAgent());
        String browser = agent.getBrowser();
        browserCounter.put(browser, browserCounter.getOrDefault(browser, 0) + 1);

        if (!agent.isBot()) {
            long epochSecond = entry.getTime().atZone(java.time.ZoneId.systemDefault()).toEpochSecond();
            secondVisitsCounter.put(epochSecond, secondVisitsCounter.getOrDefault(epochSecond, 0) + 1);

            realUserVisits++;
            realUserIps.add(entry.getIpAddr());
            realUserVisitsCounter.put(entry.getIpAddr(), realUserVisitsCounter.getOrDefault(entry.getIpAddr(), 0) + 1);
        } else {
            botVisits++;
        }

        int code = entry.getResponseCode();
        if (code >= 400 && code < 600) {
            errorResponses++;
        }

        String referer = entry.getReferer();
        if (referer.startsWith("http")) {
            String domain = extractDomain(referer);
            if (domain != null) {
                refererDomains.add(domain);
            }
        }
    }

    public double getBotVisitRatio() {
        int totalVisits = realUserVisits + botVisits;
        if (totalVisits == 0) return 0.0;
        return (double) botVisits / totalVisits;
    }

    public int getRealUserVisits() {
        return realUserVisits;
    }

    public int getBotVisits() {
        return botVisits;
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
        if (realUserIps.isEmpty()) return 0.0;
        return (double) realUserVisits / realUserIps.size();
    }

    public int getPeakTrafficPerSecond() {
        return secondVisitsCounter.values().stream().mapToInt(Integer::intValue).max().orElse(0);
    }

    public int getPeakTrafficPerMinute() {
        Map<Long, Integer> minuteCounter = new HashMap<>();
        for (Map.Entry<Long, Integer> entry : secondVisitsCounter.entrySet()) {
            long minute = entry.getKey() / 60;
            minuteCounter.put(minute, minuteCounter.getOrDefault(minute, 0) + entry.getValue());
        }
        return minuteCounter.values().stream().mapToInt(Integer::intValue).max().orElse(0);
    }

    public Set<String> getReferringDomains() {
        return refererDomains;
    }

    public int getMaxVisitsBySingleUser() {
        return realUserVisitsCounter.values().stream().mapToInt(Integer::intValue).max().orElse(0);
    }

    private String extractDomain(String referer) {
        try {
            String domain = referer.replaceFirst("^(http[s]?://)?(www\\.)?", "");
            int slashIndex = domain.indexOf('/');
            if (slashIndex != -1) {
                domain = domain.substring(0, slashIndex);
            }
            return domain;
        } catch (Exception e) {
            return null;
        }
    }
}