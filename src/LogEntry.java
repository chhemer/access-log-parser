import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogEntry {
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", java.util.Locale.ENGLISH);

    public final String ipAddr;
    public final LocalDateTime time;
    public final HttpMethod method;
    public final String path;
    public final int responseCode;
    public final int responseSize;
    public final String referer;
    public final String userAgent;

    public LogEntry(String logLine) {
        try {
            int idxFirstQuote = logLine.indexOf('"');
            int idxLastQuote = logLine.lastIndexOf('"');

            if (idxFirstQuote == -1 || idxLastQuote == -1 || idxLastQuote <= idxFirstQuote) {
                throw new IllegalArgumentException("Кавычки не найдены или неправильный порядок");
            }

            String beforeRequest = logLine.substring(0, idxFirstQuote).trim();
            String request = logLine.substring(idxFirstQuote + 1, logLine.indexOf('"', idxFirstQuote + 1));

            String rest = logLine.substring(idxFirstQuote + request.length() + 2);
            int secondQuote = rest.indexOf('"');
            String statusPart = rest.substring(0, secondQuote).trim();

            String afterStatus = rest.substring(secondQuote + 1).trim();
            int thirdQuote = afterStatus.indexOf('"');
            String refererPart = afterStatus.substring(0, thirdQuote);

            String userAgentPart = afterStatus.substring(thirdQuote + 1).replaceAll("^\"|\"$", "");

            String[] prefixParts = beforeRequest.split(" ");
            this.ipAddr = prefixParts[0];
            String rawDate = prefixParts[3].substring(1) + " " + prefixParts[4].substring(0, prefixParts[4].length() - 1);
            this.time = LocalDateTime.parse(rawDate, FORMATTER);

            String[] requestParts = request.split(" ");
            this.method = HttpMethod.valueOf(requestParts[0]);
            this.path = requestParts[1];

            String[] statusParts = statusPart.split(" ");
            this.responseCode = Integer.parseInt(statusParts[0]);
            this.responseSize = statusParts[1].equals("-") ? 0 : Integer.parseInt(statusParts[1]);

            this.referer = refererPart;
            this.userAgent = userAgentPart;

        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка разбора строки: " + e.getMessage());
        }
    }

    public String getIpAddr() { return ipAddr; }
    public LocalDateTime getTime() { return time; }
    public HttpMethod getMethod() { return method; }
    public String getPath() { return path; }
    public int getResponseCode() { return responseCode; }
    public int getResponseSize() { return responseSize; }
    public String getReferer() { return referer; }
    public String getUserAgent() { return userAgent; }
    public String getUrl() {
        return this.path;
    }
    public String getOperatingSystem() {
        UserAgent agent = new UserAgent(this.userAgent);
        return agent.getOperatingSystem();
    }
}

enum HttpMethod {
    GET, POST, PUT, DELETE, HEAD, OPTIONS, PATCH
}