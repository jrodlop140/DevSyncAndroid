package es.iescarrillo.devsync.models;

import com.google.firebase.database.IgnoreExtraProperties;

import es.iescarrillo.devsync.enums.RequestState;

@IgnoreExtraProperties
public class Request extends DomainEntity {

    // Atributos //
    private String developerId;
    private String projectId;
    private RequestState state;
    private String requestDate;
    private long createdAt;

    // Constructor vacío //
    public Request() {
    }

    // Constructor completo //
    public Request(String developerId, String projectId, RequestState state,
                   String requestDate, long createdAt) {
        this.developerId = developerId;
        this.projectId = projectId;
        this.state = state;
        this.requestDate = requestDate;
        this.createdAt = createdAt;
    }

    // Getters y Setters //

    public String getDeveloperId() {
        return developerId;
    }

    public void setDeveloperId(String developerId) {
        this.developerId = developerId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public RequestState getState() {
        return state;
    }

    public void setState(RequestState state) {
        this.state = state;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    // Representación en texto de la solicitud //
    @Override
    public String toString() {
        return "Request{" +
                "developerId='" + developerId + '\'' +
                ", projectId='" + projectId + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}