package com.sistema.productos.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sistema.productos.model.Opcion;
import com.sistema.productos.model.Usuario;
import com.sistema.productos.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
    
    private final UsuarioService usuarioService;

    public LoginController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String mostrarFormularioLogin(HttpSession session) {
        if (session.getAttribute("usuarioLogueado") != null) {
            return "redirect:/";
        }
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String usuario, @RequestParam String clave, HttpSession session,
                                RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOpt = usuarioService.findByUsuario(usuario);

        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/login";
        }

        Usuario usuarioEncontrado = usuarioOpt.get();

        if (usuarioEncontrado.getEstado() != 1) {
            redirectAttributes.addFlashAttribute("error", "Este usuario se encuentra inactivo.");
            return "redirect:/login";
        }

        if (usuarioService.verificarContrasena(clave, usuarioEncontrado.getClave())) {
            session.setAttribute("usuarioLogueado", usuarioEncontrado);

            // --- LÓGICA MODIFICADA PARA AGRUPAR EL MENÚ ---

            // 1. Obtenemos y ordenamos las opciones como antes
            List<Opcion> opcionesMenu = usuarioEncontrado.getRol().getOpciones().stream().sorted(Comparator.comparing(Opcion::getId))
                    .collect(Collectors.toList());
            Map<String, List<Opcion>> menuAgrupado = new LinkedHashMap<>();
            
            // 3. Creamos una lista para las opciones que no se agrupan (ej. Dashboard)
            List<Opcion> opcionesIndependientes = new ArrayList<>();

            // 4. Iteramos y clasificamos cada opción
            for (Opcion opcion : opcionesMenu) {
                String[] partesRuta = opcion.getRuta().split("/");
                // Si la ruta tiene un formato como /grupo/accion (ej. /productos/listar)
                if (partesRuta.length > 2) {
                    String grupo = partesRuta[1];
                    // Capitalizamos el nombre del grupo para mostrarlo (ej. "productos" -> "Productos")
                    String nombreGrupo = grupo.substring(0, 1).toUpperCase() + grupo.substring(1);
                    
                    // Agregamos la opción al mapa. Si el grupo no existe, se crea una nueva lista.
                    menuAgrupado.computeIfAbsent(nombreGrupo, k -> new ArrayList<>()).add(opcion);
                } else {
                    // Si no, es una opción independiente (ej. / o /dashboard)
                    opcionesIndependientes.add(opcion);
                }
            }
            
            // 5. Guardamos las estructuras en la sesión para que Thymeleaf las use
            session.setAttribute("menuAgrupado", menuAgrupado);
            session.setAttribute("opcionesIndependientes", opcionesIndependientes);
            // Guardamos también la lista plana por si se necesita para otras validaciones
            session.setAttribute("menuOpciones", opcionesMenu);

            return "redirect:/";
        } else {
            redirectAttributes.addFlashAttribute("error", "Contraseña incorrecta.");
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("logout", "Has cerrado sesión exitosamente.");
        return "redirect:/login";
    }
}