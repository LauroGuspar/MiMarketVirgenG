package com.sistema.productos.service.Impl;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        if (usuario.getId() == null) {
            Optional<Usuario> existentePorDoc = usuarioRepository.findByTipodocumento_IdAndNdocumento(
                usuario.getTipodocumento().getId(), usuario.getNdocumento());

            if (existentePorDoc.isPresent()) {
                Usuario existente = existentePorDoc.get();
                if (existente.getEstado() == 2) {
                    Optional<Usuario> porUsuario = usuarioRepository.findByUsuarioIgnoreCase(usuario.getUsuario());
                    if (porUsuario.isPresent() && !porUsuario.get().getId().equals(existente.getId())) {
                        throw new IllegalArgumentException("El nombre de usuario ya está en uso por otra cuenta activa.");
                    }
                    Optional<Usuario> porCorreo = usuarioRepository.findByCorreoIgnoreCase(usuario.getCorreo());
                    if (porCorreo.isPresent() && !porCorreo.get().getId().equals(existente.getId())) {
                        throw new IllegalArgumentException("El correo electrónico ya está en uso por otra cuenta activa.");
                    }

                    existente.setEstado(1);
                    existente.setNombre(usuario.getNombre());
                    existente.setUsuario(usuario.getUsuario());
                    existente.setCorreo(usuario.getCorreo());
                    existente.setApellidoPaterno(usuario.getApellidoPaterno());
                    existente.setApellidoMaterno(usuario.getApellidoMaterno());
                    existente.setTelefono(usuario.getTelefono());
                    existente.setDireccion(usuario.getDireccion());
                    existente.setRol(usuario.getRol());
                    existente.setClave(passwordEncoder.encode(usuario.getClave().trim()));
                    
                    return usuarioRepository.save(existente);
                } else {
                    throw new IllegalArgumentException("Ya existe un usuario registrado con este tipo y número de documento.");
                }
            }

            if (usuarioRepository.findByUsuarioIgnoreCase(usuario.getUsuario()).isPresent()) {
                throw new IllegalArgumentException("El nombre de usuario ya está registrado.");
            }
            if (usuarioRepository.findByCorreoIgnoreCase(usuario.getCorreo()).isPresent()) {
                throw new IllegalArgumentException("El correo electrónico ya está registrado.");
            }
            
            usuario.setClave(passwordEncoder.encode(usuario.getClave().trim()));
            usuario.setEstado(1);
            return usuarioRepository.save(usuario);
        }
        else {
             Usuario existente = usuarioRepository.findById(usuario.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado para actualizar"));
            
            if (usuario.getClave() == null || usuario.getClave().trim().isEmpty()) {
                usuario.setClave(existente.getClave());
            } else {
                usuario.setClave(passwordEncoder.encode(usuario.getClave().trim()));
            }
             return usuarioRepository.save(usuario);
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
            throw new IllegalArgumentException("ID de usuario inválido");
        }
        Usuario usuario = obtenerUsuarioPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        usuario.setEstado(2);
        usuarioRepository.save(usuario);
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
        return usuarioRepository.existsByUsuario(nombreUsuario.trim().toLowerCase());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) return false;
        return usuarioRepository.existsByCorreo(correo.trim().toLowerCase());
    }

    @Override
    public boolean verificarContrasena(String contrasenaTextoPlano, String contrasenaEncriptada) {
        return passwordEncoder.matches(contrasenaTextoPlano, contrasenaEncriptada);
    }
}