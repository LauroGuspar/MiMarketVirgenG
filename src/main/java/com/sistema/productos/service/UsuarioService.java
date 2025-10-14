package com.sistema.productos.service;

import java.util.List;
import java.util.Optional;

import com.sistema.productos.model.Usuario;

public interface UsuarioService {
    List<Usuario> listarUsuarios();
    Usuario guardarUsuario(Usuario usuario);
    long contarUsuarios();
    Optional<Usuario> obtenerUsuarioPorId(Long id);
    Optional<Usuario> findByUsuario(String usuario);
    void eliminarUsuario(Long id);
    boolean cambiarEstadoUsuario(Long id);
    boolean existeUsuario(String nombreUsuario);
    boolean existeCorreo(String correo);
    boolean verificarContrasena(String contrasenaTextoPlano, String contrasenaEncriptada);
}
