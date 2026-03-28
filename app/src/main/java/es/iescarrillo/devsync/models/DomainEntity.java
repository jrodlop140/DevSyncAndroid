package es.iescarrillo.devsync.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public abstract class DomainEntity implements Serializable {

    /*Atributos de la Clase*/
    private String id;

    /*Constructor de la Clase*/
    public DomainEntity() {
    }

    /*Getters y Setters de la Clase*/
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}

