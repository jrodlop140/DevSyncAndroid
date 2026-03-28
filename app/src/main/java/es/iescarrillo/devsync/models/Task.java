package es.iescarrillo.devsync.models;

import com.google.firebase.database.IgnoreExtraProperties;

import es.iescarrillo.devsync.enums.TaskStatus;

@IgnoreExtraProperties
public class Task extends DomainEntity {

    // Atributos //
    private String title;
    private String description;
    private TaskStatus status;
    private String projectId;
    private String assignedDeveloper;
    private String creationDate;
    private long createdAt;

    // Constructor vacío //
    public Task() {
    }

    // Constructor completo //
    public Task(String title, String description, TaskStatus status, String projectId,
                String assignedDeveloper, String creationDate, long createdAt) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.projectId = projectId;
        this.assignedDeveloper = assignedDeveloper;
        this.creationDate = creationDate;
        this.createdAt = createdAt;
    }

    // Getters y Setters //

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

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getAssignedDeveloper() {
        return assignedDeveloper;
    }

    public void setAssignedDeveloper(String assignedDeveloper) {
        this.assignedDeveloper = assignedDeveloper;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    // Representación en texto de la tarea //
    @Override
    public String toString() {
        return "Task{" +
                "title='" + title + '\'' +
                ", status='" + status + '\'' +
                ", projectId='" + projectId + '\'' +
                ", assignedDeveloper='" + assignedDeveloper + '\'' +
                '}';
    }
}