package http.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import static http.HttpTaskServer.parsePathId;
import static http.HttpTaskServer.sendText;
import static java.nio.charset.StandardCharsets.UTF_8;

public class TaskHandler implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson = Managers.getGson();

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String requestMethod = exchange.getRequestMethod();
            String query = exchange.getRequestURI().getQuery();
            switch (requestMethod) {
                case "GET" :
                    if (Pattern.matches("^/tasks/task$", path) && query == null) {
                        String response = gson.toJson(taskManager.getAllTasks());
                        sendText(exchange, response);
                    } else if (query != null) {
                        String pathId = query.replaceFirst("id=", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            String response = gson.toJson(taskManager.getTaskById(id));
                            sendText(exchange, response);
                        } else {
                            System.out.println("Получен некорректный id");
                            exchange.sendResponseHeaders(405,0);
                        }
                    } else {
                        exchange.sendResponseHeaders(405,0);
                        System.out.println("Отсутствует задача по выбанному id");
                    }
                    break;
                case "POST" :
                    InputStream inputStream = exchange.getRequestBody();
                    String body = new String(inputStream.readAllBytes(), UTF_8);
                    JsonElement jsonElement = JsonParser.parseString(body);
                    if (!jsonElement.isJsonObject()) {
                        throw new RuntimeException("Вы вводите неподходящий формат данных");
                    }
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    Task task = gson.fromJson(jsonObject, Task.class);
                    int taskId = task.getId();
                    if (Pattern.matches("^/tasks/task$", path)) {
                        if (task.getId() != 0 && taskManager.getTaskById(taskId) != null) {
                            taskManager.updateTask(task, taskId);
                        } else {
                            taskManager.createTask(task);
                        }
                    }
                    exchange.sendResponseHeaders(201, 0);
                    break;
                case "DELETE" :
                    if (Pattern.matches("^/tasks/task$", path) && query == null) {
                        taskManager.deleteAllTasks();
                    } else if (query != null) {
                        String pathId = query.replaceFirst("id=", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            taskManager.deleteTaskById(id);
                        } else {
                            System.out.println("Получен некорректный id");
                            exchange.sendResponseHeaders(405,0);
                        }
                    }
                    exchange.sendResponseHeaders(200,0);
                    break;
                default:
                    System.out.println("Ждем GET, DELETE или POST запрос, а пришел " + requestMethod);
                    exchange.sendResponseHeaders(405, 0);
            }
        } catch (Throwable e) {
            exchange.sendResponseHeaders(400, 0);
        } finally {
            exchange.close();
        }
    }
}

