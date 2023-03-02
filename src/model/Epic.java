package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

public class Epic extends Task {

    private ArrayList<Integer> subTaskIdList;
    private ArrayList<SubTask> subTasksOfEpic;

    private transient LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description);
        this.taskType = TaskType.EPIC;
        this.subTaskIdList = new ArrayList<>();
        this.subTasksOfEpic = new ArrayList<>();
    }

    public Epic(String title, String description, TaskStatus taskStatus, long duration, LocalDateTime startTime) {
        super(title, description, taskStatus, duration, startTime);
        this.taskType = TaskType.EPIC;
        this.subTaskIdList = new ArrayList<>();
        this.subTasksOfEpic = new ArrayList<>();
    }

    public ArrayList<Integer> getSubTaskIdList() {
        return subTaskIdList;
    }

    public void setSubTaskIdList(int subTaskId) {
        subTaskIdList.add(subTaskId);
    }

    public void setSubTasksOfEpic(SubTask subTask) {
        subTasksOfEpic.add(subTask);
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
        Epic epic = (Epic) obj;
        return Objects.equals(getTitle(), epic.getTitle()) &&
                Objects.equals(getDescription(), epic.getDescription()) &&
                (getId() == epic.getId()) &&
                Objects.equals(getTaskStatus(), epic.getTaskStatus()) &&
                Objects.equals(getStartTime(), epic.getStartTime()) &&
                Objects.equals(getDuration(), epic.getDuration()) &&
                Objects.equals(getEndTime(), epic.getEndTime()) &&
                Objects.equals(taskType, epic.taskType) &&
                Objects.equals(subTaskIdList, epic.subTaskIdList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle(), getDescription(), getId(), getTaskStatus(), subTaskIdList);
    }

    @Override
    public String toString() {
        return "Epic{" + "title= '" + getTitle() + "'," +
            "\ndescription= '" + getDescription() + "'," +
            "\nid= '" + getId() + "'," +
            "\nstartTime= " + (getStartTime() == null ? "null" : getStartTime()) + "," +
            "\nendTime= " + (getEndTime() == null ? "null" : getEndTime()) + "," +
            "\ntaskStatus= '" + getTaskStatus() + "'," +
            "\nsubTaskIdList= '" + getSubTaskIdList() + "'}";
    }

    public void setEpicStartTime(Epic epic) {
        if (subTasksOfEpic.size() != 0) {
            Stream.of(subTasksOfEpic)
                    .flatMap(Collection::stream)
                    .filter(subTask -> subTask.getStartTime() != null)
                    .map(Task::getStartTime)
                    .min(LocalDateTime::compareTo)
                    .ifPresent(epic::setStartTime);
        } else {
            epic.setStartTime(null);
        }
    }

    public void setDurationTime(Epic epic) {
        if (subTasksOfEpic.size() != 0) {
            long duration = Stream.of(subTasksOfEpic)
                    .flatMap(Collection::stream)
                    .filter(subTask -> subTask.getDuration() != 0)
                    .mapToLong(Task::getDuration)
                    .sum();
            epic.setDuration(duration);
        } else {
            epic.setEndTime(null);
        }
    }

    public void setEndTimeForEpic(Epic epic) {
        if (subTasksOfEpic.size() != 0) {
            Stream.of(subTasksOfEpic)
                    .flatMap(Collection::stream)
                    .filter(subTask -> subTask.getEndTime() != null)
                    .map(Task::getEndTime)
                    .max(LocalDateTime::compareTo)
                    .ifPresent(epic::setEndTime);
        } else {
            epic.setEndTime(null);
        }
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }
}
