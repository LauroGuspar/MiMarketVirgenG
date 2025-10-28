package com.sistema.productos.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sistema.productos.model.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findAllByEstadoNot(Integer estado);
    Optional<Categoria> findByNombreIgnoreCase(String nombre);

    @Modifying
    @Query("UPDATE Categoria c SET c.estado = :nuevoEstado WHERE c.id = :id")
    void actualizarEstado(Long id, Integer nuevoEstado);
    
    boolean existsByNombre(String nombre);
}