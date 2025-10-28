package com.sistema.productos.controller;

import java.util.HashMap;
import java.util.List;
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

import com.sistema.productos.model.Producto;
import com.sistema.productos.model.TipoProducto;
import com.sistema.productos.repository.CategoriaRepository;
import com.sistema.productos.repository.MarcaRepository;
import com.sistema.productos.repository.TipoProductoRepository;
import com.sistema.productos.repository.UnidadRepository;
import com.sistema.productos.service.ProductoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final CategoriaRepository categoriaRepository;
    private final MarcaRepository marcaRepository;
    private final UnidadRepository unidadRepository;
    private final TipoProductoRepository tipoProductoRepository;

    public ProductoController(ProductoService productoService, CategoriaRepository categoriaRepository, MarcaRepository marcaRepository, UnidadRepository unidadRepository,TipoProductoRepository tipoProductoRepository) {
        this.productoService = productoService;
        this.categoriaRepository = categoriaRepository;
        this.marcaRepository = marcaRepository;
        this.unidadRepository = unidadRepository;
        this.tipoProductoRepository = tipoProductoRepository;
    }

    @GetMapping("/listar")
    public String mostrarPaginaProductos() {
        return "productos";
    }

    @GetMapping("/api/listar")
    @ResponseBody
    public ResponseEntity<?> listarProductos() {
        return ResponseEntity.ok(Map.of("success", true, "data", productoService.listarTodosLosProductos()));
    }

    @GetMapping("/api/categorias")
    @ResponseBody
    public ResponseEntity<?> listarCategorias() {
        return ResponseEntity.ok(categoriaRepository.findAllByEstadoNot(2));
    }

    @GetMapping("/api/marcas")
    @ResponseBody
    public ResponseEntity<?> listarMarcas() {
        return ResponseEntity.ok(marcaRepository.findAllByEstadoNot(2));
    }

    @GetMapping("/api/unidades")
    @ResponseBody
    public ResponseEntity<?> listarUnidades() {
        return ResponseEntity.ok(unidadRepository.findByEstado(1));
    }

    @PostMapping(value = "/api/guardar", consumes = {"multipart/form-data"})
    @ResponseBody
    @SuppressWarnings("UseSpecificCatch")
    public ResponseEntity<?> guardarProducto(
            @RequestParam("producto") String productoJson,
            @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            
            Producto producto = objectMapper.readValue(productoJson, Producto.class);
            Producto productoGuardado = productoService.guardarProducto(producto, imagenFile);
            
            response.put("success", true);
            response.put("message", "Producto guardado correctamente");
            response.put("producto", productoGuardado);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al guardar el producto: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<?> obtenerProducto(@PathVariable Long id) {
        return productoService.obtenerProductoPorId(id)
                .map(producto -> ResponseEntity.ok(Map.of("success", true, "data", producto)))
                .orElse(ResponseEntity.status(404).body(Map.of("success", false, "message", "Producto no encontrado")));
    }

    @PostMapping("/api/cambiar-estado/{id}")
    @ResponseBody
    public ResponseEntity<?> cambiarEstadoProducto(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        boolean exito = productoService.cambiarEstadoProducto(id);
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
    @SuppressWarnings("UseSpecificCatch")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            productoService.eliminarProducto(id);
            response.put("success", true);
            response.put("message", "Categoría eliminada correctamente");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al eliminar la categoría: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/api/tipos-producto/por-categoria/{idCategoria}")
    @ResponseBody
    public ResponseEntity<?> listarTiposProductoPorCategoria(@PathVariable Long idCategoria) {
        List<TipoProducto> tipos = tipoProductoRepository.findByCategorias_IdAndEstadoNot(idCategoria, 2); 
        return ResponseEntity.ok(Map.of("success", true, "data", tipos));
    }
}