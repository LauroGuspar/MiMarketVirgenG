package com.sistema.productos.controller;

import java.util.HashMap;
import java.util.Map;

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

import com.sistema.productos.model.Marca;
import com.sistema.productos.service.MarcaService;

@Controller
@RequestMapping("/productos/marcas")
public class MarcaController {

    private final MarcaService marcaService;

    public MarcaController(MarcaService marcaService) {
        this.marcaService = marcaService;
    }

    @GetMapping("/listar")
    public String mostrarPaginaMarcas() {
        return "marcas";
    }

    @GetMapping("/api/listar")
    @ResponseBody
    public ResponseEntity<?> listarMarcasApi() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", marcaService.listarTodasLasMarcas());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerMarca(@PathVariable Long id) {
        return marcaService.obtenerMarcaPorId(id)
                .map(marca -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("data", marca);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/guardar")
    @ResponseBody
    public ResponseEntity<?> guardarMarca(@RequestBody Marca marca) {
        Map<String, Object> response = new HashMap<>();
        try {
            Marca marcaGuardada = marcaService.guardarMarca(marca);
            response.put("success", true);
            response.put("marca", marcaGuardada);
            response.put("message", marca.getId() != null ? "Marca actualizada" : "Marca creada");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error interno del servidor.");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/api/cambiar-estado/{id}")
    @ResponseBody
    public ResponseEntity<?> cambiarEstadoMarca(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        boolean exito = marcaService.cambiarEstadoMarca(id);
        if (exito) {
            response.put("success", true);
            response.put("message", "Estado de la marca actualizado");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Marca no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/api/eliminar/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarMarca(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            marcaService.eliminarMarca(id);
            response.put("success", true);
            response.put("message", "Marca eliminada correctamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar la marca: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}