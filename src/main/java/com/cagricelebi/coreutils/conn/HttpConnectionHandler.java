package com.cagricelebi.coreutils.conn;

import com.cagricelebi.coreutils.Helper;
import com.cagricelebi.coreutils.log.Logger;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpConnectionHandler {

    private static final Logger logger = Logger.getLogger(HttpConnectionHandler.class);

    public static Response getUrl(String endPoint) {
        Response r = new Response();
        try {
            logger.debug("(getUrl) Trying to access endPoint with GET method: " + endPoint);
            URL url = new URL(endPoint);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setRequestMethod("GET");
            setRequestProperties(httpCon, "GET");
            return connect(httpCon);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return r;
    }

    public static Response postUrl(String endPoint, Map<String, String> params) {
        try {
            logger.debug("(postUrl) Trying to access endPoint with POST method: " + endPoint);
            URL url = new URL(endPoint);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            setRequestProperties(httpCon, "POST");
            httpCon.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            addPostData(httpCon, params);
            return connect(httpCon);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static Response postJson(String endPoint, String jsonParams) {
        try {
            logger.debug("(postJson) Trying to access endPoint with POST method: " + endPoint);
            logger.debug("(postJson) Json data to post: " + jsonParams);
            URL url = new URL(endPoint);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            setRequestProperties(httpCon, "POST");
            httpCon.setRequestProperty("Content-Type", "application/json");
            String encodedData = URLEncoder.encode(jsonParams, "UTF-8");
            httpCon.setRequestProperty("Content-Length", encodedData.length() + "");
            try (OutputStreamWriter out = new OutputStreamWriter(httpCon.getOutputStream(), "UTF-8")) {
                out.write(encodedData);
            }
            return connect(httpCon);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    private static void setRequestProperties(HttpURLConnection httpCon, String type) {
        try {
            httpCon.setUseCaches(false);

            Map<String, String> requestProperties = new HashMap<>();
            requestProperties.put("Pragma", "no-cache");
            requestProperties.put("Expires", "-1");
            requestProperties.put("Cache-Control", "no-cache");
            requestProperties.put("connection", "close");
            requestProperties.put("Accept", "application/json");

            if (type.contentEquals("POST")) {
                httpCon.setRequestMethod("POST");
                httpCon.setDoOutput(true);
            }

            for (Map.Entry<String, String> entry : requestProperties.entrySet()) {
                String name = entry.getKey();
                String value = entry.getValue();
                httpCon.setRequestProperty(name, value);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private static boolean addPostData(HttpURLConnection httpCon, Map<String, String> inputParameters) throws Exception {
        OutputStreamWriter out = null;
        boolean success = false;
        try {
            StringBuilder uri_query = new StringBuilder("");
            for (Map.Entry<String, String> entry : inputParameters.entrySet()) {
                String name = entry.getKey();
                String value = entry.getValue();
                uri_query.append(name).append("=").append(URLEncoder.encode(value, "UTF-8")).append("&");
            }
            String finalUriQ = uri_query.toString();
            logger.debug("(addPostData) uri_query : " + finalUriQ);
            httpCon.setRequestProperty("Content-Length", finalUriQ.length() + "");
            logger.debug("(addPostData) input parameters exist, setting Content-Length to " + finalUriQ.length() + ".");
            out = new OutputStreamWriter(httpCon.getOutputStream(), "UTF-8");
            out.write(finalUriQ);
            success = true;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        if (!success) {
            throw new Exception("Error Constructing Parameters");
        }
        return success;
    }

    private static Response connect(HttpURLConnection httpCon) {
        Response r = new Response();
        BufferedReader rd = null;
        StringBuilder sb = new StringBuilder();
        try {
            try {
                InputStream in = httpCon.getInputStream();
                if (in != null) {
                    try (InputStreamReader in2 = new InputStreamReader(in)) {
                        rd = new BufferedReader(in2, 1024);
                        String line;
                        while ((line = rd.readLine()) != null) {
                            sb.append(line);
                        }
                        in.close();
                    }
                } else {
                    logger.debug("(connect) Cannot read httpCon.getInputStream().");
                    try {
                        logger.debug("(connect) Trying to read getErrorStream 1.");
                        InputStream er = httpCon.getErrorStream();
                        if (er != null) {
                            try (InputStreamReader er2 = new InputStreamReader(er)) {
                                rd = new BufferedReader(er2, 1024);
                                String line;
                                while ((line = rd.readLine()) != null) {
                                    sb.append(line);
                                }
                                er.close();
                            }
                        } else {
                            logger.debug("(connect) Cannot read httpCon.getErrorStream() 1.");
                        }
                    } catch (Exception e1) {
                        logger.debug("(connect) Exception e1: " + e1.getMessage(), e1);
                    }
                }
            } catch (Exception e2) {
                logger.debug("(connect) Exception e2: " + e2.getMessage());
                try {
                    logger.debug("(connect) Trying to read getErrorStream 2.");
                    InputStream er = httpCon.getErrorStream();
                    if (er != null) {
                        rd = new BufferedReader(new InputStreamReader(er), 1024);
                        String line;
                        while ((line = rd.readLine()) != null) {
                            sb.append(line);
                        }
                        er.close();
                    } else {
                        logger.debug("(connect) Cannot read httpCon.getErrorStream() 2.");
                    }
                } catch (Exception e3) {
                    logger.debug("(connect) Exception e3: " + e3.getMessage(), e3);
                }
            }

            String output = sb.toString();
            r.setOutput(output);
            logger.debug("(connect) output: " + output);
        } catch (Exception e4) {
            logger.debug("(connect) Exception e4: " + e4.getMessage(), e4);
        } finally {
            try {
                if (rd != null) {
                    rd.close();
                }
            } catch (Exception e5) {
                logger.debug("(connect) Exception e5: " + e5.getMessage(), e5);
            }
        }

        int responseCode = -1;
        try {
            responseCode = httpCon.getResponseCode();
        } catch (Exception e6) {
            logger.debug("(connect) Exception e6: " + e6.getMessage(), e6);
        }

        String responseMessage = "-1";
        try {
            responseMessage = httpCon.getResponseMessage();
        } catch (Exception e7) {
            logger.debug("(connect) Exception e7: " + e7.getMessage(), e7);
        }
        logger.debug("(connect) responseCode: {}, responseMessage: {}", responseCode, responseMessage);

        r.setHttpResponseCode(responseCode);
        r.setHttpResponseMessage(responseMessage);

        if (logger.isDebugEnabled()) {
            readHeaders(httpCon);
        }
        return r;
    }

    private static void readHeaders(HttpURLConnection httpCon) {
        try {
            logger.debug("(readHeaders) Accessing getHeaderFields.");
            Map<String, List<String>> headers = httpCon.getHeaderFields();
            if (!Helper.isEmpty(headers)) {
                for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                    String key = entry.getKey();
                    List<String> list = entry.getValue();
                    if (!Helper.isEmpty(list)) {
                        for (int i = 0; i < list.size(); i++) {
                            String val = list.get(i);
                            logger.debug("(readHeaders) key: {} -> val[{}]: {}", key, i, val);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.debug(e.getMessage(), e);
        }
    }

}
