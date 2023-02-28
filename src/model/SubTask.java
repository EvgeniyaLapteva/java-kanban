package model;

import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends  Task {

    private int epicId;

    public SubTask(String title, String description, TaskStatus taskStatus, int epicId) {
        super(title, description, taskStatus);
        this.epicId = epicId;
        this.taskType = TaskType.SUBTASK;
    }

    public SubTask(String title, String description, TaskStatus taskStatus, long duration, LocalDateTime startTime,
                   int epicId) {
        super(title, description, taskStatus, duration, startTime);
        this.epicId = epicId;
        this.taskType = TaskType.SUBTASK;
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
                Objects.equals(getStartTime(), otherTask.getStartTime()) &&
                Objects.equals(getDuration(), otherTask.getDuration()) &&
                Objects.equals(getEndTime(), otherTask.getEndTime()) &&
                Objects.equals(taskType, otherTask.taskType) &&
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
            "\nstartTime= " + (getStartTime() == null ? "null" : getStartTime()) + "," +
            "\nendTime= " + (getEndTime() == null ? "null" : getEndTime()) + "," +
            "\ntaskStatus= '" + getTaskStatus() + "'," +
            "\nepicID= '" + getEpicId() + "'}";
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }


}
