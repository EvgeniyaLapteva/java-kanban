package model;

import java.util.ArrayList;
import java.util.Objects;


public class Epic extends  Task {

    ArrayList<Integer> subTaskIdList = new ArrayList<>();

    public Epic(String title, String description, TaskStatus taskStatus) {
        super(title, description, taskStatus);
    }

    public ArrayList<Integer> getSubTaskIdList() {
        return subTaskIdList;
    }

    public void setSubTaskIdList(int subTaskId) {
        subTaskIdList.add(subTaskId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Epic epic = (Epic) obj;
        return Objects.equals(title, epic.title) &&
                Objects.equals(description, epic.description) &&
                (id == epic.id) &&
                Objects.equals(taskStatus, epic.taskStatus) &&
                Objects.equals(subTaskIdList, epic.subTaskIdList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, id, taskStatus, subTaskIdList);
    }

    @Override
    public String toString() {
        return "Epic{" + "title= '" + getTitle() + "'," +
                "\ndescription= '" + getDescription() + "'," +
                "\nid= '" + getId() + "'," +
                "\ntaskStatus= '" + getTaskStatus() + "'," +
                "\nsubTaskIdList= '" + getSubTaskIdList() + "'}";
    }

}
