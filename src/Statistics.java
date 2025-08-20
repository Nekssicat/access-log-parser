import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;

public class Statistics {
    long totalTraffic;
    LocalDateTime minTime;
    LocalDateTime maxTime;
    HashSet<String> pages;
    HashMap<String, Integer> osStat;


    public Statistics() {
        this.maxTime = null;
        this.minTime = null;
        this.totalTraffic = 0;
        this.pages = new HashSet<>();
        this.osStat = new HashMap<>();
    }

    public void addEntry(LogEntry logEntry) {
        this.totalTraffic += logEntry.getResponseSize();
        LocalDateTime logEntryTime = logEntry.getTime();
        String userAgentOs = logEntry.userAgent.getOs();

        if (this.minTime == null || logEntryTime.isBefore(this.minTime)) {
            this.minTime = logEntryTime;
        }
        if (this.maxTime == null || logEntryTime.isAfter(this.maxTime)) {
            this.maxTime = logEntryTime;
        }
        if (logEntry.getResponseCode() == 200) {
            this.pages.add(logEntry.getRefer());
        }
        if (osStat.get(userAgentOs) == null) {
            osStat.put(userAgentOs, 1);
        } else {
            osStat.put(userAgentOs, osStat.get(userAgentOs) + 1);
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

    public HashSet<String> getPages() {
        return pages;
    }

    public HashMap<String, Double> getOsStat() {
        HashMap<String, Double> osResult = new HashMap<>();
        int totalCount = 0;
        for (int count : osStat.values()) {
            totalCount += count;
        }
        for (HashMap.Entry<String, Integer> os : osStat.entrySet()) {
            double ratio = (double) os.getValue() / totalCount;
            osResult.put(os.getKey(), Math.round(ratio * 1000.0) / 1000.0);
        }
        return osResult;
    }
}
