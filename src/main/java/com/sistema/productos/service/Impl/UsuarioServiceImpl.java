package com.sistema.productos.service.Impl;

import java.util.List;
import java.util.Optional;
import java.util.Objects;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

import com.sistema.productos.model.Usuario;
import com.sistema.productos.repository.UsuarioRepository;
import com.sistema.productos.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService{

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAllByEstadoNot(2);
    }

    @Override
    @Transactional
    public Usuario guardarUsuario(Usuario usuario) {
        if (usuario.getCorreo() != null) usuario.setCorreo(usuario.getCorreo().trim());
        if (usuario.getTelefono() != null) usuario.setTelefono(usuario.getTelefono().trim());
        if (usuario.getNdocumento() != null) usuario.setNdocumento(usuario.getNdocumento().trim());

        try {
            if (usuario.getId() == null) {
                if (usuarioRepository.existsByTipodocumento_IdAndNdocumento(usuario.getTipodocumento().getId(), usuario.getNdocumento())) {
                     throw new IllegalArgumentException("Ya existe un usuario (activo o inactivo) con el documento " + usuario.getNdocumento() + ".");
                }
                String generatedUsuario = generarNombreUsuario(usuario.getNombre(), usuario.getApellidoPaterno());
                usuario.setUsuario(generatedUsuario);
                String generatedClave = RandomStringUtils.randomAlphanumeric(10);
                usuario.setClave(passwordEncoder.encode(generatedClave));
                verificarConflictosUnicosNuevoUsuario(usuario);
                usuario.setEstado(1);
                System.out.println("Nuevo usuario creado: " + usuario.getUsuario() + ", Clave Temporal: " + generatedClave);
                return usuarioRepository.save(usuario);

            } else {
                Usuario existente = usuarioRepository.findById(usuario.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + usuario.getId()));
                verificarConflictosUnicosActualizarUsuario(usuario, existente);
                existente.setCorreo(usuario.getCorreo());
                existente.setTelefono(usuario.getTelefono());
                existente.setRol(usuario.getRol());
                return usuarioRepository.save(existente);
            }
        } catch (IllegalArgumentException e) {
             throw e;
        } catch (DataIntegrityViolationException e) {
            String message = "Error de base de datos. Es posible que un campo único ya exista.";
            if (e.getMessage().contains("emple_nombreuser")) message = "El nombre de usuario ya existe.";
            else if (e.getMessage().contains("emple_correo")) message = "El correo electrónico ya existe.";
            else if (e.getMessage().contains("emple_telefono")) message = "El número de teléfono ya existe.";
            else if (e.getMessage().contains("emple_ndocumento")) message = "El número de documento ya existe.";
             System.err.println("DataIntegrityViolationException: " + e.getMessage());
            throw new IllegalArgumentException(message, e);
        } catch (Exception e) {
             System.err.println("Error inesperado en guardarUsuario: " + e.getMessage());
            throw new RuntimeException("Ocurrió un error inesperado al guardar el usuario.", e);
        }
    }

    private String generarNombreUsuario(String nombre, String apellidoPaterno) {
        if (nombre == null || nombre.trim().isEmpty() || apellidoPaterno == null || apellidoPaterno.trim().isEmpty()) {
            throw new IllegalArgumentException("Nombre y Apellido Paterno son requeridos para generar el usuario.");
        }
        String baseUsuario = (nombre.trim().substring(0, 1) + apellidoPaterno.trim()).toLowerCase().replaceAll("\\s+", "");
        String usuarioFinal = baseUsuario;
        int counter = 1;
        while (usuarioRepository.existsByUsuarioIgnoreCase(usuarioFinal)) {
            usuarioFinal = baseUsuario + counter;
            counter++;
        }
        return usuarioFinal;
    }

     private void verificarConflictosUnicosNuevoUsuario(Usuario usuario) {
         if (usuario.getCorreo() != null && !usuario.getCorreo().isBlank() && usuarioRepository.existsByCorreoIgnoreCase(usuario.getCorreo())) {
             throw new IllegalArgumentException("El correo electrónico '" + usuario.getCorreo() + "' ya está registrado.");
         }
         if (usuario.getTelefono() != null && !usuario.getTelefono().isBlank() && usuarioRepository.existsByTelefonoIgnoreCase(usuario.getTelefono())) {
             throw new IllegalArgumentException("El número de teléfono '" + usuario.getTelefono() + "' ya está registrado.");
         }
     }

     private void verificarConflictosUnicosActualizarUsuario(Usuario usuarioActualizado, Usuario usuarioExistente) {
         Long idActual = usuarioExistente.getId();
         if (usuarioActualizado.getCorreo() != null && !usuarioActualizado.getCorreo().isBlank() &&
             !usuarioExistente.getCorreo().equalsIgnoreCase(usuarioActualizado.getCorreo())) {
             Optional<Usuario> otroConCorreo = usuarioRepository.findByCorreoIgnoreCase(usuarioActualizado.getCorreo());
             if (otroConCorreo.isPresent() && !Objects.equals(otroConCorreo.get().getId(), idActual) && otroConCorreo.get().getEstado() != 2) {
                 throw new IllegalArgumentException("El correo electrónico '" + usuarioActualizado.getCorreo() + "' ya está registrado por otro usuario.");
             }
         }
         if (usuarioActualizado.getTelefono() != null && !usuarioActualizado.getTelefono().isBlank() &&
             !usuarioExistente.getTelefono().equalsIgnoreCase(usuarioActualizado.getTelefono())) {
             Optional<Usuario> otroConTelefono = usuarioRepository.findByTelefonoIgnoreCase(usuarioActualizado.getTelefono());
             if (otroConTelefono.isPresent() && !Objects.equals(otroConTelefono.get().getId(), idActual) && otroConTelefono.get().getEstado() != 2) {
                 throw new IllegalArgumentException("El número de teléfono '" + usuarioActualizado.getTelefono() + "' ya está registrado por otro usuario.");
             }
         }
     }

    @Override
    @Transactional(readOnly = true)
    public long contarUsuarios() {
        return usuarioRepository.countByEstadoNot(2);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> obtenerUsuarioPorId(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return usuarioRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Usuario> findByUsuario(String usuario) {
        return usuarioRepository.findByUsuario(usuario.trim().toLowerCase());
    }

    @Override
    @Transactional
    public void eliminarUsuario(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de usuario inválido: " + id);
        }
        Usuario usuario = obtenerUsuarioPorId(id).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
        usuario.setEstado(2);
        usuarioRepository.save(usuario);
        System.out.println("Usuario ID " + id + " marcado como eliminado.");
    }

    @Override
    @Transactional
    public boolean cambiarEstadoUsuario(Long id) {
        if (id == null || id <= 0) {
            return false;
        }

        Optional<Usuario> usuarioOpt = obtenerUsuarioPorId(id);

        if (usuarioOpt.isEmpty()) {
            return false;
        }

        Usuario usuario = usuarioOpt.get();
        if (usuario.getEstado() == 1) {
            usuarioRepository.actualizarEstado(id, 0);
        } else if (usuario.getEstado() == 0) {
            usuarioRepository.actualizarEstado(id, 1);
        }
        return true;
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existeUsuario(String nombreUsuario) {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) return false;
        return usuarioRepository.existsByUsuarioIgnoreCase(nombreUsuario);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) return false;
        return usuarioRepository.existsByCorreoIgnoreCase(correo);
    }

    @Override
    public boolean verificarContrasena(String contrasenaTextoPlano, String contrasenaEncriptada) {
        return passwordEncoder.matches(contrasenaTextoPlano, contrasenaEncriptada);
    }
}