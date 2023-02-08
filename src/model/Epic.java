package model;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {

    private ArrayList<Integer> subTaskIdList = new ArrayList<>();

    public Epic(String title, String description, TaskStatus taskStatus) {
        super(title, description, taskStatus);
        this.taskType = TaskType.EPIC;
    }

    public ArrayList<Integer> getSubTaskIdList() {
        return subTaskIdList;
    }

    public void setSubTaskIdList(int subTaskId) {
        subTaskIdList.add(subTaskId);
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

    public void setSubTaskIdList(ArrayList<Integer> subTaskIdList) {
        this.subTaskIdList = subTaskIdList;
    }
}
