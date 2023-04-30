package canban.application.server;

import canban.manager.http.HttpTaskServer;

import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) throws IOException {
        final HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();
    }
}
