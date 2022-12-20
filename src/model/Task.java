package model;

import java.util.Objects;

public class Task {
    private String title;
    private String description;
    private    int id;
    private TaskStatus taskStatus;


    public Task(String title, String description,  TaskStatus taskStatus) {
        this.title = title;
        this.description = description;
        this.taskStatus = taskStatus;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }



    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Task otherTask = (Task) obj;
        return Objects.equals(title, otherTask.title) &&
                Objects.equals(description, otherTask.description) &&
                (id == otherTask.id) &&
                Objects.equals(taskStatus, otherTask.taskStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, id, taskStatus);
    }

    @Override
    public String toString() {
        return "Task{" + "title= '" + title + "'," +
                        "\ndescription= '" + description + "'," +
                        "\nid= '" + id + "'," +
                        "\ntaskStatus= '" + taskStatus + "'}";
    }



}
