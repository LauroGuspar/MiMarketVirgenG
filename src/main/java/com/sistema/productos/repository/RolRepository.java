package com.sistema.productos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sistema.productos.model.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    List<Rol> findAllByEstadoNot(Integer estado);
    Optional<Rol> findByNombreIgnoreCase(String nombre);
}