package com.sistema.productos.service;

import com.sistema.productos.model.Cliente;
import com.sistema.productos.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {
    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public List<Cliente> listarClientes() {
        return clienteRepository.findAllByEstadoNot(2);
    }

    @Transactional
    public Cliente guardarCliente(Cliente cliente) {
        if (cliente.getCorreo() != null && cliente.getCorreo().trim().isEmpty()) {
            cliente.setCorreo(null);
        }
        if (cliente.getTelefono() != null && cliente.getTelefono().trim().isEmpty()) {
            cliente.setTelefono(null);
        }


        if (cliente.getId() == null) {
            Optional<Cliente> existentePorDoc = clienteRepository.findByTipodocumento_IdAndNdocumento(
                    cliente.getTipodocumento().getId(), cliente.getNdocumento());

            if (existentePorDoc.isPresent()) {
                Cliente existente = existentePorDoc.get();
                if (existente.getEstado() == 2) {
                    if (cliente.getCorreo() != null && !cliente.getCorreo().isBlank()) {
                        Optional<Cliente> porCorreo = clienteRepository.findByCorreoIgnoreCase(cliente.getCorreo());
                        if (porCorreo.isPresent() && !porCorreo.get().getId().equals(existente.getId()) && porCorreo.get().getEstado() != 2) {
                            throw new IllegalArgumentException("El correo electrónico ya está en uso por otra cuenta activa.");
                        }
                    }

                    existente.setEstado(1);
                    existente.setNombre(cliente.getNombre());
                    existente.setApellidoPaterno(cliente.getApellidoPaterno());
                    existente.setApellidoMaterno(cliente.getApellidoMaterno());
                    existente.setCorreo(cliente.getCorreo());
                    existente.setTelefono(cliente.getTelefono());
                    existente.setDireccion(cliente.getDireccion());
                    existente.setNombreEmpresa(cliente.getNombreEmpresa());
                    existente.setDireccionEmpresa(cliente.getDireccionEmpresa());

                    return clienteRepository.save(existente);
                } else {
                    throw new IllegalArgumentException("Ya existe un cliente activo registrado con este tipo y número de documento.");
                }
            }

            if (cliente.getCorreo() != null && !cliente.getCorreo().isBlank()) {
                Optional<Cliente> porCorreo = clienteRepository.findByCorreoIgnoreCase(cliente.getCorreo());
                 if (porCorreo.isPresent() && porCorreo.get().getEstado() != 2) {
                     throw new IllegalArgumentException("El correo electrónico ya está registrado por un cliente activo.");
                 }
            }
            cliente.setEstado(1);
            return clienteRepository.save(cliente);

        } else {
            Cliente existente = clienteRepository.findById(cliente.getId()).orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado para actualizar"));
            if (cliente.getCorreo() != null && !cliente.getCorreo().isBlank()) {
                Optional<Cliente> porCorreo = clienteRepository.findByCorreoIgnoreCase(cliente.getCorreo());
                 if (porCorreo.isPresent() && !porCorreo.get().getId().equals(existente.getId()) && porCorreo.get().getEstado() != 2) {
                     throw new IllegalArgumentException("El correo electrónico ya está en uso por otra cuenta activa.");
                 }
            }

            existente.setNombre(cliente.getNombre());
            existente.setApellidoPaterno(cliente.getApellidoPaterno());
            existente.setApellidoMaterno(cliente.getApellidoMaterno());
            existente.setCorreo(cliente.getCorreo());
            existente.setTelefono(cliente.getTelefono());
            existente.setDireccion(cliente.getDireccion());
            existente.setNombreEmpresa(cliente.getNombreEmpresa());
            existente.setDireccionEmpresa(cliente.getDireccionEmpresa());
            return clienteRepository.save(existente);
        }
    }

    @Transactional(readOnly = true)
    public long contarClientes() {
        return clienteRepository.countByEstadoNot(2);
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> obtenerClientePorId(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return clienteRepository.findById(id);
    }

    @Transactional
    public void eliminarCliente(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de usuario inválido");
        }
        Cliente cliente = obtenerClientePorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        cliente.setEstado(2);
        clienteRepository.save(cliente);
    }

    @Transactional
    public boolean cambiarEstadoCliente(Long id) {
        if (id == null || id <= 0) {
            return false;
        }

        Optional<Cliente> clienteOpt = obtenerClientePorId(id);

        if (clienteOpt.isEmpty()) {
            return false;
        }

        Cliente cliente = clienteOpt.get();
        if (cliente.getEstado() == 1) {
            clienteRepository.actualizarEstado(id, 0);
        } else if (cliente.getEstado() == 0) {
            clienteRepository.actualizarEstado(id, 1);
        }
        return true;
    }


    @Transactional(readOnly = true)
    public boolean existeCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) return false;
        return clienteRepository.existsByCorreo(correo.trim().toLowerCase());
    }
}
