package com.sistema.productos.service.Impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sistema.productos.model.Marca;
import com.sistema.productos.repository.MarcaRepository;
import com.sistema.productos.service.MarcaService;

@Service
public class MarcaServiceImpl implements MarcaService {

    private final MarcaRepository marcaRepository;

    public MarcaServiceImpl(MarcaRepository marcaRepository) {
        this.marcaRepository = marcaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Marca> listarTodasLasMarcas() {
        return marcaRepository.findAllByEstadoNot(2);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Marca> obtenerMarcaPorId(Long id) {
        return marcaRepository.findById(id);
    }

    @Override
    @Transactional
    public Marca guardarMarca(Marca marca) {
    if (marca.getId() == null) {
        Optional<Marca> existenteOpt = marcaRepository.findByNombreIgnoreCase(marca.getNombre());
        if (existenteOpt.isPresent()) {
            Marca existente = existenteOpt.get();
            if (existente.getEstado() == 2) {
                existente.setEstado(1);
                existente.setFecha(LocalDate.now());
                return marcaRepository.save(existente);
            } else {
                throw new IllegalArgumentException("Ya existe una marca con el nombre: " + marca.getNombre());
            }
        }
    }
    if (marca.getId() == null && marca.getFecha() == null) {
        marca.setFecha(LocalDate.now());
    }
    return marcaRepository.save(marca);
    }

    @Override
    @Transactional
    public void eliminarMarca(Long id) {
        Marca marca = marcaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Marca no encontrada con ID: " + id));
        marca.setEstado(2);
        marcaRepository.save(marca);
    }

    @Override
    @Transactional
    public boolean cambiarEstadoMarca(Long id) {
        Optional<Marca> marcaOpt = marcaRepository.findById(id);
        if (marcaOpt.isEmpty()) {
            return false;
        }
        Marca marca = marcaOpt.get();
        if (marca.getEstado() == 1) {
            marcaRepository.actualizarEstado(id, 0);
        } else if (marca.getEstado() == 0) {
            marcaRepository.actualizarEstado(id, 1);
        }
        return true;
    }
}