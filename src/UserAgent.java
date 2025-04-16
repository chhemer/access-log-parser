public class UserAgent {
    public final String browser;
    public final String os;

    public UserAgent(String userAgent) {
        this.browser = parseBrowser(userAgent);
        this.os = parseOS(userAgent);
    }

    private String parseBrowser(String agent) {
        if (agent.contains("Edg")) return "Edge";
        if (agent.contains("OPR") || agent.contains("Opera")) return "Opera";
        if (agent.contains("Chrome")) return "Chrome";
        if (agent.contains("Firefox")) return "Firefox";
        if (agent.contains("Safari")) return "Safari";
        return "Other";
    }

    private String parseOS(String agent) {
        if (agent.contains("Windows")) return "Windows";
        if (agent.contains("Macintosh") || agent.contains("Mac OS")) return "macOS";
        if (agent.contains("X11") || agent.contains("Linux")) return "Linux";
        return "Other";
    }

    public String getBrowser() { return browser; }
    public String getOs() { return os; }
}