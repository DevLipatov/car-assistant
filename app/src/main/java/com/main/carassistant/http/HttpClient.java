package com.main.carassistant.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClient {

    public <Result> Result getResult(String url, ResultConverter<Result> resultConverter) throws IOException {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            URL reqUrl = new URL(url);
            connection = ((HttpURLConnection) reqUrl.openConnection());
            connection.setRequestMethod("GET");
            inputStream = connection.getInputStream();
            return resultConverter.convert(inputStream);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public interface ResultConverter<Result> {

        Result convert(InputStream inputStream);

    }
}
