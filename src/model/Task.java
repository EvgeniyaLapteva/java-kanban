package model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private String title;
    private String description;
    private int id;
    private TaskStatus taskStatus;
    protected TaskType taskType;
    private long duration;
    private LocalDateTime startTime;

    public Task(String title, String description,  TaskStatus taskStatus) {
        this.title = title;
        this.description = description;
        this.taskStatus = taskStatus;
        this.taskType = TaskType.TASK;
    }

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Task(String title, String description, TaskStatus taskStatus, long duration, LocalDateTime startTime) {
        this.title = title;
        this.description = description;
        this.taskStatus = taskStatus;
        this.taskType = TaskType.TASK;
        this.duration = duration;
        this.startTime = startTime;
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
                Objects.equals(startTime, otherTask.startTime) &&
                Objects.equals(duration, otherTask.duration) &&
                Objects.equals(taskType, otherTask.taskType) &&
                Objects.equals(getEndTime(), otherTask.getEndTime()) &&
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
                "\nstartTime= " + (getStartTime() == null ? "null" : getStartTime()) + "," +
                "\nendTime= " + (getEndTime() == null ? "null" : getEndTime()) + "," +
                "\ntaskStatus= '" + taskStatus + "'}";
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public LocalDateTime getEndTime() {
        if (startTime != null) {
            return startTime.plusMinutes(duration);
        } else {
            return null;
        }
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
}
