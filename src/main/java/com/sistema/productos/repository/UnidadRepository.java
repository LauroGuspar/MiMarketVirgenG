package com.sistema.productos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sistema.productos.model.Unidad;

@Repository
public interface UnidadRepository extends JpaRepository<Unidad, Long> {
    List<Unidad> findByEstado(int estado);
    Optional<Unidad> findByNombreIgnoreCase(String nombre);
    List<Unidad> findAllByEstadoNot(Integer estado);

    @Modifying
    @Query("UPDATE Unidad u SET u.estado = :nuevoEstado WHERE u.id = :id")
    void actualizarEstado(Long id, Integer nuevoEstado);
}