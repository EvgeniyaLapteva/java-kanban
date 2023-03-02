package service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import http.KVTaskClient;
import model.Epic;
import model.SubTask;
import model.Task;

import java.io.IOException;
import java.net.URI;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {

    private final KVTaskClient client;
    private final Gson gson = Managers.getGson();

    public HttpTaskManager(URI uri) throws IOException, InterruptedException {
        client = new KVTaskClient(uri);

        JsonElement jsonTasks = JsonParser.parseString(client.load("Task"));
        if (!jsonTasks.isJsonNull()) {
            JsonArray jsonArrayOfTasks = jsonTasks.getAsJsonArray();
            for (JsonElement jsonTask : jsonArrayOfTasks) {
                Task task = gson.fromJson(jsonTask, Task.class);
                createTask(task);
            }
        }

        JsonElement jsonEpics = JsonParser.parseString(client.load("Epic"));
        if (!jsonEpics.isJsonNull()) {
            JsonArray jsonArrayOfEpics = jsonEpics.getAsJsonArray();
            for (JsonElement jsonEpic : jsonArrayOfEpics) {
                Epic epic = gson.fromJson(jsonEpic, Epic.class);
                createEpic(epic);
            }
        }

        JsonElement jsonSubtasks = JsonParser.parseString(client.load("Subtask"));
        if (!jsonSubtasks.isJsonNull()) {
            JsonArray jsonArrayOfSubtasks = jsonSubtasks.getAsJsonArray();
            for (JsonElement jsonSubtask : jsonArrayOfSubtasks) {
                SubTask subTask = gson.fromJson(jsonSubtask, SubTask.class);
                createSubtask(subTask);
            }
        }

        JsonElement jsonHistory = JsonParser.parseString(client.load("History"));
        if (!jsonHistory.isJsonNull()) {
            JsonArray jsonArrayOfHistory = jsonHistory.getAsJsonArray();
            for (JsonElement jsonIdOfTask : jsonArrayOfHistory) {
                int id = jsonIdOfTask.getAsInt();
                if (subTasks.containsKey(id)) {
                    getSubtaskById(id);
                } else if (epics.containsKey(id)) {
                    getEpicById(id);
                } else if (tasks.containsKey(id)) {
                    getTaskById(id);
                }
            }
        }
    }

    @Override
    public void save() {
        try {
            client.put("Task", gson.toJson(tasks.values()));
            client.put("Epic", gson.toJson(epics.values()));
            client.put("Subtask", gson.toJson(subTasks.values()));
            client.put("History", gson.toJson(getHistory()
                    .stream()
                    .map(Task::getId)
                    .collect(Collectors.toList())));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
