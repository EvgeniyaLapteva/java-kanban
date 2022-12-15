package model;

import java.util.Objects;

public class SubTask extends  Task {

    private int epicId;

    public SubTask(String title, String description, TaskStatus taskStatus, int epicId) {
        super(title, description, taskStatus);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        SubTask otherTask = (SubTask) obj;
        return Objects.equals(title, otherTask.title) &&
                Objects.equals(description, otherTask.description) &&
                (id == otherTask.id) &&
                Objects.equals(taskStatus, otherTask.taskStatus) &&
                (epicId == otherTask.epicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, id, taskStatus, epicId);
    }

    @Override
    public String toString() {
        return "SubTask{" + "title= '" + getTitle() + "'," +
                "\ndescription= '" + getDescription() + "'," +
                "\nid= '" + getId() + "'," +
                "\ntaskStatus= '" + getTaskStatus() + "'," +
                "\nepicID= '" + getEpicId() + "'}";
    }

}
