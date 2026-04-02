package es.iescarrillo.devsync.models;

import es.iescarrillo.devsync.enums.RequestState;

/**
 * Modelo auxiliar para mostrar solicitudes con nombre de proyecto.
 */
public class RequestItem {

    private String projectName;
    private RequestState state;
    private String requestDate;

    public RequestItem() {
    }

    public RequestItem(String projectName, RequestState state, String requestDate) {
        this.projectName = projectName;
        this.state = state;
        this.requestDate = requestDate;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
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
}