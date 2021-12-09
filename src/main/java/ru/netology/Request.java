package ru.netology;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {
    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final List<NameValuePair> queryParams;
    private final InputStream in;

    private Request(String method, String path, Map<String, String> headers, List<NameValuePair> queryParams, InputStream in) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.queryParams = queryParams;
        this.in = in;
    }


    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public List<NameValuePair> getQueryParams() {
        return queryParams;
    }

    public NameValuePair getQueryParam(String name) {
        return queryParams.stream().filter(nameValuePair -> nameValuePair.getName().equals(name)).findAny().orElse(null);
    }

    public static Request fromInputStream(InputStream inputStream) throws IOException, URISyntaxException {
        final var in = new BufferedReader(new InputStreamReader(inputStream));
        final var requestLine = in.readLine();

        final var parts = requestLine.split(" ");
        if (parts.length != 3) {
            throw new IOException("Invalid request");
        }

        final var uri = new URI(parts[1]);
        final var params = URLEncodedUtils.parse(uri.getQuery(), StandardCharsets.UTF_8);

        String line;
        Map<String, String> headers = new HashMap<>();
        while (!(line = in.readLine()).equals("")) {
            String[] lineParts = line.trim().split(":");
            headers.put(lineParts[0], lineParts[1]);
        }

        return new Request(parts[0], uri.getPath(), headers, params, inputStream);
    }
}
