package service;

import model.*;
import service.exception.ManagerSaveException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    private File fileForSave;

    public FileBackedTasksManager(File fileForSave) {
        this.fileForSave = fileForSave;
    }

    public File getFileForSave() {
        return fileForSave;
    }

    public static void main(String[] args) {

        File file = new File("src/resources/history.csv");

        TaskManager taskManager = new FileBackedTasksManager(file);

        Task firstTask = new Task("Выполнить 6 ТЗ", "Разобраться с методами", TaskStatus.IN_PROGRESS);
        Task secondTask = new Task("Забрать детей", "Школа и садик", TaskStatus.NEW);
        Epic firstEpic = new Epic("Сходить за покупками", "По пути в школу", TaskStatus.NEW);
        Epic secondEpic = new Epic("Съездить к бабушке", "Живет далеко", TaskStatus.NEW);
        SubTask firstSubTask = new SubTask("Зайти за курицей", "Куриный у дома", TaskStatus.NEW, 3);
        SubTask secondSubTask = new SubTask("Промтовары", "Магнит у дома", TaskStatus.NEW, 3);

        taskManager.createTask(firstTask);
        taskManager.createTask(secondTask);
        taskManager.createEpic(firstEpic);
        taskManager.createEpic(secondEpic);
        taskManager.createSubtask(firstSubTask);
        taskManager.createSubtask(secondSubTask);

        taskManager.getTaskById(2);
        taskManager.getSubtaskById(5);
        taskManager.getEpicById(3);

        System.out.println("_______________");

        TaskManager loadedTaskManager = loadFromFile(file);

        loadedTaskManager.getHistory();

        System.out.println("_______________");
        System.out.println(loadedTaskManager.getAllTasks());
    }
    public void save() {
        try (FileWriter fileWriter = new FileWriter(fileForSave)) {
            fileWriter.write("id,type,name,status,description,epic\n");
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
            throw new ManagerSaveException();
        }
    }

    public String toString(Task task) {
        return String.format("%d,%S,%s,%S,%s\n", task.getId(),task.getTaskType().toString(), task.getTitle(),
                task.getTaskStatus().toString(), task.getDescription());
    }

    public String toString(Epic epic) {
        return String.format("%d,%S,%s,%S,%s\n", epic.getId(), epic.getEpicType().toString(), epic.getTitle(),
                epic.getTaskStatus().toString(), epic.getDescription());
    }

    public  String toString(SubTask subTask) {
        return String.format("%d,%S,%s,%S,%s,%d\n", subTask.getId(), subTask.getSubTaskType().toString(), subTask.getTitle(),
                subTask.getTaskStatus().toString(), subTask.getDescription(), subTask.getEpicId());
    }

    public Task fromString(String value) {
        String[] items = value.split(",");
        Task task;
        TaskStatus taskStatus;
        if (items[3].length() == 3) {
            taskStatus = TaskStatus.NEW;
        } else if (items[3].length() == 4) {
            taskStatus = TaskStatus.DONE;
        } else {
            taskStatus = TaskStatus.IN_PROGRESS;
        }
        if (items.length > 5 && items[5] != null) {
            task = new SubTask(items[2], items[4], taskStatus, Integer.parseInt(items[5]));
        } else if (items[1].equals("EPIC")) {
            task = new Epic(items[2], items[4], taskStatus);
            task.setTaskStatus(taskStatus);
        } else {
            task = new Task(items[2], items[4], taskStatus);
        }
        task.setId(Integer.parseInt(items[0]));
        return task;
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
        String[] ids = value.split(",");
        for (String item : ids) {
            idFromString.add(Integer.parseInt(item));
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
                if (!line.isEmpty()) {
                    Task task = loadedFromFile.fromString(line);
                    if (task.getTaskType() == TaskType.TASK) {
                        loadedFromFile.addTask(task);
                    }
                    if (task.getTaskType() == TaskType.EPIC) {
                        loadedFromFile.addEpic((Epic) task);

                    }
                    if (task.getTaskType() == TaskType.SUBTASK) {
                        loadedFromFile.addSubTask((SubTask) task);
                    }
                } else {
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
                }
            }

        } catch (IOException exception) {
            System.out.println("Произошла ошибка во время чтения файла");
        }
        return loadedFromFile;
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
        historyManager.add(tasks.get(taskId));
        save();
        return tasks.get(taskId);
    }

    @Override
    public Epic getEpicById(int epicId) {
        if (epics.get(epicId) == null) {
            System.out.println("Эпика с таким id не существует");
        } else {
            historyManager.add(epics.get(epicId));
        }
        save();
        return epics.get(epicId);
    }

    @Override
    public SubTask getSubtaskById(int subTaskId) {
        historyManager.add(subTasks.get(subTaskId));
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
    public void addTask(Task task) {
        super.createTask(task);
    }

    public void addEpic(Epic epic) {
        super.createEpic(epic);
    }

    public void addSubTask(SubTask subTask) {
        super.createSubtask(subTask);
    }
}
