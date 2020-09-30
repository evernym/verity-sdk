/*
 * COPYRIGHT 2013-2020, ALL RIGHTS RESERVED, EVERNYM INC.
 * Adapted from https://hc.apache.org/httpcomponents-core-ga/httpcore/examples/org/apache/http/examples/HttpFileServer.java
 */
package com.evernym.sdk.example;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.Locale;

import org.apache.http.ConnectionClosedException;
import org.apache.http.ExceptionLogger;
import org.apache.http.HttpConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;

interface Handler {
    public void handler(String message);
}

public class Listener {
    Integer port;
    Handler handler;
    private HttpServer server;

    public Listener (Integer port, Handler handler) {
        this.port = port;
        this.handler = handler;
    }

    public void listen() throws IOException, InterruptedException {
        SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(15000)
                .setTcpNoDelay(true)
                .build();

        server = ServerBootstrap.bootstrap()
                .setListenerPort(this.port)
                .setServerInfo("Test/1.1")
                .setSocketConfig(socketConfig)
                .setExceptionLogger(new StdErrorExceptionLogger())
                .registerHandler("*", new HttpHandler())
                .create();

        server.start();
    }

    public void stop() {
        if (server != null)
            server.stop();
    }

    static class StdErrorExceptionLogger implements ExceptionLogger {

        @Override
        public void log(final Exception ex) {
            if (ex instanceof SocketTimeoutException) {
                // System.err.println("Connection timed out");
            } else if (ex instanceof ConnectionClosedException) {
                // System.err.println(ex.getMessage());
            } else if (ex instanceof SocketException) {
                System.err.println("Socket closed");
            } else {
                ex.printStackTrace();
            }
        }

    }

    class HttpHandler implements HttpRequestHandler  {

        public HttpHandler() {
            super();
        }

        public void handle(
                final HttpRequest request,
                final HttpResponse response,
                final HttpContext context) throws HttpException, IOException {

            String method = request.getRequestLine().getMethod().toUpperCase(Locale.ROOT);
            if (!method.equals("GET") && !method.equals("HEAD") && !method.equals("POST")) {
                throw new MethodNotSupportedException(method + " method not supported");
            }
            String target = request.getRequestLine().getUri();
            // System.err.println("Got request for " + method + " " + target);

            if (request instanceof HttpEntityEnclosingRequest) {
                HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
                byte[] entityContent = EntityUtils.toByteArray(entity);
                Listener.this.handler.handler(new String(entityContent));
            }
            
            HttpCoreContext coreContext = HttpCoreContext.adapt(context);
            HttpConnection conn = coreContext.getConnection(HttpConnection.class);
            response.setStatusCode(HttpStatus.SC_OK);
            response.setEntity(new StringEntity("Success", Charset.forName("UTF-8")));
            // System.err.println(conn + ": serving data \"Success\"");
        }
    }
}
