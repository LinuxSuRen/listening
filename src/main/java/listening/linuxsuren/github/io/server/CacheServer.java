/*
 * Copyright 2024 LinuxSuRen.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package listening.linuxsuren.github.io.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.*;
import java.net.http.HttpRequest;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class CacheServer implements HttpHandler {
    private static int port = -1;
    private Map<String, String> cacheQueue = new HashMap<>();

    public void start(int port) throws IOException {
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(20);
        InetSocketAddress address = new InetSocketAddress("127.0.0.1", port);
        HttpServer server = HttpServer.create(address, 0);
        server.setExecutor(threadPoolExecutor);
        server.createContext("/", this);
        server.start();
        port = server.getAddress().getPort(); // it can return the real port if the input parameter is 0
        System.out.println("started cache server with port: " + port);
    }

    public void start() throws IOException {
        start(0);
    }

    private String getCacheDir() {
       return System.getProperty("user.home") + File.separator + ".config/listening/cache";
    }

    @Override
    public void handle(HttpExchange e) throws IOException {
        String query = e.getRequestURI().getRawQuery();
        String path = e.getRequestURI().getPath();
        path = path.substring(1);
        if (query != null && !query.isEmpty()) {
            path += "?" + query;
        }
        e.getRequestHeaders().forEach((a, b) -> {
            System.out.println(a + "==" + b);
        });

        File cacheFile;
        try {
            byte[] hash = MessageDigest.getInstance("MD5").digest(path.getBytes());
            String output = new java.math.BigInteger(1, hash).toString(16);
            cacheFile = new File(getCacheDir(), output);
            cacheFile.getParentFile().mkdirs();
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }

        // check the cache living duration
        Duration cacheLive = parseCacheLive(query);
        if (cacheLive != null) {
            Date expectedDate = new Date(cacheFile.lastModified() + cacheLive.toMillis());
            if (expectedDate.before(new Date())) {
                System.out.println("will delete the invalid cache: " + path);
                // TODO should hava a smart way to let the caller know it
                cacheFile.delete();
            }
        }

        boolean stillCache = cacheQueue.get(path) != null;
        if (!cacheFile.exists() || cacheFile.length() == 0 || stillCache) {
            if (!stillCache) {
                cacheQueue.put(path, "");
            }

            System.out.println("start to cache (still: " + stillCache + ") " + path);
            ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
            OutputStream output = null;
            try {
                if (!stillCache) {
                    output = new FileOutputStream(cacheFile);
                }

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(new URI(path))
                        .header("User-Agent", "linuxsuren/listening")
                        .GET()
                        .build();

                HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
                HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

                try (InputStream respInput = response.body()) {
                    byte[] buf = new byte[1024];
                    int count = respInput.read(buf);
                    while (count != -1) {
                        if (output != null) {
                            output.write(buf, 0, count);
                        }

                        byteOutput.write(buf, 0, count);
                        count = respInput.read(buf);
                    }
                }

                cacheQueue.remove(path);
                response.headers().map().forEach((k, values) -> {
                    values.forEach( v -> {
                        e.getResponseHeaders().add(k, v);
                    });
                });
            } catch (URISyntaxException ex) {
                throw new RuntimeException(ex);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            } finally {
                if (output != null) {
                    output.close();
                }
            }

            byte[] data = byteOutput.toByteArray();
            e.sendResponseHeaders(200, data.length);
            e.getResponseBody().write(data);
            System.out.println("end of cache " + path);
        } else {
            System.out.println("found cache of " + path);
            try (InputStream input = new FileInputStream(cacheFile);
                OutputStream writer = e.getResponseBody()) {
                e.sendResponseHeaders(200, cacheFile.length());

                byte[] buf = new byte[1024];
                int count = input.read(buf);
                while(count != -1) {
                    writer.write(buf, 0, count);
                    count = input.read(buf);
                }
            }
        }
    }

    public static Duration parseCacheLive(String query) {
        if (query == null || query.isEmpty()) {
            return null;
        }

        Optional<String> cacheLiveOpt = Arrays.stream(query.split("&")).filter(o -> o.startsWith("cacheLive=")).findFirst();
        if (cacheLiveOpt.isPresent()) {
            return Duration.parse(cacheLiveOpt.get().replace("cacheLive=", ""));
        }

        return null;
    }

    public static String wrap(String address) {
        if (port == -1) {
            return address;
        }
        return "http://127.0.0.1:" + port + "/" + address;
    }

    public static URL wrapURL(String address) throws MalformedURLException {
        return new URL(wrap(address));
    }

    public static URL wrapURLWithLive(String address, Duration live) throws MalformedURLException {
        if (live == null) {
            return wrapURL(address);
        }

        String wrappedAddress = address;
        if (!wrappedAddress.contains("?")) {
            wrappedAddress += "?";
        } else {
            wrappedAddress += "&";
        }
        wrappedAddress += "cacheLive=" + live;
        return wrapURL(wrappedAddress);
    }
}
