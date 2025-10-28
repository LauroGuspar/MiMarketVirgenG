package com.sistema.productos.service.Impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Objects;
import java.util.regex.Pattern;
import org.apache.commons.lang3.RandomStringUtils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;

import com.sistema.productos.model.Usuario;
import com.sistema.productos.model.dto.UsuarioDTO;
import com.sistema.productos.repository.UsuarioRepository;
import com.sistema.productos.service.EmailService;
import com.sistema.productos.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService{

    @Value("${password.reset.token.validity.minutes}")
    private int tokenValidityMinutes;

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // Validaciones de Nueva COntraseña
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");
    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*\\d.*");

    @Autowired
    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, BCryptPasswordEncoder passwordEncoder, EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAllByEstadoNot(2);
    }

    private UsuarioDTO convertirAUsuarioDTO(Usuario usuario) {
        if (usuario == null) return null;
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setUsuario(usuario.getUsuario());
        dto.setApellidoPaterno(usuario.getApellidoPaterno());
        dto.setApellidoMaterno(usuario.getApellidoMaterno());
        dto.setCorreo(usuario.getCorreo());
        dto.setTelefono(usuario.getTelefono());
        dto.setDireccion(usuario.getDireccion());
        dto.setNdocumento(usuario.getNdocumento());
        if (usuario.getTipodocumento() != null) {
            dto.setId_tipodocumento(usuario.getTipodocumento().getId());
            dto.setNombre_tipodocumento(usuario.getTipodocumento().getNombre());
        }
        if (usuario.getRol() != null) {
            dto.setId_rol(usuario.getRol().getId());
            dto.setNombre_rol(usuario.getRol().getNombre());
        }
        return dto;
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
                      throw new IllegalArgumentException("Ya existe un usuario con el documento " + usuario.getNdocumento() + ".");
                 }

                String generatedUsuario = generarNombreUsuario(usuario.getNombre(), usuario.getApellidoPaterno());
                usuario.setUsuario(generatedUsuario);
                String generatedClavePlainText = RandomStringUtils.randomAlphanumeric(10);
                usuario.setClave(passwordEncoder.encode(generatedClavePlainText));
                 verificarConflictosUnicosNuevoUsuario(usuario);
                usuario.setEstado(1);
                Usuario usuarioGuardado = usuarioRepository.save(usuario); 

                try {
                    emailService.EnviarCredencialesEmail(usuarioGuardado.getCorreo(), usuarioGuardado.getUsuario(), generatedClavePlainText);
                } catch (Exception e) {
                    System.err.println("WARN: Usuario " + usuarioGuardado.getUsuario() + " creado, pero falló el envío de correo de credenciales: " + e.getMessage());
                }
                System.out.println("Nuevo usuario creado: " + usuarioGuardado.getUsuario() + " (Clave enviada por correo)");
                return usuarioGuardado;

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

    // Clave
    @Override
    @Transactional
    public void iniciarRecuperacionClave(String ndocumento, String correo) {
        if (ndocumento == null || ndocumento.isBlank() || correo == null || correo.isBlank()) {
            throw new IllegalArgumentException("El DNI y el correo son obligatorios.");
        }
        
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNdocumentoAndCorreoIgnoreCase(ndocumento.trim(), correo.trim());

        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("No se encontró un usuario con el DNI y correo electrónico proporcionados.");
        }

        Usuario usuario = usuarioOpt.get();

        if (usuario.getEstado() != 1) {
            throw new IllegalArgumentException("El usuario asociado a estos datos no está activo.");
        }

        String recoveryToken = RandomStringUtils.randomAlphanumeric(20);
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(tokenValidityMinutes);

        usuario.setTokenReseteo(recoveryToken);
        usuario.setTokenReseteoExpira(expiryDate);
        usuarioRepository.save(usuario);

        System.out.println("INFO: Token de recuperación guardado para " + usuario.getUsuario() + ": " + recoveryToken + ", Expira: " + expiryDate);

        try {
            // Modify sendPasswordRecoveryEmail if needed to include a full URL
            // Example URL: "http://localhost:8080/restablecer-clave?token=" + recoveryToken
            // For now, just sending the token code as before.
            emailService.EnviarPasswordRecoveryEmail(usuario.getCorreo(), recoveryToken);
        } catch (Exception e) {
            System.err.println("Error CRÍTICO al enviar correo de recuperación para " + usuario.getUsuario() + ": " + e.getMessage());
            throw new RuntimeException("No se pudo enviar el correo de recuperación. Inténtalo más tarde.", e);
        }
    }

    @Override
    @Transactional
    public void restablecerClave(String token, String nuevaClave) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("El token de restablecimiento es inválido o ha expirado.");
        }
        validatePasswordStrength(nuevaClave);

        if (nuevaClave == null || nuevaClave.trim().length() < 6) {
             throw new IllegalArgumentException("La nueva contraseña debe tener al menos 6 caracteres.");
        }
        Optional<Usuario> usuarioOpt = usuarioRepository.findByTokenReseteo(token);

        if (usuarioOpt.isEmpty()) {
             throw new IllegalArgumentException("El token de restablecimiento es inválido o ha expirado.");
        }

        Usuario usuario = usuarioOpt.get();

        if (usuario.getTokenReseteoExpira() == null || usuario.getTokenReseteoExpira().isBefore(LocalDateTime.now())) {
            usuario.setTokenReseteo(null);
            usuario.setTokenReseteoExpira(null);
            usuarioRepository.save(usuario);
            throw new IllegalArgumentException("El token de restablecimiento es inválido o ha expirado.");
        }

        usuario.setClave(passwordEncoder.encode(nuevaClave.trim()));
        usuario.setTokenReseteo(null);
        usuario.setTokenReseteoExpira(null);

        usuarioRepository.save(usuario);
        System.out.println("Contraseña restablecida exitosamente para usuario: " + usuario.getUsuario());
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
    public Optional<UsuarioDTO> obtenerUsuarioDTOPorId(Long id) {
        return usuarioRepository.findById(id).map(this::convertirAUsuarioDTO);
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

    //Metodos

    // Validación de Contraseña
    private void validatePasswordStrength(String password) {
         if (password == null || password.trim().isEmpty()) {
             throw new IllegalArgumentException("La nueva contraseña no puede estar vacía.");
         }

         String trimmedPassword = password.trim();

         if (trimmedPassword.length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalArgumentException(String.format("La contraseña debe tener al menos %d caracteres.", MIN_PASSWORD_LENGTH));
         }
         if (!UPPERCASE_PATTERN.matcher(trimmedPassword).matches()) {
            throw new IllegalArgumentException("La contraseña debe contener al menos una letra mayúscula.");
         }
         if (!LOWERCASE_PATTERN.matcher(trimmedPassword).matches()) {
            throw new IllegalArgumentException("La contraseña debe contener al menos una letra minúscula.");
         }
         if (!DIGIT_PATTERN.matcher(trimmedPassword).matches()) {
            throw new IllegalArgumentException("La contraseña debe contener al menos un número.");
         }
         if (trimmedPassword.contains(" ")) {
              throw new IllegalArgumentException("La contraseña no debe contener espacios.");
         }
    }

    // Usuarios

    // Nombre de Usuario
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

    // Verificar que el usuario no se repita
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
}