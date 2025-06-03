package com.ArqProyect.msinventory.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.ArqProyect.msinventory.model.Inventario;
import com.ArqProyect.msinventory.repository.InventarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventarioService {
    private final InventarioRepository inventarioRepository;

    public List<Inventario> listarInventarioCasa(String casaId) {
        return inventarioRepository.findByCasaId(casaId)
                .orElseThrow(() -> new NoSuchElementException("Inventario no encontrado para la casa con id: " + casaId));
    }
}
