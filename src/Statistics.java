import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Statistics {
    long totalTraffic;
    LocalDateTime minTime;
    LocalDateTime maxTime;
    long hours;
    HashSet<String> pages;
    HashMap<String, Integer> osStat;
    HashSet<String> nonExistPages;
    HashMap<String, Integer> browserStat;
    ArrayList<Boolean> isBotList;
    long errorResponseCount;
    HashSet<String> humanIP;


    public Statistics() {
        this.maxTime = null;
        this.minTime = null;
        this.hours = 1;
        this.totalTraffic = 0;
        this.pages = new HashSet<>();
        this.osStat = new HashMap<>();
        this.nonExistPages = new HashSet<>();
        this.browserStat = new HashMap<>();
        this.isBotList = new ArrayList<>();
        this.errorResponseCount = 0;
        this.humanIP = new HashSet<>();
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

        if (Duration.between(this.minTime, this.maxTime).toHours() == 0) {
            hours = 1;
        } else {
            this.hours = Duration.between(this.minTime, this.maxTime).toHours();
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

        isBotList.add(logEntry.userAgent.isBot());

        if (logEntry.getResponseCode() >= 400 || logEntry.getResponseCode() < 600) {
            this.errorResponseCount++;
        }

        if (!logEntry.userAgent.isBot()) {
            humanIP.add(logEntry.getIpAddr());
        }
    }

    public long getTrafficRate() {
        return totalTraffic / hours;
    }

    @Override
    public String toString() {
        return "totalTraffic=" + totalTraffic +
                ", minTime=" + minTime +
                ", maxTime=" + maxTime + ", часы=" + hours;
    }

    public HashSet<String> getPages() {
        return pages;
    }

    public HashSet<String> getNonExistPages() {
        return nonExistPages;
    }

    private double threeDecimalRound(double number) {
        return (Math.round(number * 1000.0) / 1000.0);
    }

    public HashMap<String, Double> getOsStat() {
        HashMap<String, Double> osResult = new HashMap<>();
        int totalCount = 0;
        for (int count : osStat.values()) {
            totalCount += count;
        }
        for (HashMap.Entry<String, Integer> os : osStat.entrySet()) {
            double ratio = (double) os.getValue() / totalCount;
            osResult.put(os.getKey(), threeDecimalRound(ratio));
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
            browserResult.put(browser.getKey(), threeDecimalRound(ratio));
        }
        return browserResult;
    }

    public double getAverageHumanTraffic() {
        long humanVisits = isBotList.stream()
                .filter((Boolean isBot) -> !isBot)
                .count();
        return threeDecimalRound((double) humanVisits / hours);
    }

    public double getAverageErrorTraffic() {
        return threeDecimalRound((double) errorResponseCount / hours);
    }

    public double getAverageUniqueHumanTraffic() {
        long uniqueHumanCount = humanIP.stream().count();
        long humanVisitsCount = isBotList.stream()
                .filter((Boolean isBot) -> !isBot)
                .count();
        return threeDecimalRound((double) uniqueHumanCount / humanVisitsCount);
    }

}
