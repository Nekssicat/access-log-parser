import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

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
    HashSet<String> personIPs;
    HashMap<Long, Integer> trafficPerSecondStat;
    HashSet<String> sites;
    HashMap<String, Integer> trafficPersonStat;


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
        this.personIPs = new HashSet<>();
        this.trafficPerSecondStat = new HashMap<>();
        this.sites = new HashSet<>();
        this.trafficPersonStat = new HashMap<>();
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
            personIPs.add(logEntry.getIpAddr());

            trafficPersonStat.merge(logEntry.getIpAddr(), 1, Integer::sum);

            if (trafficPerSecondStat.get(logEntryTime.toEpochSecond(ZoneOffset.UTC)) == null) {
                trafficPerSecondStat.put(logEntryTime.toEpochSecond(ZoneOffset.UTC), 1);
            } else {
                trafficPerSecondStat.put(logEntryTime.toEpochSecond(ZoneOffset.UTC), trafficPerSecondStat.get(logEntryTime.toEpochSecond(ZoneOffset.UTC)) + 1);
            }
        }
        sites.add(logEntry.getRefer());
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

    public double getAveragePersonTraffic() {
        long personTraffic = isBotList.stream()
                .filter((Boolean isBot) -> !isBot)
                .count();
        return threeDecimalRound((double) personTraffic / hours);
    }

    public double getAverageErrorTraffic() {
        return threeDecimalRound((double) errorResponseCount / hours);
    }

    public double getAverageUniquePersonTraffic() {
        long uniquePersonCount = personIPs.stream().count();
        long personTrafficCount = isBotList.stream()
                .filter((Boolean isBot) -> !isBot)
                .count();
        return threeDecimalRound((double) uniquePersonCount / personTrafficCount);
    }

    public HashMap<Long, Integer> getTrafficPerSecondStat() {
        return trafficPerSecondStat;
    }

    public HashMap<Long, Integer> getMaxTrafficPerSecond() {
        HashMap<Long, Integer> res = new HashMap<>();

        int maxValue = trafficPerSecondStat.values().stream()
                .mapToInt(x -> x.intValue())
                .max()
                .orElse(0);

        trafficPerSecondStat.entrySet().stream()
                .filter(pair -> pair.getValue() == maxValue)
                .forEach(pair -> res.put(pair.getKey(), pair.getValue()));
        return res;
    }

    public HashSet<String> getDomains() {
        return sites.stream()
                .map(link -> this.extractDomain(link))
                .filter(domain -> domain != null && !domain.isEmpty())
                .collect(Collectors.toCollection(HashSet::new));
    }

    private String extractDomain(String link) {
        link = URLDecoder.decode(link, StandardCharsets.UTF_8);
        if (link == null || link.isEmpty() || link.equals("-")) {
            return null;
        }
        return link.replaceFirst("^(https?://)?(www\\.)?", "")
                .replaceFirst("/.*$", "")
                .replaceFirst("\\?.*$", "")
                .replaceFirst(":#.*$", "")
                .trim();
    }

    public HashMap<String, Integer> getMaxPersonTraffic() {
        HashMap<String, Integer> res = new HashMap<>();

        int max = trafficPersonStat.values().stream()
                .mapToInt(x -> x.intValue())
                .max()
                .orElse(0);

        trafficPersonStat.entrySet().stream()
                .filter(pair -> pair.getValue() == max)
                .forEach(pair -> res.put(pair.getKey(), pair.getValue()));
        return res;
    }


}
