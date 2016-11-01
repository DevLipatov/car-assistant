package com.main.carassistant.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HttpClient_recucler {

    public String get(String url) throws Exception {
        return doRequest(url, "GET", null, null);
    }

    public String get(String url, Map<String, String> headers) throws Exception {
        return doRequest(url, "GET", headers, null);
    }

    public String post(String url, Map<String, String> header, String body) throws Exception {
        return doRequest(url, "POST", header, body);
    }

    private String doRequest(String url, String type, Map<String, String> header, String body) throws Exception {
        String response = null;
        HttpURLConnection connection = null;
        try {
            URL reqUrl = new URL(url);
            connection = ((HttpURLConnection) reqUrl.openConnection());
            connection.setRequestMethod(type);
            if (header != null) {
                for (String key : header.keySet()) {
                    connection.addRequestProperty(key, header.get(key));
                }
            }

            InputStream inputStream;

            boolean isSuccess = connection.getResponseCode() >= 200 && connection.getResponseCode() < 300;
            if (isSuccess) {
                inputStream = connection.getInputStream();
            } else {
                inputStream = connection.getErrorStream();
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            response = stringBuilder.toString();

            inputStream.close();

            if (!isSuccess) {
                System.out.println("http exception = " + response);
                throw new Exception(response);
            }

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return response;
    }

}
