package model;

import java.util.Objects;

public class SubTask extends  Task {

    private int epicId;

    private final TaskType subTaskType = TaskType.SUBTASK;

    public SubTask(String title, String description, TaskStatus taskStatus, int epicId) {
        super(title, description, taskStatus);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
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
        SubTask otherTask = (SubTask) obj;
        return Objects.equals(getTitle(), otherTask.getTitle()) &&
                Objects.equals(getDescription(), otherTask.getDescription()) &&
                (getId() == otherTask.getId()) &&
                Objects.equals(getTaskStatus(), otherTask.getTaskStatus()) &&
                (epicId == otherTask.epicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle(), getDescription(), getId(), getTaskStatus(), epicId);
    }

    @Override
    public String toString() {
        return "SubTask{" + "title= '" + getTitle() + "'," +
                "\ndescription= '" + getDescription() + "'," +
                "\nid= '" + getId() + "'," +
                "\ntaskStatus= '" + getTaskStatus() + "'," +
                "\nepicID= '" + getEpicId() + "'}";
    }

    public TaskType getSubTaskType() {
        return subTaskType;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}
