package com.sistema.productos.controller;

import com.sistema.productos.model.Unidad;
import com.sistema.productos.service.UnidadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/productos/unidad")
public class UnidadController {

    private final UnidadService unidadService;

    public UnidadController(UnidadService unidadService) {
        this.unidadService = unidadService;
    }

    @GetMapping("/listar")
    public String mostrarPaginaUnidad() {
        return "unidad";
    }

    @GetMapping("/api/listar")
    @ResponseBody
    public ResponseEntity<?> listarUnidadesApi() {
        return ResponseEntity.ok(Map.of("data", unidadService.listarTodasLasUnidades()));
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerUnidad(@PathVariable Long id) {
        return unidadService.obtenerUnidadPorId(id)
                .map(unidad -> ResponseEntity.ok(Map.of("success", true, "data", unidad)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Unidad no encontrada")));
    }

    @PostMapping("/api/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarUnidad(@RequestBody Unidad unidad) {
        Map<String, Object> response = new HashMap<>();
        try {
            Unidad unidadGuardada = unidadService.guardarUnidad(unidad);
            response.put("success", true);
            response.put("message", unidad.getId() != null ? "Unidad actualizada correctamente" : "Unidad creada correctamente");
            response.put("data", unidadGuardada);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error interno al guardar la unidad.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/api/cambiar-estado/{id}")
    @ResponseBody
    public ResponseEntity<?> cambiarEstadoUnidad(@PathVariable Long id) {
        boolean exito = unidadService.cambiarEstadoUnidad(id);
        if (exito) {
            return ResponseEntity.ok(Map.of("success", true, "message", "Estado de la unidad actualizado"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Unidad no encontrada"));
        }
    }

    @DeleteMapping("/api/eliminar/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarUnidad(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            unidadService.eliminarUnidad(id);
            response.put("success", true);
            response.put("message", "Unidad eliminada correctamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar la unidad: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}