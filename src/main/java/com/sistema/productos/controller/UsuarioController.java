package com.sistema.productos.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sistema.productos.model.Usuario;
import com.sistema.productos.service.RolService;
import com.sistema.productos.service.TipoDocumentoService;
import com.sistema.productos.service.UsuarioService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/empleados/")
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final RolService rolService;
    private final TipoDocumentoService tipoDocumentoService;

    public UsuarioController(UsuarioService usuarioService, RolService rolService, TipoDocumentoService tipoDocumentoService) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
        this.tipoDocumentoService = tipoDocumentoService;
    }

    @GetMapping("/listar")
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("formUsuario", new Usuario());
        return "usuarios";
    }
    @GetMapping("/api/listar")
    @ResponseBody
    public ResponseEntity<?> listarUsuariosApi() {
        Map<String, Object> response = new HashMap<>();
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        response.put("success", true);
        response.put("data", usuarios);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/roles")
    @ResponseBody
    public ResponseEntity<?> listarRolesActivosApi() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", rolService.listarTodosLosRoles());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/api/tipodocumento")
    @ResponseBody
    public ResponseEntity<?> listarTiposDocumentosApi() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", tipoDocumentoService.listarTiposDocumentoActivos());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarUsuarioAjax(@Valid @RequestBody Usuario usuario, BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();

        if (bindingResult.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> errores.put(error.getField(), error.getDefaultMessage()));
            response.put("success", false);
            response.put("message", "Error de validación. Revise los campos.");
            response.put("errors", errores);
             System.err.println("Validation Errors (@Valid): " + errores);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Usuario usuarioGuardado = usuarioService.guardarUsuario(usuario);

            response.put("success", true);
            response.put("usuario", usuarioGuardado);
            response.put("message",
                    (usuario.getId() != null && usuarioGuardado.getId() != null)
                     ? "Usuario actualizado correctamente"
                     : "Usuario creado/reactivado correctamente");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
             System.err.println("Business Logic Error (guardarUsuario): " + e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
             System.err.println("Unexpected Error (guardarUsuario): " + e.getMessage());
            response.put("success", false);
            response.put("message", "Error interno del servidor al guardar el usuario.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerUsuario(@PathVariable Long id) {
        try {
            return usuarioService.obtenerUsuarioPorId(id).map(usuario -> {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", usuario);
                return ResponseEntity.ok(response);
            }).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al obtener usuario: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @DeleteMapping("/api/eliminar/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarUsuarioAjax(@PathVariable Long id, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
            if (usuarioLogueado == null) {
                 response.put("success", false);
                 response.put("message", "Error: Sesión no válida o expirada.");
                 return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            if (Objects.equals(usuarioLogueado.getId(), id)) {
                 response.put("success", false);
                 response.put("message", "Operación no permitida: No puedes eliminar tu propia cuenta.");
                  System.out.println("Self-deletion attempt blocked for user ID: " + id);
                 return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            usuarioService.eliminarUsuario(id);

            response.put("success", true);
            response.put("message", "Usuario marcado como eliminado correctamente.");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
             response.put("success", false);
             response.put("message", e.getMessage());
             System.err.println("Error deleting user (IllegalArgument): " + e.getMessage());
             if (e.getMessage().contains("no encontrado")) {
                 return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
             } else {
                 return ResponseEntity.badRequest().body(response);
             }
        }
         catch (Exception e) {
             System.err.println("Unexpected Error deleting user ID " + id + ": " + e.getMessage());
            response.put("success", false);
            response.put("message", "Error interno del servidor al eliminar el usuario.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/api/cambiar-estado/{id}")
    @ResponseBody
    public ResponseEntity<?> cambiarEstadoUsuarioAjax(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean exito = usuarioService.cambiarEstadoUsuario(id);
            if (exito) {
                response.put("success", true);
                response.put("message", "Estado del usuario actualizado correctamente");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Usuario no encontrado o la operación no pudo realizarse");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al cambiar estado: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}