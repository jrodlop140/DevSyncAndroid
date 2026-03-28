package es.iescarrillo.devsync.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;
import java.util.Map;

import es.iescarrillo.devsync.enums.ProjectStatus;

@IgnoreExtraProperties
public class Project extends DomainEntity {

    // Atributos //
    private String name;
    private String description;
    private String creator;
    private Map<String, Boolean> participants; // ids de usuarios
    private List<String> technologies;
    private long createdAt;
    private ProjectStatus status;

    // Constructor vacío //
    public Project() {
    }

    // Constructor completo //
    public Project(String name, String description, String creator,
                   Map<String, Boolean> participants, List<String> technologies,
                   ProjectStatus status, long createdAt) {
        this.name = name;
        this.description = description;
        this.creator = creator;
        this.participants = participants;
        this.technologies = technologies;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters y Setters //

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Map<String, Boolean> getParticipants() {
        return participants;
    }

    public void setParticipants(Map<String, Boolean> participants) {
        this.participants = participants;
    }

    public List<String> getTechnologies() {
        return technologies;
    }

    public void setTechnologies(List<String> technologies) {
        this.technologies = technologies;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    // Representación en texto del proyecto //
    @Override
    public String toString() {
        return "Project{" +
                "name='" + name + '\'' +
                ", creator='" + creator + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}