import java.time.Duration;
import java.time.LocalDateTime;

public class Statistics {
    long totalTraffic;
    LocalDateTime minTime;
    LocalDateTime maxTime;

    public Statistics() {
        this.maxTime = null;
        this.minTime = null;
        this.totalTraffic = 0;
    }

    public void addEntry(LogEntry logEntry) {
        this.totalTraffic += logEntry.getResponseSize();
        LocalDateTime logEntryTime = logEntry.getTime();
        if (this.minTime == null || logEntryTime.isBefore(this.minTime)) {
            this.minTime = logEntryTime;
        }
        if (this.maxTime == null || logEntryTime.isAfter(this.maxTime)) {
            this.maxTime = logEntryTime;
        }
    }

    public long getTrafficRate() {
        long hours = Duration.between(minTime, maxTime).toHours();
        if (hours != 0) {
            return totalTraffic / hours;
        }
        return totalTraffic;
    }

    @Override
    public String toString() {
        return "totalTraffic=" + totalTraffic +
                ", minTime=" + minTime +
                ", maxTime=" + maxTime + ", часы=" + Duration.between(minTime, maxTime).toHours();
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
