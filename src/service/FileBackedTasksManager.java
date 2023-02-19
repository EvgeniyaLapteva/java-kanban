package service;

import model.*;
import service.exception.ManagerSaveException;
import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    private File fileForSave;

    protected DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public FileBackedTasksManager(File fileForSave) {
        this.fileForSave = fileForSave;
    }

    public static void main(String[] args) {

        File file = new File("src/resources/history.csv");

        TaskManager taskManager = new FileBackedTasksManager(file);

        Task firstTask = new Task("Выполнить 6 ТЗ", "Разобраться с методами", TaskStatus.IN_PROGRESS);
        Task secondTask = new Task("Забрать детей", "Школа и садик", TaskStatus.NEW);
        Epic firstEpic = new Epic("Сходить за покупками", "По пути в школу");
        Epic secondEpic = new Epic("Съездить к бабушке", "Живет далеко");
        SubTask firstSubTask = new SubTask("Зайти за курицей", "Куриный у дома", TaskStatus.NEW, 3);
        SubTask secondSubTask = new SubTask("Промтовары", "Магнит у дома", TaskStatus.NEW, 3);
        Task taskTime = new Task("taskWithTime", "new", TaskStatus.NEW, Duration.ofMinutes(60),
                LocalDateTime.of(2023, 2, 25, 18, 0));
        SubTask subTask = new SubTask("title", "withTime", TaskStatus.NEW, Duration.ofMinutes(30),
                LocalDateTime.of(2023, 3, 15, 8, 0), 3);

        taskManager.createTask(firstTask);
        taskManager.createTask(secondTask);
        taskManager.createEpic(firstEpic);
        taskManager.createEpic(secondEpic);
        taskManager.createSubtask(firstSubTask);
        taskManager.createSubtask(secondSubTask);
        taskManager.createTask(taskTime);
        taskManager.createSubtask(subTask);

        taskManager.getTaskById(2);
        taskManager.getSubtaskById(5);
        taskManager.getEpicById(3);

        System.out.println("_______________");

        TaskManager loadedTaskManager = loadFromFile(file);

        System.out.println(loadedTaskManager.getAllTasks());
        System.out.println("_______________");
        System.out.println(loadedTaskManager.getAllSubtasks());
        System.out.println("_______________");
        System.out.println(loadedTaskManager.getAllEpics());
        System.out.println("_______________");
        System.out.println(loadedTaskManager.getHistory());

    }
    public void save() {
        try (FileWriter fileWriter = new FileWriter(fileForSave)) {
            fileWriter.write("id,type,name,status,description,startTime,Duration,epic\n");
            for (Task task: tasks.values()) {
                fileWriter.write(toString(task));
            }
            for (Epic epic: epics.values()) {
                fileWriter.write(toString(epic));
            }
            for (SubTask subTask: subTasks.values()) {
                fileWriter.write(toString(subTask));
            }
            fileWriter.write("\n" + historyToString(historyManager));
        } catch (IOException exception) {
            throw new ManagerSaveException("Не удается выполнить сохранение в файл");
        }
    }

    public String toString(Task task) {
        return String.format("%d,%S,%s,%S,%s,%s,%s\n", task.getId(),task.getTaskType().toString(), task.getTitle(),
                task.getTaskStatus().toString(), task.getDescription(), (task.getStartTime() == null ? "null" :
                        task.getStartTime().format(formatter)), (task.getDuration() == null) ? "null" :
                        task.getDuration().toString());
    }

    public String toString(Epic epic) {
        return String.format("%d,%S,%s,%S,%s,%s,%s\n", epic.getId(), epic.getTaskType().toString(), epic.getTitle(),
                epic.getTaskStatus().toString(), epic.getDescription(), (epic.getStartTime() == null ? "null" :
                        epic.getStartTime().format(formatter)), (epic.getDuration() == null ? "null" :
                        epic.getDuration().toString()));
    }

    public  String toString(SubTask subTask) {
        return String.format("%d,%S,%s,%S,%s,%s,%s,%d\n", subTask.getId(), subTask.getTaskType().toString(), subTask.getTitle(),
                subTask.getTaskStatus().toString(), subTask.getDescription(), (subTask.getStartTime() == null ? "null" :
                        subTask.getStartTime().format(formatter)), (subTask.getDuration() == null ? "null" :
                        subTask.getDuration().toString()), subTask.getEpicId());
    }

    public Task fromString(String value) {
            String[] items = value.split(",");
            Task task;
            LocalDateTime startTime = items[5].equals("null") ? null : LocalDateTime.parse(items[5], formatter);
            Duration duration = items[6].equals("null") ? null : Duration.parse(items[6]);
            if (items[1].equals("SUBTASK")) {
                task = new SubTask(items[2], items[4], checkStatus(items), Integer.parseInt(items[7]));
            } else if (items[1].equals("EPIC")) {
                task = new Epic(items[2], items[4]);
                task.setTaskStatus(checkStatus(items));
            } else {
                task = new Task(items[2], items[4], checkStatus(items));
            }
            task.setId(Integer.parseInt(items[0]));
            task.setStartTime(startTime);
            task.setDuration(duration);
            return task;
    }

        public TaskStatus checkStatus(String[] items) {
        TaskStatus taskStatus;
        if (items[3].equals("NEW")) {
            taskStatus = TaskStatus.NEW;
            return taskStatus;
        } else if (items[3].equals("DONE")) {
            taskStatus = TaskStatus.DONE;
            return taskStatus;
        } else {
            taskStatus = TaskStatus.IN_PROGRESS;
            return taskStatus;
        }
    }

    public static String historyToString(HistoryManager manager) {
        StringBuilder viewedTasksIds = new StringBuilder();
        for (Task task: manager.getHistory()) {
            viewedTasksIds.append(task.getId()).append(",");
        }
        return viewedTasksIds.toString();
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> idFromString = new ArrayList<>();
        if (value != null) {
            String[] ids = value.split(",");
            for (String item : ids) {
                idFromString.add(Integer.parseInt(item));
            }
            return idFromString;
        }
        return idFromString;
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager loadedFromFile = new FileBackedTasksManager(file);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            bufferedReader.readLine();
            while (bufferedReader.ready()) {
                line = bufferedReader.readLine();
                if (line.equals("")) {
                    break;
                } else if (!line.isEmpty()) {
                    Task task = loadedFromFile.fromString(line);
                    checkTaskClass(loadedFromFile, task);
                }
            }
            String nextLine = bufferedReader.readLine();
            List<Integer> ids = historyFromString(nextLine);
            for (Integer id : ids) {
                if (loadedFromFile.tasks.get(id) != null) {
                    loadedFromFile.historyManager.add(loadedFromFile.tasks.get(id));
                }
                if (loadedFromFile.epics.get(id) != null) {
                    loadedFromFile.historyManager.add(loadedFromFile.epics.get(id));
                }
                if (loadedFromFile.subTasks.get(id) != null) {
                    loadedFromFile.historyManager.add(loadedFromFile.subTasks.get(id));
                }
            }
        } catch (IOException exception) {
            System.out.println("Произошла ошибка во время чтения файла");
        }
        return loadedFromFile;
    }

    public static void checkTaskClass(FileBackedTasksManager loadedFromFile, Task task) {
        if (task.getClass() == Task.class) {
            task.setId(task.getId());
            loadedFromFile.tasks.put(task.getId(), task);
        }
        if (task.getClass() == Epic.class) {
            task.setId(task.getId());
            loadedFromFile.epics.put(task.getId(), (Epic) task);
        }
        if (task.getClass() == SubTask.class) {
            task.setId(task.getId());
            Epic epic = loadedFromFile.epics.get(((SubTask) task).getEpicId());
            if (epic != null) {
                loadedFromFile.subTasks.put(task.getId(), (SubTask) task);
                epic.setSubTaskIdList(task.getId());
                loadedFromFile.updateLoadedEpicStatus(epic);
            }
        }
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(SubTask subTask) {
        super.createSubtask(subTask);
        save();
    }

    @Override
    public Task getTaskById(int taskId) {
        if (tasks.get(taskId) != null) {
            historyManager.add(tasks.get(taskId));
        } else {
            throw new IllegalArgumentException("Отсутствует задача по выбранному идентификатору");
        }
        save();
        return tasks.get(taskId);
    }

    @Override
    public Epic getEpicById(int epicId) {
        if (epics.get(epicId) != null) {
            historyManager.add(epics.get(epicId));
        } else {
            throw new IllegalArgumentException("Отсутствует эпик по выбранному идентификатору");
        }
        save();
        return epics.get(epicId);
    }

    @Override
    public SubTask getSubtaskById(int subTaskId) {
        if (subTasks.get(subTaskId) != null) {
            historyManager.add(subTasks.get(subTaskId));
        } else {
            throw new IllegalArgumentException("Отсутствует подзадача по выбранному идентификатору");
        }
        save();
        return subTasks.get(subTaskId);
    }

    @Override
    public void updateTask(Task task, int taskId) {
        super.updateTask(task, taskId);
        save();
    }

    @Override
    public void updateEpic(Epic epic, int epicId) {
        super.updateEpic(epic, epicId);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask, int subTaskID) {
        super.updateSubTask(subTask, subTaskID);
        save();
    }

    @Override
    public void updateEpicStatus(Epic epic) {
        super.updateEpicStatus(epic);
        save();
    }

    public void updateLoadedEpicStatus(Epic epic) {
        super.updateEpicStatus(epic);
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        ArrayList<Task> listOfTasks = new ArrayList<>(tasks.values());
        for (Task task: listOfTasks) {
            historyManager.add(task);
        }
        save();
        return  listOfTasks;
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        ArrayList<Epic> listOfEpics = new ArrayList<>(epics.values());
        for (Epic epic: listOfEpics) {
            historyManager.add(epic);
        }
        save();
        return listOfEpics;
    }

    @Override
    public ArrayList<SubTask> getAllSubtasks() {
        ArrayList<SubTask> listOfSubtasks = new ArrayList<>(subTasks.values());
        for (SubTask subTask: listOfSubtasks) {
            historyManager.add(subTask);
        }
        save();
        return listOfSubtasks;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }


    @Override
    public void deleteTaskById(int taskId) {
        super.deleteTaskById(taskId);
        save();
    }

    @Override
    public void deleteEpicById(int epicId) {
        super.deleteEpicById(epicId);
        save();
    }

    @Override
    public void deleteSubTaskById(int subTaskId) {
        super.deleteSubTaskById(subTaskId);
        save();
    }

}
