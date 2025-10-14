package com.sistema.productos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.sistema.productos.service.ProductoService;
import com.sistema.productos.service.UsuarioService;

@Controller
public class DashboardController {
    private final UsuarioService usuarioService;

    private final ProductoService productoService;

    public DashboardController(UsuarioService usuarioService, ProductoService productoService) {
        this.usuarioService = usuarioService;
        this.productoService = productoService;
    }

    @GetMapping("/")
    public String mostrarDashboard(Model model) {
        long totalUsuarios = usuarioService.contarUsuarios();
        model.addAttribute("totalUsuarios", totalUsuarios);
        
        Long totalProductos = productoService.contarProductos();
        model.addAttribute("totalProductos", totalProductos);

        return "index";
    }
}