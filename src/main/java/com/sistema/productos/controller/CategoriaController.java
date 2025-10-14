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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistema.productos.model.Categoria;
import com.sistema.productos.service.CategoriaService;

@Controller
@RequestMapping("/productos/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping("/listar")
    public String mostrarPaginaCategorias() {
        return "categorias";
    }

    @GetMapping("/api/listar")
    @ResponseBody
    public ResponseEntity<?> listarCategoriasApi() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", categoriaService.listarTodasLasCategorias());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerCategoria(@PathVariable Long id) {
        return categoriaService.obtenerCategoriaPorId(id)
                .map(categoria -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("data", categoria);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(value = "/api/guardar", consumes = {"multipart/form-data"})
    @ResponseBody
    @SuppressWarnings("UseSpecificCatch")
    public ResponseEntity<?> guardarCategoria(
            @RequestParam("categoria") String categoriaJson,
            @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Categoria categoria = objectMapper.readValue(categoriaJson, Categoria.class);
            if (categoria.getId() != null && imagenFile == null) {
                categoriaService.obtenerCategoriaPorId(categoria.getId()).ifPresent(catExistente -> {
                    categoria.setImg(catExistente.getImg());
                });
            }

            Categoria categoriaGuardada = categoriaService.guardarCategoria(categoria, imagenFile);
            
            response.put("success", true);
            response.put("message", "Categoría guardada correctamente");
            response.put("categoria", categoriaGuardada);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al guardar la categoría: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PostMapping("/api/cambiar-estado/{id}")
    @ResponseBody
    public ResponseEntity<?> cambiarEstadoCategoria(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        boolean exito = categoriaService.cambiarEstadoCategoria(id);
        if (exito) {
            response.put("success", true);
            response.put("message", "Estado de la categoría actualizado");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Categoría no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @DeleteMapping("/api/eliminar/{id}")
    @ResponseBody
    public ResponseEntity<?> eliminarCategoria(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            categoriaService.eliminarCategoria(id);
            response.put("success", true);
            response.put("message", "Categoría eliminada correctamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar la categoría: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}