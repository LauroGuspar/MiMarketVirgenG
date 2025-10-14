package com.sistema.productos.service.Impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistema.productos.model.Opcion;
import com.sistema.productos.model.Rol;
import com.sistema.productos.repository.OpcionRepository;
import com.sistema.productos.repository.RolRepository;
import com.sistema.productos.service.RolService;

@Service
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;
    private final OpcionRepository opcionRepository;

    public RolServiceImpl(RolRepository rolRepository, OpcionRepository opcionRepository) {
        this.rolRepository = rolRepository;
        this.opcionRepository = opcionRepository;
    }
    @Override
    @Transactional(readOnly = true)
    public List<Rol> listarTodosLosRoles() {
        return rolRepository.findAllByEstadoNot(2);
    }

    @Override
    @Transactional
    public Rol guardarRol(Rol rol) {
if (rol.getId() == null) {
        Optional<Rol> existenteOpt = rolRepository.findByNombreIgnoreCase(rol.getNombre());
        if (existenteOpt.isPresent()) {
            Rol existente = existenteOpt.get();
            if (existente.getEstado() == 2) {
                existente.setEstado(1);
                existente.setDescripcion(rol.getDescripcion());
                return rolRepository.save(existente);
            } else {
                throw new IllegalArgumentException("Ya existe un rol con el nombre: " + rol.getNombre());
            }
        }
    }
    return rolRepository.save(rol);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Rol> obtenerRolPorId(Long id) {
        return rolRepository.findById(id);
    }

    @Override
    @Transactional
    public Optional<Rol> cambiarEstadoRol(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }

        return obtenerRolPorId(id).map(rol -> {
            if (rol.getEstado() == 1) {
                rol.setEstado(0);
            } else if (rol.getEstado() == 0) {
                rol.setEstado(1);
            }
            return rolRepository.save(rol);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Opcion> listarTodasLasOpciones() {
        return opcionRepository.findAll();
    }

    @Override
    @Transactional
    public void eliminarRol(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de rol inválido");
        }

        Rol rol = obtenerRolPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));

        rol.setEstado(2);
        rolRepository.save(rol);
    }
}