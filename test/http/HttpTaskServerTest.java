package http;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.*;
import service.Managers;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {

    private static KVServer kvServer;
    private static HttpTaskServer server;
    private static final Gson gson = Managers.getGson();

    @BeforeEach
    public void startServer() {
        try {
            kvServer = new KVServer();
            kvServer.start();
            server = new HttpTaskServer();
            server.start();
        } catch (IOException exception) {
            System.out.println("Ошибка при запуске сервера");
        }
    }

    @AfterEach
    public void stopServer() {
        kvServer.stop();
        server.stop();
    }

//    @BeforeEach
//    public void rebootServer() {
//        HttpClient client = HttpClient.newHttpClient();
//        URI uri = URI.create("http://localhost:8080/tasks/task");
//        try {
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(uri)
//                    .DELETE()
//                    .build();
//            client.send(request, HttpResponse.BodyHandlers.ofString());
//            uri = URI.create("http://localhost:8080/tasks/epic");
//            request = HttpRequest.newBuilder()
//                    .uri(uri)
//                    .DELETE()
//                    .build();
//            client.send(request, HttpResponse.BodyHandlers.ofString());
//            uri = URI.create("http://localhost:8080/tasks/subtask");
//            request = HttpRequest.newBuilder()
//                    .uri(uri)
//                    .DELETE()
//                    .build();
//            client.send(request, HttpResponse.BodyHandlers.ofString());
//        } catch (IOException | InterruptedException exception) {
//            System.out.println("Ошибка при перезагрузке сервера");
//        }
//    }

    @Test
    public void shouldGetTasks() {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task");
        Task task = new Task("task", "test", TaskStatus.NEW, 15,
                LocalDateTime.of(2023, 2, 5, 2, 10));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder().uri(uri).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonArray jsonArrayOfTasks = JsonParser.parseString(response.body()).getAsJsonArray();

            assertEquals(200, response.statusCode(), "Пришел не тот код ответа");
            assertEquals(1, jsonArrayOfTasks.size());

        } catch (IOException | InterruptedException exception) {
            System.out.println("Задача не возвращается");
        }
    }

    @Test
    public void shouldGetTaskById() {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task");
        Task task = new Task("task", "test", TaskStatus.NEW, 15,
                LocalDateTime.of(2023, 2, 5, 2, 10));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request  = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/tasks/task/?id=1"))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Type taskType = new TypeToken<Task>(){}.getType();

            Task actual = gson.fromJson(response.body(), taskType);
            task.setId(1);

            assertEquals(200, response.statusCode(), "Пришел не тот код ответа");
            assertNotNull(actual);
            assertEquals(task, actual);

        } catch (IOException | InterruptedException exception) {
            System.out.println("Задача не возвращается");
        }
    }

    @Test
    public void shouldDeleteAllTasks() {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task");
        Task task = new Task("task", "test", TaskStatus.NEW, 15,
                LocalDateTime.of(2023, 2, 5, 2, 10));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder()
                    .uri(uri)
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            HttpRequest requestAfterDelete = HttpRequest.newBuilder().uri(uri).GET().build();
            HttpResponse<String> responseAfterDelete = client.send(requestAfterDelete,
                    HttpResponse.BodyHandlers.ofString());

            JsonArray jsonArrayOfTasks = JsonParser.parseString(responseAfterDelete.body()).getAsJsonArray();

            assertEquals(200, response.statusCode(), "Пришел не тот код ответа на DELETE-запрос");
            assertEquals(0, jsonArrayOfTasks.size(), "Задачи не удалены");
        } catch (IOException | InterruptedException exception) {
            System.out.println("Выполнить удаление не получается");
        }
    }

    @Test
    public void shouldDeleteTaskById() {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task");
        Task task = new Task("task", "test", TaskStatus.NEW, 15,
                LocalDateTime.of(2023, 2, 5, 2, 10));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/tasks/task/?id=1"))
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode(), "Пришел не тот код ответа на DELETE-запрос");
        } catch (IOException | InterruptedException exception) {
            System.out.println("Выполнить удаление не получается");
        }
    }

    @Test
    public void shouldUpdateTask() {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task");
        Task task = new Task("task", "test", TaskStatus.NEW, 15,
                LocalDateTime.of(2023, 2, 5, 2, 10));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            task.setId(1);
            task.setTaskStatus(TaskStatus.IN_PROGRESS);
            request = HttpRequest.newBuilder()
                    .uri(uri).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task))).build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            uri = URI.create("http://localhost:8080/tasks/task/?id=1");
            request = HttpRequest.newBuilder().uri(uri).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Task taskFromResponse = gson.fromJson(response.body(), Task.class);

            assertEquals(task, taskFromResponse);
        } catch (IOException | InterruptedException exception) {
            System.out.println("Ошибка при обновлении задачи");
        }
    }

    @Test
    public void shouldGetEpics() {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic");
        Epic epic = new Epic("epic", "test", TaskStatus.NEW, 50, LocalDateTime.of(2022, 12,
                3, 10, 50));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder().uri(uri).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonArray jsonArrayOfEpics = JsonParser.parseString(response.body()).getAsJsonArray();

            assertEquals(200, response.statusCode(), "Пришел не тот код ответа");
            assertEquals(1, jsonArrayOfEpics.size());

        } catch (IOException | InterruptedException exception) {
            System.out.println("Эпик не возвращается");
        }
    }

    @Test
    public void shouldUpdateEpic() {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        Epic epic = new Epic("epic", "test");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        try {
            HttpResponse<String> postResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (postResponse.statusCode() == 201) {
                epic.setTaskStatus(TaskStatus.IN_PROGRESS);
                request = HttpRequest.newBuilder()
                        .uri(url)
                        .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                        .build();
                client.send(request, HttpResponse.BodyHandlers.ofString());

                url = URI.create("http://localhost:8080/tasks/epic/?id=1");
                request = HttpRequest.newBuilder().uri(url).GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                Epic responseTask = gson.fromJson(response.body(), Epic.class);

                assertEquals(200, response.statusCode());
                assertEquals(epic, responseTask);

            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldGetEpicById() {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic");
        Epic epic = new Epic("epic", "test", TaskStatus.NEW, 15,
                LocalDateTime.of(2023, 2, 5, 2, 10));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request  = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/tasks/epic/?id=1"))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Type epicType = new TypeToken<Epic>(){}.getType();

            Epic actual = gson.fromJson(response.body(), epicType);
            epic.setId(1);

            assertEquals(200, response.statusCode(), "Пришел не тот код ответа");
            assertNotNull(actual);
            assertEquals(epic, actual);

        } catch (IOException | InterruptedException exception) {
            System.out.println("Эпик не возвращается");
        }
    }

    @Test
    public void shouldDeleteEpicById() {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic");
        Epic epic = new Epic("epic", "test", TaskStatus.NEW, 15,
                LocalDateTime.of(2023, 2, 5, 2, 10));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/tasks/epic/?id=1"))
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode(), "Пришел не тот код ответа на DELETE-запрос");
        } catch (IOException | InterruptedException exception) {
            System.out.println("Выполнить удаление не получается");
        }
    }

    @Test
    public void shouldDeleteAllEpics() {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic");
        Epic epic = new Epic("epic", "test", TaskStatus.NEW, 15,
                LocalDateTime.of(2023, 2, 5, 2, 10));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder()
                    .uri(uri)
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            HttpRequest requestAfterDelete = HttpRequest.newBuilder().uri(uri).GET().build();
            HttpResponse<String> responseAfterDelete = client.send(requestAfterDelete,
                    HttpResponse.BodyHandlers.ofString());

            JsonArray jsonArrayOfTasks = JsonParser.parseString(responseAfterDelete.body()).getAsJsonArray();

            assertEquals(200, response.statusCode(), "Пришел не тот код ответа на DELETE-запрос");
            assertEquals(0, jsonArrayOfTasks.size(), "Эпики не удалены");
        } catch (IOException | InterruptedException exception) {
            System.out.println("Выполнить удаление не получается");
        }
    }

    @Test
    public void shouldGetSubtask() {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic");
        Epic epic = new Epic("epic", "test", TaskStatus.NEW, 15,
                LocalDateTime.of(2023, 2, 5, 2, 10));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            uri = URI.create("http://localhost:8080/tasks/subtask");
            SubTask subTask = new SubTask("task", "test", TaskStatus.NEW, 15,
                    LocalDateTime.of(2022, 2, 5, 2, 10), 1);
            request = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask)))
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder().uri(uri).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonArray jsonArrayOfSubtasks = JsonParser.parseString(response.body()).getAsJsonArray();

            assertEquals(200, response.statusCode(), "Пришел не тот код ответа");
            assertEquals(1, jsonArrayOfSubtasks.size());

        } catch (IOException | InterruptedException exception) {
            System.out.println("Подзадачи не возвращается");
        }
    }

    @Test
    public void shouldGetSubtaskById() {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic");
        Epic epic = new Epic("epic", "test", TaskStatus.NEW, 15,
                LocalDateTime.of(2023, 2, 5, 2, 10));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();

        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            uri = URI.create("http://localhost:8080/tasks/subtask");
            SubTask subTask = new SubTask("subtask", "test", TaskStatus.NEW, 15,
                    LocalDateTime.of(2023, 3, 5, 2, 10), 1);
            request = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask)))
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());
            request  = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/tasks/subtask/?id=2"))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Type subtaskType = new TypeToken<SubTask>(){}.getType();

            SubTask actual = gson.fromJson(response.body(), subtaskType);
            subTask.setId(2);

            assertEquals(200, response.statusCode(), "Пришел не тот код ответа");
            assertNotNull(actual);
            assertEquals(subTask, actual);

            } catch (IOException | InterruptedException exception) {
                System.out.println("Подзадача не возвращается");
        }
    }

    @Test
    public void shouldDeleteSubtaskById() {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic");
        Epic epic = new Epic("epic", "test", TaskStatus.NEW, 15,
                LocalDateTime.of(2023, 2, 5, 2, 10));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            uri = URI.create("http://localhost:8080/tasks/subtask");
            SubTask subTask = new SubTask("subtask", "test", TaskStatus.NEW, 15,
                    LocalDateTime.of(2023, 3, 5, 2, 10), 1);
            request = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask)))
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/tasks/subtask/?id=2"))
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response.statusCode(), "Пришел не тот код ответа на DELETE-запрос");
        } catch (IOException | InterruptedException exception) {
            System.out.println("Выполнить удаление не получается");
        }
    }

    @Test
    public void shouldDeleteAllSubtasks() {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic");
        Epic epic = new Epic("epic", "test", TaskStatus.NEW, 15,
                LocalDateTime.of(2023, 2, 5, 2, 10));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            uri = URI.create("http://localhost:8080/tasks/subtask");
            SubTask subTask = new SubTask("subtask", "test", TaskStatus.NEW, 15,
                    LocalDateTime.of(2023, 3, 5, 2, 10), 1);
            request = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask)))
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder()
                    .uri(uri)
                    .DELETE()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            HttpRequest requestAfterDelete = HttpRequest.newBuilder().uri(uri).GET().build();
            HttpResponse<String> responseAfterDelete = client.send(requestAfterDelete,
                    HttpResponse.BodyHandlers.ofString());

            JsonArray jsonArrayOfTasks = JsonParser.parseString(responseAfterDelete.body()).getAsJsonArray();

            assertEquals(200, response.statusCode(), "Пришел не тот код ответа на DELETE-запрос");
            assertEquals(0, jsonArrayOfTasks.size(), "Задачи не удалены");
        } catch (IOException | InterruptedException exception) {
            System.out.println("Выполнить удаление не получается");
        }
    }

    @Test
    public void shouldUpdateSubTask() {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic");
        Epic epic = new Epic("epic", "test", TaskStatus.NEW, 15,
                LocalDateTime.of(2023, 2, 5, 2, 10));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            uri = URI.create("http://localhost:8080/tasks/subtask");
            SubTask subTask = new SubTask("subtask", "test", TaskStatus.NEW, 15,
                    LocalDateTime.of(2023, 3, 5, 2, 10), 1);
            request = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask)))
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            subTask.setId(2);
            subTask.setTaskStatus(TaskStatus.IN_PROGRESS);
            request = HttpRequest.newBuilder()
                    .uri(uri).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask))).build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            uri = URI.create("http://localhost:8080/tasks/subtask/?id=2");
            request = HttpRequest.newBuilder().uri(uri).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Task taskFromResponse = gson.fromJson(response.body(), SubTask.class);

            assertEquals(subTask, taskFromResponse);
        } catch (IOException | InterruptedException exception) {
            System.out.println("Ошибка при обновлении подзадачи");
        }
    }

    @Test
    public void shouldGetSubtaskIdByEpicId() {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic");
        Epic epic = new Epic("epic", "test", TaskStatus.NEW, 15,
                LocalDateTime.of(2023, 2, 5, 2, 10));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            uri = URI.create("http://localhost:8080/tasks/subtask");
            SubTask subTask = new SubTask("subtask", "test", TaskStatus.NEW, 15,
                    LocalDateTime.of(2023, 3, 5, 2, 10), 1);
            request = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask)))
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/tasks/subtask/epic/?id=1"))
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonArray jsonArrayOfSubtasksId = JsonParser.parseString(response.body()).getAsJsonArray();

            assertEquals(200, response.statusCode(), "Пришел не тот код ответа");
            assertEquals(1, jsonArrayOfSubtasksId.size());
        } catch (IOException | InterruptedException exception) {
            System.out.println("Ошибка при создании эпика из подзадачи");
        }
    }

    @Test
    public void shouldGetHistory() {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/epic");
        Epic epic = new Epic("epic", "test", TaskStatus.NEW, 15,
                LocalDateTime.of(2023, 2, 5, 2, 10));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            uri = URI.create("http://localhost:8080/tasks/subtask");
            SubTask subTask = new SubTask("subtask", "test", TaskStatus.NEW, 15,
                    LocalDateTime.of(2023, 3, 5, 2, 10), 1);
            request = HttpRequest.newBuilder()
                    .uri(uri)
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask)))
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/epic/?id=1")).GET().build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks/subtask/?id=2"))
                    .GET().build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/tasks/history")).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonArray jsonArrayOfTasks = JsonParser.parseString(response.body()).getAsJsonArray();

            assertEquals(200, response.statusCode(), "Пришел не тот код ответа");
            assertEquals(2, jsonArrayOfTasks.size());

        } catch (IOException | InterruptedException exception) {
            System.out.println("Задача не возвращается");
        }
    }

    @Test
    public void shouldGetPrioritizedTasks() {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/task");
        Task task = new Task("task", "test", TaskStatus.NEW, 15,
                LocalDateTime.of(2023, 2, 5, 2, 10));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task)))
                .build();
        try {
            client.send(request, HttpResponse.BodyHandlers.ofString());
            Task task1 = new Task("task2", "test2", TaskStatus.NEW, 15,
                    LocalDateTime.of(2023, 2, 5, 2, 10));
            request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder().uri(uri).GET().build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/tasks")).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonArray jsonArrayOfTasks = JsonParser.parseString(response.body()).getAsJsonArray();

            assertEquals(200, response.statusCode(), "Пришел не тот код ответа");
            assertEquals(1, jsonArrayOfTasks.size());

        } catch (IOException | InterruptedException exception) {
            System.out.println("Задача не возвращается");
        }
    }
}