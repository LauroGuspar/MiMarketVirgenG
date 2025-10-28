package com.sistema.productos.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sistema.productos.model.Rol;
import com.sistema.productos.service.RolService;

@Controller
@RequestMapping("/empleados/roles")
public class RolController {

    private final RolService rolService;

    public RolController(RolService rolService) {
        this.rolService = rolService;
    }

    @GetMapping("/listar")
    public String mostrarPaginaRoles() {
        return "roles";
    }

    @GetMapping("/api/listar")
    @ResponseBody
    public ResponseEntity<?> listarRolesApi() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", rolService.listarTodosLosRoles());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerRol(@PathVariable Long id) {
        return rolService.obtenerRolPorId(id)
                .map(rol -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    Map<String, Object> rolData = new HashMap<>();
                    rolData.put("id", rol.getId());
                    rolData.put("nombre", rol.getNombre());
                    rolData.put("descripcion", rol.getDescripcion());
                    rolData.put("estado", rol.getEstado());
                    rolData.put("opciones",
                            rol.getOpciones().stream().map(op -> op.getId()).collect(Collectors.toSet()));

                    response.put("data", rolData);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarRol(@RequestBody Rol rol) {
        Map<String, Object> response = new HashMap<>();
        try {
            Rol rolGuardado = rolService.guardarRol(rol);
            response.put("success", true);
            response.put("message", rol.getId() != null ? "Rol actualizado" : "Rol creado");
            response.put("rol", rolGuardado);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al guardar el rol: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/api/cambiar-estado/{id}")
    @ResponseBody
    public ResponseEntity<?> cambiarEstadoRol(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        return rolService.cambiarEstadoRol(id)
                .map(rol -> {
                    response.put("success", true);
                    response.put("message", "Estado del rol actualizado");
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    response.put("success", false);
                    response.put("message", "Rol no encontrado");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }

    @GetMapping("/api/opciones")
    @ResponseBody
    public ResponseEntity<?> listarOpcionesApi() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", rolService.listarTodasLasOpciones());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/api/eliminar/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarRol(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            rolService.eliminarRol(id);
            response.put("success", true);
            response.put("message", "Rol eliminado correctamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar el Rol: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}