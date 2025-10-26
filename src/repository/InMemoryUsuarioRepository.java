package repository;

import model.RolUsuario;
import model.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryUsuarioRepository implements UsuarioRepository {
    private final List<Usuario> usuarios = new ArrayList<>();

    public InMemoryUsuarioRepository() {
        usuarios.add(new Usuario(1L, "Admin", "admin@sgcc.local", RolUsuario.ADMIN));
        usuarios.add(new Usuario(2L, "Voluntario", "vol@sgcc.local", RolUsuario.VOLUNTARIO));
        usuarios.add(new Usuario(10L, "Ana Beneficiaria", "ana@sgcc.local", RolUsuario.BENEFICIARIO));
    }

    @Override public List<Usuario> findAll() { return usuarios; }

    @Override public Optional<Usuario> findById(Long id) {
        return usuarios.stream().filter(u -> u.getId().equals(id)).findFirst();
    }
}
