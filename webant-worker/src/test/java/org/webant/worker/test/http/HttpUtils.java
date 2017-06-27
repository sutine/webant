package org.webant.worker.test.http;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.Asserts;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;

public class HttpUtils {
    private static int DEFAULT_MAX_TOTAL_CONNECTION = 200;
    private static int DEFAULT_MAX_PER_ROUT = 50;

    private static HttpClientBuilder defaultPoolingHttpClientBuilder() {
        ConnectionSocketFactory plainSf = PlainConnectionSocketFactory.getSocketFactory();
        LayeredConnectionSocketFactory sslSf = SSLConnectionSocketFactory.getSocketFactory();
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", plainSf)
                .register("https", sslSf)
                .build();

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry);
        // max total connection
        cm.setMaxTotal(DEFAULT_MAX_TOTAL_CONNECTION);
        cm.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUT);

//        HttpHost localhost = new HttpHost("localhost", 80);
//        cm.setMaxPerRoute(new HttpRoute(localhost), 50);

        //请求重试处理
        HttpRequestRetryHandler httpRequestRetryHandler = (exception, executionCount, context) -> {
            // max retry times
            if (executionCount >= 3) {
                return false;
            }
            // lost connection
            if (exception instanceof NoHttpResponseException) {
                return true;
            }
            if (exception instanceof SSLHandshakeException) {
                return false;
            }
            // time out
            if (exception instanceof InterruptedIOException) {
                return false;
            }
            // host unreachable
            if (exception instanceof UnknownHostException) {
                return false;
            }
            // forbidden
            if (exception instanceof ConnectTimeoutException) {
                return false;
            }
            if (exception instanceof SSLException) {
                return false;
            }

            HttpClientContext clientContext = HttpClientContext.adapt(context);
            HttpRequest request = clientContext.getRequest();
            // retry
            if (!(request instanceof HttpEntityEnclosingRequest)) {
                return true;
            }
            return false;
        };

        return HttpClients.custom()
                .setConnectionManager(cm)
                .setRetryHandler(httpRequestRetryHandler);
    }

    public static CloseableHttpClient createHttpClient() {
        return defaultPoolingHttpClientBuilder().build();
    }

    public static CloseableHttpClient createProxyHttpClient(String host, int port) {
        Asserts.check(StringUtils.isNotBlank(host) && port > 0, "invalid proxy!");
        HttpRoutePlanner routePlanner = (target, request, context) -> new HttpRoute(target, null,  new HttpHost(host, port),
                "https".equalsIgnoreCase(target.getSchemeName()));

        return defaultPoolingHttpClientBuilder()
                .setRoutePlanner(routePlanner)
                .build();
    }

    public static CloseableHttpClient createProxyHttpClient(String host, int port, String username, String password) {
        Asserts.check(StringUtils.isNotBlank(host) && port > 0, "invalid proxy!");

        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        AuthScope authscope = new AuthScope(host, port);
        if (StringUtils.isNotBlank(username)) {
            Credentials credentials = new UsernamePasswordCredentials(username, password);
            credentialsProvider.setCredentials(authscope, credentials);

            return defaultPoolingHttpClientBuilder()
                    .setDefaultCredentialsProvider(credentialsProvider)
                    .build();
        } else
            return createProxyHttpClient(host, port);
    }
}
