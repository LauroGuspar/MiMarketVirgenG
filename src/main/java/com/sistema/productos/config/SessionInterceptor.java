package com.sistema.productos.config;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
/*
import com.sistema.productos.model.Usuario;
import com.sistema.productos.model.Opcion;
import com.sistema.productos.model.Rol;
*/
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/*
import java.util.Set;
import java.util.stream.Collectors;
*/
@Component
public class SessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,@NonNull Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("/login");
            return false;
        }
        return true;
    }

    /*
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = requestURI.substring(contextPath.length());
        String finalPath = path.endsWith("/") && path.length() > 1 ? path.substring(0, path.length() - 1) : path;
        System.out.println("Interceptando: " + path + " (Normalizado: " + finalPath + ")");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioLogueado") == null) {
            if (!path.equals("/login") && !path.equals("/logueo") && !isPublicResource(path)) {
                System.out.println("Interceptor: Sin sesión para: " + path + ". Redirigiendo a /login.");
                response.sendRedirect(contextPath + "/login");
                return false;
            }
            System.out.println("Interceptor: Permitido (Público/Login): " + path);
            return true;
        }
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        Rol rolUsuario = usuarioLogueado.getRol();
        if (finalPath.equals("")) {
             System.out.println("Interceptor: Permitido (Ruta Raíz '/'): " + finalPath);
            return true;
        }

        if (finalPath.startsWith("/reniec/api/")) {
            System.out.println("Interceptor: Permitido (Llamada API Externa Reniec): " + finalPath);
            return true;
        }

        if (rolUsuario == null || rolUsuario.getOpciones() == null) {
            System.err.println("Interceptor Error: Rol u Opciones no cargadas para el usuario: " + usuarioLogueado.getUsuario() + ". Path: " + path);
            response.sendRedirect(contextPath + "/");
            return false;
        }

        Set<String> rutasPermitidas = rolUsuario.getOpciones().stream().map(Opcion::getRuta).collect(Collectors.toSet());
        rutasPermitidas.add("/");
        boolean isApiCall = finalPath.contains("/api/");
        String pathToVerify = finalPath;

        if (isApiCall && !finalPath.startsWith("/reniec/api/")) {
            if (finalPath.startsWith("/clientes/api/")) {
                pathToVerify = "/clientes/listar";
            } else if (finalPath.startsWith("/empleados/roles/api/")) {
                pathToVerify = "/empleados/roles/listar";
            } else if (finalPath.startsWith("/empleados/api/")) {
                 pathToVerify = "/empleados/listar";
            } else if (finalPath.startsWith("/productos/categorias/api/")) {
                 pathToVerify = "/productos/categorias/listar";
            } else if (finalPath.startsWith("/productos/marcas/api/")) {
                 pathToVerify = "/productos/marcas/listar";
            } else if (finalPath.startsWith("/productos/unidad/api/")) {
                 pathToVerify = "/productos/unidad/listar";
            } else if (finalPath.startsWith("/productos/tipos-producto/api/")) {
                 pathToVerify = "/productos/tipos-producto/listar";
            } else if (finalPath.startsWith("/productos/api/")) {
                 pathToVerify = "/productos/listar";
            }
             else if (finalPath.startsWith("/ventas/api/")) {
                 pathToVerify = "/ventas/listar";
             }
             else {
                 pathToVerify = finalPath;
                 System.out.println("Interceptor Advertencia: No se pudo mapear ruta API interna '" + finalPath + "'. Verificando permiso para ruta original.");
            }
            System.out.println("Interceptor: Llamada API interna detectada (" + finalPath + "). Verificando permiso para página: " + pathToVerify);
        }

        boolean tienePermiso = rutasPermitidas.contains(pathToVerify);

        System.out.println("Interceptor Check: User=" + usuarioLogueado.getUsuario() + ", Role=" + rolUsuario.getNombre() + ", PathToCheck=" + pathToVerify + ", Permitted=" + tienePermiso);

        if (!tienePermiso) {
            System.out.println("Interceptor: DENEGANDO acceso para: " + finalPath + " (Verificado contra: " + pathToVerify + ")");
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                System.out.println("-> Petición AJAX denegada. Enviando 403 Forbidden.");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"success\": false, \"message\": \"Acceso API denegado\"}");
                return false;
            } else {
                System.out.println("-> Petición normal denegada. Redirigiendo a /");
                response.sendRedirect(contextPath + "/");
                return false;
            }
        }

        System.out.println("Interceptor: Permitido: " + finalPath);
        return true;
    }

    private boolean isPublicResource(String path) {
        return path.startsWith("/css/") ||
               path.startsWith("/js/") ||
               path.startsWith("/images/") ||
               path.startsWith("/webjars/") ||
               path.equals("/favicon.ico") ||
               path.startsWith("/error");
    }
    */
}