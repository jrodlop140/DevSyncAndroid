package es.iescarrillo.devsync.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class User extends DomainEntity {

    // Atributos //
    private String name;
    private String email;
    private String photo;
    private List<String> skills;
    private Map<String, Boolean> projects; // referencias a proyectos
    private long createdAt;

    // Constructor vacío //
    public User() {
    }

    // Constructor completo //
    public User(String name, String email, String photo, List<String> skills,
                Map<String, Boolean> projects, long createdAt) {
        this.name = name;
        this.email = email;
        this.photo = photo;
        this.skills = skills;
        this.projects = projects;
        this.createdAt = createdAt;
    }

    // Getters y Setters //

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public Map<String, Boolean> getProjects() {
        return projects;
    }

    public void setProjects(Map<String, Boolean> projects) {
        this.projects = projects;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    // Representación en texto del usuario //
    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", skills=" + skills +
                '}';
    }
}