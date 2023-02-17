package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

public class Epic extends Task {

    private ArrayList<Integer> subTaskIdList = new ArrayList<>();
    private ArrayList<SubTask> subTasksOfEpic = new ArrayList<>();

    private LocalDateTime endTime;

    public Epic(String title, String description) {
        super(title, description);
        this.taskType = TaskType.EPIC;
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
                "\ntaskStatus= '" + getTaskStatus() + "'," +
                "\nsubTaskIdList= '" + getSubTaskIdList() + "'}";
    }

    public void setEpicStartTime(Epic epic) {
        if (subTasksOfEpic.size() == 0) {
            epic.setStartTime(null);
        }
        Stream.of(subTasksOfEpic)
                .flatMap(Collection::stream)
                .filter(subTask -> subTask.getStartTime() != null)
                .map(Task::getStartTime)
                .min(LocalDateTime::compareTo)
                .ifPresent(epic::setStartTime);
    }

    public void setDurationTime(Epic epic) {
        if (subTasksOfEpic.size() == 0) {
            epic.setEndTime(null);
        }
        long durationTime = Stream.of(subTasksOfEpic)
                .flatMap(Collection::stream)
                .filter(subTask -> subTask.getDuration() != null)
                .map(Task::getDuration)
                .mapToLong(Duration::toMinutes)
                .sum();
        epic.setDuration(Duration.ofMinutes(durationTime));
    }

    public void setEndTimeForEpic(Epic epic) {
        if (subTasksOfEpic.size() == 0) {
            epic.setEndTime(null);
        }
        Stream.of(subTasksOfEpic)
                .flatMap(Collection::stream)
                .filter(subTask -> subTask.getEndTime() != null)
                .map(Task::getEndTime)
                .max(LocalDateTime::compareTo)
                .ifPresent(epic::setEndTime);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }
}
