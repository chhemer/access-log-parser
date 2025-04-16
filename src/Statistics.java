import java.time.Duration;
import java.time.LocalDateTime;

public class Statistics {
    private long totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;

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
}
