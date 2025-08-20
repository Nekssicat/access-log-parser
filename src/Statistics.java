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
    HashSet<String> nonExistPages;
    HashMap<String, Integer> browserStat;


    public Statistics() {
        this.maxTime = null;
        this.minTime = null;
        this.totalTraffic = 0;
        this.pages = new HashSet<>();
        this.osStat = new HashMap<>();
        this.nonExistPages = new HashSet<>();
        this.browserStat = new HashMap<>();
    }

    public void addEntry(LogEntry logEntry) {
        this.totalTraffic += logEntry.getResponseSize();
        LocalDateTime logEntryTime = logEntry.getTime();
        String userAgentOs = logEntry.userAgent.getOs();
        String userAgentBrowser = logEntry.userAgent.getBrowser();

        if (this.minTime == null || logEntryTime.isBefore(this.minTime)) {
            this.minTime = logEntryTime;
        }
        if (this.maxTime == null || logEntryTime.isAfter(this.maxTime)) {
            this.maxTime = logEntryTime;
        }
        if (logEntry.getResponseCode() == 200) {
            this.pages.add(logEntry.getRefer());
        } else if (logEntry.getResponseCode() == 404) {
            this.nonExistPages.add(logEntry.getRefer());
        }
        if (osStat.get(userAgentOs) == null) {
            osStat.put(userAgentOs, 1);
        } else {
            osStat.put(userAgentOs, osStat.get(userAgentOs) + 1);
        }
        if (browserStat.get(userAgentBrowser) == null) {
            browserStat.put(userAgentBrowser, 1);
        } else {
            browserStat.put(userAgentBrowser, browserStat.get(userAgentBrowser) + 1);
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

    public HashSet<String> getNonExistPages() {
        return nonExistPages;
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

    public HashMap<String, Double> getBrowserStat() {
        HashMap<String, Double> browserResult = new HashMap<>();
        int totalCount = 0;
        for (int count : browserStat.values()) {
            totalCount += count;
        }
        for (HashMap.Entry<String, Integer> browser : browserStat.entrySet()) {
            double ratio = (double) browser.getValue() / totalCount;
            browserResult.put(browser.getKey(), Math.round(ratio * 1000.0) / 1000.0);
        }
        return browserResult;
    }
}
