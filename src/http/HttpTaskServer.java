package http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import http.handlers.*;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {

    private static final int PORT = 8080;
    //private static final TaskManager taskManager = Managers.getDefaultFileBackedManager();

    private static HttpServer server;

    public void start() {
        try {
            TaskManager taskManager = Managers.getDefaultHttpManager();
            server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
            System.out.println("Запускаем сервер на порту " + PORT);
            System.out.println("Открой в браузере http://localhost:" + PORT + "/");
            server.createContext("/tasks/task", new TaskHandler(taskManager));
            server.createContext("/tasks/subtask", new SubtaskHandler(taskManager));
            server.createContext("/tasks/epic", new EpicHandler(taskManager));
            server.createContext("/tasks/history", new HistoryHandler(taskManager));
            server.createContext("/tasks", new PrioritizedHandler(taskManager));
            server.start();
        } catch (IOException | InterruptedException exception) {
            System.out.println("При старте сервера произошла ошибка");
        }
    }

    public void stop() {
        System.out.println("Остановили сервер на порту " + PORT);
        server.stop(0);
    }

    public static void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }

    public static int parsePathId(String path) {
        try {
            return Integer.parseInt(path);
        } catch (NumberFormatException exception) {
            return -1;
        }
    }
}
