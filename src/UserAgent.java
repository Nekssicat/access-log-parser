public class UserAgent {
    final String os;
    final String browser;

    public UserAgent(String userAgent) {
        this.os = parseOsType(userAgent);
        this.browser = parseBrowser(userAgent);
    }

    private String parseOsType(String userAgent) {
        String upperTxt = userAgent.toUpperCase();
        if (upperTxt.contains("WINDOWS")) {
            return "Windows";
        } else if (upperTxt.contains("MAC OS")) {
            return "Mac OS";
        } else if (upperTxt.contains("LINUX")) {
            return "Linux";
        } else if (upperTxt.contains("ANDROID")) {
            return "Android";
        } else if (upperTxt.contains("IPHONE")) {
            return "iPhone";
        } else {
            return "Остальные";
        }
    }

    private String parseBrowser(String userAgent) {
        String upperTxt = userAgent.toUpperCase();
        if (upperTxt.contains("FIREFOX")) {
            return "Firefox";
        } else if (upperTxt.contains("CHROME")) {
            return "Chrome";
        } else if (upperTxt.contains("OPERA")) {
            return "Opera";
        } else if (upperTxt.contains("EDGE")) {
            return "Edge";
        } else if (upperTxt.contains("SAFARI")) {
            return "Safari";
        } else {
            return "-";
        }
    }

    public String getOs() {
        return os;
    }

    public String getBrowser() {
        return browser;
    }

    @Override
    public String toString() {
        return "UserAgent{" +
                "ОС='" + os + '\'' +
                ", браузер='" + browser + '\'' +
                '}';
    }
}
