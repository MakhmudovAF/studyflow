package studyflow.http;

import com.sun.net.httpserver.HttpServer;
import studyflow.service.TaskService;

import java.io.IOException;
import java.net.InetSocketAddress;

public class StudyFlowServer {
    private final HttpServer server;

    public StudyFlowServer(int port, TaskService taskService) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/tasks", new TaskHandler(taskService));
        server.setExecutor(null);
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(0);
    }
}