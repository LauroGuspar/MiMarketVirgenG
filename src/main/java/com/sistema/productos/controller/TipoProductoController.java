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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sistema.productos.model.TipoProducto;
import com.sistema.productos.service.TipoProductoService;

@Controller
@RequestMapping("/productos/tipos-producto") 
public class TipoProductoController {

    private final TipoProductoService tipoProductoService;

    public TipoProductoController(TipoProductoService tipoProductoService) {
        this.tipoProductoService = tipoProductoService;
    }

    @GetMapping("/listar")
    public String mostrarPaginaTiposProducto() {
        return "tipos-producto"; 
    }

    @GetMapping("/api/listar")
    @ResponseBody
    public ResponseEntity<?> listarTiposProducto(@RequestParam(required = false) Long idCategoria) {
        return ResponseEntity.ok(Map.of("success", true, "data", 
                tipoProductoService.listarTodosLosTiposProducto(idCategoria)));
    }

    @PostMapping(value = "/api/guardar")
    @ResponseBody
    @SuppressWarnings("UseSpecificCatch")
    public ResponseEntity<?> guardarTipoProducto(@RequestBody TipoProducto tipoProducto) {
        Map<String, Object> response = new HashMap<>();
        try {
            TipoProducto tipoGuardado = tipoProductoService.guardarTipoProducto(tipoProducto);
            response.put("success", true);
            response.put("message", "Tipo de producto guardado correctamente");
            response.put("producto", tipoGuardado);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al guardar el tipo de producto: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerTipoProducto(@PathVariable Long id) {
        // Llama al nuevo método del servicio que devuelve un DTO
        return tipoProductoService.obtenerTipoProductoDTOPorId(id) 
                .map(tipoDTO -> ResponseEntity.ok(Map.of("success", true, "data", tipoDTO)))
                .orElse(ResponseEntity.status(404).body(Map.of("success", false, "message", "Tipo de producto no encontrado")));
    }

    @PostMapping("/api/cambiar-estado/{id}")
    @ResponseBody
    public ResponseEntity<?> cambiarEstadoTipoProducto(@PathVariable Long id) {
        // ... (sin cambios)
         Map<String, Object> response = new HashMap<>();
        boolean exito = tipoProductoService.cambiarEstadoTipoProducto(id);
        if (exito) {
            response.put("success", true);
            response.put("message", "Estado del tipo de producto actualizado");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Tipo de producto no encontrado o está eliminado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/api/eliminar/{id}")
    @ResponseBody
    @SuppressWarnings("UseSpecificCatch")
    public ResponseEntity<?> eliminarTipoProducto(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            tipoProductoService.eliminarTipoProducto(id);
            response.put("success", true);
            response.put("message", "Tipo de producto eliminado correctamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar el tipo de producto: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}