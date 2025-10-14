package com.sistema.productos.service;

import java.util.List;
import java.util.Optional;

import com.sistema.productos.model.Opcion;
import com.sistema.productos.model.Rol;

public interface RolService {
    List<Rol> listarTodosLosRoles();

    Rol guardarRol(Rol rol);

    Optional<Rol> obtenerRolPorId(Long id);

    Optional<Rol> cambiarEstadoRol(Long id);

    List<Opcion> listarTodasLasOpciones();

    void eliminarRol(Long id);
}