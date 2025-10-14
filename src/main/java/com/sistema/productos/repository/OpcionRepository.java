package com.sistema.productos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sistema.productos.model.Opcion;

@Repository
public interface OpcionRepository extends JpaRepository<Opcion, Long> {
}