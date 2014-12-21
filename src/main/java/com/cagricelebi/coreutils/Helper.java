package com.cagricelebi.coreutils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;
import javax.xml.bind.DatatypeConverter;

public class Helper {

    /**
     * Beware, the valid word "null" is also rendered as empty.
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || "".contentEquals(str) || "null".contentEquals(str);
    }

    public static <K, V> boolean isEmpty(Map<K, V> map) {
        return map == null || map.isEmpty();
    }

    public static <T> boolean isEmpty(List<T> l) {
        return l == null || l.isEmpty();
    }

    /**
     * Transforms dotted IP address to long number.
     *
     * @param ipStr Dotted ip ie. "127.0.0.1"
     * @return Long number ie. "2130706433"
     */
    public static long ip2Long(String ipStr) {
        long result = 0;
        try {
            InetAddress ip = InetAddress.getByName(ipStr);
            byte[] octets = ip.getAddress();
            for (byte octet : octets) {
                result <<= 8;
                result |= octet & 0xff;
            }
        } catch (Exception e) {
        }
        return result;
    }

    /**
     * Transforms long number to dotted IP address.
     * Does not check validity.
     *
     * @param l Long number ie. "2130706433"
     * @return Dotted ip ie. "127.0.0.1"
     */
    public static String long2Ip(long l) {
        return ((l >> 24) & 0xFF) + "."
                + ((l >> 16) & 0xFF) + "."
                + ((l >> 8) & 0xFF) + "."
                + (l & 0xFF);
    }

    /**
     * Checks validity of a dotted IP address string.
     * Uses {@link java.net.InetAddress}.getByName() method.
     *
     * @param ipStr
     * @return
     */
    public static boolean isValidIp(String ipStr) {
        try {
            InetAddress ip = InetAddress.getByName(ipStr);
            return ip2Long(ipStr) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Read header values and log them in filesystem.
     *
     * @param request
     * @param logger Injected logger.
     * @return
     *
     * public static String logRequestDetails(HttpServletRequest request, Logger logger) {
     * StringBuilder sb = new StringBuilder();
     * try {
     *
     * try {
     * sb.append("(logRequestDetails) Request Address: ").append(extractRequestAddress(request)).append("\n");
     * logger.log(sb.toString());
     * } catch (Exception ignored) {
     * }
     *
     * logger.log("(logRequestDetails) Read request headers of incoming uri query: " + request.getQueryString());
     * sb.append("(logRequestDetails) Read request headers of incoming uri query: ").append(request.getQueryString()).append("\n");
     * Enumeration<String> headers = request.getHeaderNames();
     * while (headers.hasMoreElements()) {
     * String headerName = headers.nextElement();
     * String headerValue = request.getHeader(headerName);
     * logger.log("(logRequestDetails) " + headerName + " : " + headerValue);
     * sb.append("(logRequestDetails) ").append(headerName).append(" : ").append(headerValue).append("\n");
     * }
     * logger.log("(logRequestDetails) request.getRemoteAddr() : " + request.getRemoteAddr());
     * sb.append("(logRequestDetails) request.getRemoteAddr() : ").append(request.getRemoteAddr()).append("\n");
     * } catch (Exception e) {
     * logger.log(e);
     * }
     * return sb.toString();
     * }
     *
     * public static String extractRequestAddress(HttpServletRequest request) {
     * StringBuilder sb = new StringBuilder("");
     * try {
     * sb.append(request.getScheme()).append("://").append(request.getServerName()).append("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort()).append(request.getRequestURI()).append(request.getQueryString() != null ? "?" + request.getQueryString() : "");
     * } catch (Exception e) {
     * }
     * return sb.toString();
     * }
     */
    /**
     * Uses X-Forwarded-For or request.getRemoteAddr() as fallback.
     *
     * @param request
     * @return Dotted IP address in format "xxx.xxx.xxx.xxx".
     *
     * public static String getRemoteAddress(HttpServletRequest request) {
     * String remoteAddress = null;
     *
     * if (request.getHeader("X-Forwarded-For") != null) {
     * remoteAddress = request.getHeader("X-Forwarded-For");
     * }
     * if (request.getHeader("x-forwarded-for") != null) {
     * remoteAddress = request.getHeader("x-forwarded-for");
     * }
     * try {
     * if (remoteAddress != null && !Helper.isEmpty(remoteAddress) && remoteAddress.contains(",")) {
     * String ipAddressRegex = "([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})", privateIpAddressRegex = "(^127\\.0\\.0\\.1)|(^10\\.)|(^172\\.1[6-9]\\.)|(^172\\.2[0-9]\\.)|(^172\\.3[0-1]\\.)|(^192\\.168\\.)";
     * Pattern ipAddressPattern = Pattern.compile(ipAddressRegex), privateIpAddressPattern = Pattern.compile(privateIpAddressRegex);
     * Matcher matcher = ipAddressPattern.matcher(remoteAddress);
     * while (matcher.find()) {
     * if (!privateIpAddressPattern.matcher(matcher.group(0)).find()) {
     * String found = matcher.group(0);
     * if (isValidIp(found)) {
     * remoteAddress = found;
     * break;
     * }
     * }
     * matcher.region(matcher.end(), remoteAddress.length());
     * }
     * }
     * } catch (Exception e) {
     * }
     * if (Helper.isEmpty(remoteAddress) || !Helper.isValidIp(remoteAddress)) {
     * remoteAddress = request.getRemoteAddr();
     * }
     * return remoteAddress;
     * }
     *
     * public static int getStaffIdFromRequestSession(HttpServletRequest request) {
     * try {
     * return ((Staff) request.getSession().getAttribute("staff")).getId();
     * } catch (Exception e) {
     * }
     * return 0;
     * }
     *
     * public static long getProductIdFromRequestSession(HttpServletRequest request) {
     * try {
     * return ((Product) request.getSession().getAttribute("selectedProduct")).getId();
     * } catch (Exception e) {
     * }
     * return 0;
     * }
     */
    /**
     * Returns a valid IP v4 address.
     *
     * @return [0-255].[0-255].[0-255].[0-255]
     */
    public static String generateRandomIp() {
        try {
            Random r = new Random();
            String ip = r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);
            if (isValidIp(ip)) {
                return ip;
            } else {
                return generateRandomIp();
            }
        } catch (Exception e) {
        }
        return "127.0.0.1";
    }

    /**
     * Generates 32 char long UUID without "-" char, in regex pattern "[0-9a-f]".
     *
     * @return
     */
    public static String generateUid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * For logging.
     *
     * @param map
     * @return
     */
    public static <K, V> String map2str(Map<K, V> map) {
        StringBuilder sb = new StringBuilder();
        try {
            if (!isEmpty(map)) {
                for (Map.Entry<K, V> entry : map.entrySet()) {
                    String key = entry.getKey().toString();
                    String val = entry.getValue().toString();
                    sb.append(key).append(':').append(val).append(',');
                }
                return sb.substring(0, sb.length() - 1);
            }
        } catch (Exception e) {
        }
        return sb.toString();
    }

    /**
     * For logging.
     *
     * @param arr
     * @return
     */
    public static <T> String array2str(T[] arr) {
        StringBuilder sb = new StringBuilder("[");
        try {
            if (arr != null && arr.length > 0) {
                for (T elem : arr) {
                    sb.append(elem.toString()).append(", ");
                }
                sb.delete(sb.length() - 2, sb.length());
            }
            sb.append("]");
        } catch (Exception e) {
        }
        return sb.toString();
    }

    /**
     * Simple matcher that finds given regular expression patterns.
     * Samples:<br />
     * contains("selam", new String[]{"q", "r"}) -> returns false.<br />
     * contains("selam", new String[]{"q", "a"}) -> returns true.<br />
     * contains("selam", new String[]{"q", "a$"}) -> returns false.<br />
     * contains("selam", new String[]{"q", "m$"}) -> returns true.<br />
     * contains("selam", new String[]{"^m"}) -> returns false.<br />
     *
     * @param str Source string to search for.
     * @param regularExpressions Regular Expressions as an array.
     * @return TRUE if ANY of the given patterns match. FALSE, if ALL of the given patterns are not found.
     */
    public static boolean contains(String str, String[] regularExpressions) {
        for (String regularExpression : regularExpressions) {
            Pattern pattern = Pattern.compile(regularExpression);
            Matcher matcher = pattern.matcher(str);
            if (matcher.find()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Wrapper for SimpleDateFormat("yyyy-MM-dd HH:mm:ss").
     * ie. "2014-05-31 19:59:08"
     *
     * @param date
     * @return "yyyy-MM-dd HH:mm:ss"
     */
    public static String date2str(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    /**
     * Wrapper for SimpleDateFormat("yyyy-MM-dd HH:mm:ss").
     * ie. "2014-05-31 19:59:08"
     *
     * @param str "yyyy-MM-dd HH:mm:ss"
     * @return
     * @throws ParseException
     */
    public static Date str2date(String str) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.parse(str);
    }

    /**
     * Wrapper for SimpleDateFormat("yyyyMMddHHmmss").
     * ie. "20140531195908"
     *
     * @return
     */
    public static String getFormattedDate() {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
            return sdf.format(java.util.Calendar.getInstance().getTime());
        } catch (Exception e) {
        }
        return Calendar.getInstance().getTimeInMillis() + "";
    }

    /**
     * Wrapper for SimpleDateFormat("yyyyMMddHHmmss").
     * ie. "20140531195908"
     *
     * @param date
     * @return
     */
    public static String getFormattedDate(Date date) {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMddHHmmss");
            return sdf.format(date.getTime());
        } catch (Exception e) {
        }
        return "" + date.getTime();
    }

    /**
     * "1984/10/18".
     *
     * @param date
     * @return
     */
    public static String slashedDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        return sdf.format(date);
    }

    /**
     * printStackTrace.
     *
     * @param t
     * @return
     */
    public static String getStackTrace(Throwable t) {
        try {
            StackTraceElement ste[] = t.getStackTrace();
            StringBuilder sb = new StringBuilder("");
            for (StackTraceElement stackTraceElement : ste) {
                sb.append(stackTraceElement.toString()).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * Gets current thread's stack trace.
     *
     * @return
     */
    public static String getStackTrace() {
        try {
            StackTraceElement ste[] = Thread.currentThread().getStackTrace();
            StringBuilder sb = new StringBuilder("");
            for (int i = 0; i < ste.length; i++) {
                StackTraceElement stackTraceElement = ste[i];
                sb.append(stackTraceElement.toString()).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * Replaces characters.
     * 1. Turkish chars are replaced; ğüşıöçĞÜŞİÖÇ-gusiocGUSIOC.
     * 2. Capital letters converted to lowercase.
     * 3. Anything other than letters, numbers and dot chars are removed.
     *
     * @param source
     * @return source.toLowerCase().replaceAll("[^a-z0-9.]", "").replace("ğ", "g");
     */
    public static String cleanChars(String source) {
        try {
            source = source.replace("ğ", "g");
            source = source.replace("Ğ", "G");
            source = source.replace("ü", "u");
            source = source.replace("Ü", "U");
            source = source.replace("ş", "s");
            source = source.replace("Ş", "S");
            source = source.replace("ı", "i");
            source = source.replace("İ", "I");
            source = source.replace("ö", "o");
            source = source.replace("Ö", "O");
            source = source.replace("ç", "c");
            source = source.replace("Ç", "C");
        } catch (Exception e) {
        }
        try {
            source = source.toLowerCase(Locale.ENGLISH);
        } catch (Exception e) {
        }
        try {
            source = source.replaceAll("[^a-z0-9.]", "");
        } catch (Exception e) {
        }

        return source;
    }

    /**
     * For logging, extremely unnecessary.
     *
     * @param list
     * @return
     */
    public static <T> String list2str(List<T> list) {
        StringBuilder sb = new StringBuilder();
        if (list != null && !list.isEmpty()) {
            sb.append("[");
            for (T t : list) {
                sb.append(t.toString()).append(", ");
            }
            sb.delete(sb.length() - 2, sb.length());
            sb.append("]");
        }
        return sb.toString();
    }

    public static String guessFileTypeFromFileName(String fileName) {
        if (isEmpty(fileName)) {
            return "text/plain;charset=utf-8";
        }
        if (contains(fileName, new String[]{"\\.json(\\.txt)?$"})) {
            return "application/json;charset=utf-8";
        }
        if (contains(fileName, new String[]{"\\.txt$"})) {
            return "text/plain;charset=utf-8";
        }
        if (contains(fileName, new String[]{"\\.js$"})) {
            return "application/javascript;charset=utf-8";
        }
        if (contains(fileName, new String[]{"\\.png$"})) {
            return "image/png";
        }
        if (contains(fileName, new String[]{"\\.jp(eg|e|g)$"})) {
            return "image/jpeg";
        }
        if (contains(fileName, new String[]{"\\.gif$"})) {
            return "image/gif";
        }
        if (contains(fileName, new String[]{"\\.htm(l)?$"})) {
            return "text/html;charset=utf-8";
        }
        if (contains(fileName, new String[]{"\\.XXX$"})) {
            return "XXX/XXX";
        }
        return "text/plain;charset=utf-8";
    }

    /**
     * TODO This method can be improved.
     * Prefer a recursive algorithm.
     *
     * @param <T> POJO or other complex types.
     * @param logger to log with signature of caller class.
     * @param oldVersion before update.
     * @param newVersion after update.
     * @return
     *
     * public static <T> String diff(Logger logger, T oldVersion, T newVersion) {
     * try {
     * Gson gson = new GsonBuilder().setPrettyPrinting().create();
     * String diffStr = new GsonDiff().diff(gson.toJson(oldVersion), gson.toJson(newVersion));
     * StringBuilder sb = new StringBuilder();
     * {
     * JsonElement diffJson = gson.fromJson(diffStr, JsonElement.class);
     * logger.log("diffJson.prettyPrint(): \n" + gson.toJson(diffJson) + "\n");
     * sb.append("diffJson.prettyPrint(): <pre/>\n").append(gson.toJson(diffJson)).append("\n</pre>\n");
     * }
     * return sb.toString();
     * } catch (Exception e) {
     * logger.log(e);
     * }
     * return null;
     * }
     */
    public static <T> String toJson(T obj) {
        return new Gson().toJson(obj);
    }

    public static <T> String toJsonPrettyPrint(T obj) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(obj);
    }

    /**
     * TODO check faster and more stable implementations.
     *
     * @param source_filepath
     * @param destinaton_zip_filepath
     * @throws IOException
     */
    public static void gzipFile(final String source_filepath, final String destinaton_zip_filepath) throws IOException {
        byte[] buffer = new byte[4096];
        try (FileOutputStream fileOutputStream = new FileOutputStream(destinaton_zip_filepath)) {
            try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutputStream)) {
                try (FileInputStream fileInput = new FileInputStream(source_filepath)) {
                    int bytes_read;
                    while ((bytes_read = fileInput.read(buffer)) > 0) {
                        gzipOutputStream.write(buffer, 0, bytes_read);
                    }
                }
                gzipOutputStream.finish();
            }
        } catch (IOException ex) {
            throw ex;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Inflate - Deflate as Base64">
    public static String compressAndBase64byteArray(String openStr) throws Exception {
        byte[] bytes = openStr.getBytes(Statics.DEFAULT_CHARSET);
        byte[] compr = compress(bytes);
        return DatatypeConverter.printBase64Binary(compr);
    }

    public static String decompressBase64edByteArray(String base64) throws Exception {
        byte[] compr = DatatypeConverter.parseBase64Binary(base64);
        byte[] open = decompress(compr);
        return new String(open, Statics.DEFAULT_CHARSET);
    }

    private static byte[] compress(byte[] data) throws IOException {
        Deflater deflater = new Deflater();
        deflater.setInput(data);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);

        deflater.finish();
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer); // returns the generated code... index  
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        byte[] output = outputStream.toByteArray();
        deflater.end();

        // System.out.println("Original: " + data.length + " bytes.");
        // System.out.println("Compressed: " + output.length + " bytes.");
        return output;
    }

    private static byte[] decompress(byte[] data) throws IOException, DataFormatException {
        Inflater inflater = new Inflater();
        inflater.setInput(data);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!inflater.finished()) {
            int count = inflater.inflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        byte[] output = outputStream.toByteArray();
        inflater.end();

        // System.out.println("Original: " + data.length + " bytes.");
        // System.out.println("Decompressed: " + output.length + " bytes.");
        return output;
    }
    // </editor-fold>

}
