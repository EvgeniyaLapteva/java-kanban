package http.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Epic;
import model.SubTask;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import static http.HttpTaskServer.parsePathId;
import static http.HttpTaskServer.sendText;
import static java.nio.charset.StandardCharsets.UTF_8;

public class SubtaskHandler implements HttpHandler {

    TaskManager taskManager;
    Gson gson = Managers.getGson();

    public SubtaskHandler(TaskManager taskManager) {
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
                    if (Pattern.matches("^/tasks/subtask$", path) && query == null) {
                        String response = gson.toJson(taskManager.getAllSubtasks());
                        sendText(exchange, response);
                    } else if (Pattern.matches("^/tasks/subtask/$", path) && query != null) {
                        String pathId = query.replaceFirst("id=", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            String response = gson.toJson(taskManager.getSubtaskById(id));
                            sendText(exchange, response);
                        } else {
                            System.out.println("Получен некорректный id");
                            exchange.sendResponseHeaders(405, 0);
                        }
                    }
                    if (Pattern.matches("^/tasks/subtask/epic/$", path) && query != null) {
                        String pathID = query.replaceFirst("id=", "");
                        int id = parsePathId(pathID);
                        if (id != -1) {
                            Epic epic = taskManager.getEpicById(id);
                            String response = gson.toJson(epic.getSubTaskIdList());
                            sendText(exchange, response);
                        }
                    } else {
                        exchange.sendResponseHeaders(405,0);
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
                    SubTask subTask = gson.fromJson(jsonObject, SubTask.class);
                    int subtaskId = subTask.getId();
                    if (Pattern.matches("^/tasks/subtask$", path)) {
                        if (subTask.getId() !=0 && taskManager.getSubtaskById(subtaskId) != null) {
                            taskManager.updateSubTask(subTask, subtaskId);
                        } else {
                            taskManager.createSubtask(subTask);
                        }
                    }
                    exchange.sendResponseHeaders(201, 0);
                    break;
                case "DELETE" :
                    if (Pattern.matches("^/tasks/subtask$", path) && query == null) {
                        taskManager.deleteAllSubTasks();
                    } else if (query != null) {
                        String pathId = query.replaceFirst("id=", "");
                        int id = parsePathId(pathId);
                        if (id != -1) {
                            taskManager.deleteSubTaskById(id);
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
