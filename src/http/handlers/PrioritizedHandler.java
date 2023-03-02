package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.util.regex.Pattern;

import static http.HttpTaskServer.sendText;

public class PrioritizedHandler implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson = Managers.getGson();

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String requestMethod = exchange.getRequestMethod();
            switch (requestMethod) {
                case "GET" :
                    if (Pattern.matches("^/tasks$", path)) {
                        String response = gson.toJson(taskManager.getPrioritizedTasks());
                        sendText(exchange, response);
                    } else {
                        exchange.sendResponseHeaders(405,0);
                    }
                    break;
                default:
                    System.out.println("Ждем GET запрос, а пришел " + requestMethod);
            }
        } catch (Throwable e) {
            exchange.sendResponseHeaders(400, 0);
        } finally {
            exchange.close();
        }
    }
}
