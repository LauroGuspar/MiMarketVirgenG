package com.sistema.productos.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;

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
            List<Opcion> opcionesMenu = usuarioEncontrado.getRol().getOpciones().stream().sorted(Comparator.comparing(Opcion::getId))
                    .collect(Collectors.toList());
            Map<String, List<Opcion>> menuAgrupado = new LinkedHashMap<>();
            List<Opcion> opcionesIndependientes = new ArrayList<>();
            for (Opcion opcion : opcionesMenu) {
                String[] partesRuta = opcion.getRuta().split("/");
                if (partesRuta.length > 2) {
                    String grupo = partesRuta[1];
                    String nombreGrupo = grupo.substring(0, 1).toUpperCase() + grupo.substring(1);
                    menuAgrupado.computeIfAbsent(nombreGrupo, k -> new ArrayList<>()).add(opcion);
                } else {
                    opcionesIndependientes.add(opcion);
                }
            }
            
            session.setAttribute("menuAgrupado", menuAgrupado);
            session.setAttribute("opcionesIndependientes", opcionesIndependientes);
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

    @GetMapping("/recuperar-clave")
    public String mostrarFormularioRecuperacion() {
        return "recuperar-clave";
    }

    @PostMapping("/recuperar-clave/solicitar")
    @ResponseBody
    public ResponseEntity<?> solicitarRecuperacionClave(@RequestBody Map<String, String> payload) {
        Map<String, Object> response = new HashMap<>();
        String ndocumento = payload.get("ndocumento");
        String correo = payload.get("correo");

        try {
            usuarioService.iniciarRecuperacionClave(ndocumento, correo);
            response.put("success", true);
            response.put("message", "Se han enviado las instrucciones para restablecer tu contraseña a tu correo electrónico.");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/restablecer-clave")
    public String mostrarFormularioRestablecer(@RequestParam(name = "token", required = false) String token, Model model, RedirectAttributes redirectAttributes) {
        if (token == null || token.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Token de restablecimiento inválido o faltante.");
            return "redirect:/login";
        }

        model.addAttribute("token", token);
        return "restablecer-clave";
    }

    @PostMapping("/restablecer-clave")
    public String procesarRestablecerClave(@RequestParam String token,  @RequestParam String nuevaClave, @RequestParam String confirmarClave, RedirectAttributes redirectAttributes) {

        if (token == null || token.isBlank()) {
             redirectAttributes.addFlashAttribute("error", "Token inválido.");
             return "redirect:/login";
        }
        if (nuevaClave == null || nuevaClave.length() < 6) {
             redirectAttributes.addFlashAttribute("error", "La contraseña debe tener al menos 6 caracteres.");
             redirectAttributes.addFlashAttribute("token", token);
             return "redirect:/restablecer-clave?token=" + token;
        }
        if (!nuevaClave.equals(confirmarClave)) {
            redirectAttributes.addFlashAttribute("error", "Las contraseñas no coinciden.");
            redirectAttributes.addFlashAttribute("token", token);
            return "redirect:/restablecer-clave?token=" + token;
        }

        try {
            usuarioService.restablecerClave(token, nuevaClave);
            redirectAttributes.addFlashAttribute("success", "Contraseña restablecida exitosamente. Ya puedes iniciar sesión.");
            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            if (e.getMessage().toLowerCase().contains("token")) {
                 return "redirect:/login";
            } else {
                 redirectAttributes.addFlashAttribute("token", token);
                 return "redirect:/restablecer-clave?token=" + token;
            }
        } catch (Exception e) {
             System.err.printf("Error inesperado en procesarRestablecerClave: " + e.getMessage(), e);
             redirectAttributes.addFlashAttribute("error", "Ocurrió un error inesperado al restablecer la contraseña.");
             return "redirect:/login";
        }
    }
}