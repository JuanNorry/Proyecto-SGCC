package model;

public class Usuario {
    private Long id;
    private String nombre;
    private String email;
    private RolUsuario rol;

    public Usuario(Long id, String nombre, String email, RolUsuario rol) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public RolUsuario getRol() { return rol; }

    @Override public String toString() {
        return nombre + " (" + rol + ")";
    }
}
