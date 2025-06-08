package com.ArqProyect.msinventory.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ArqProyect.msinventory.model.Inventario;
import com.ArqProyect.msinventory.repository.InventarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventarioService {

    private final InventarioRepository inventarioRepository;

    public Optional<List<Inventario>> listarInventarioCasa(String casaId) {
        return inventarioRepository.findByCasaId(casaId);
    }

    public Inventario aumentarStock(String casaId, String productoId, String nombreProducto, int cantidad) {
        Optional<Inventario> existente = inventarioRepository.findByCasaIdAndProductoId(casaId, productoId);

        int cantidadTotal = cantidad;

        if (existente.isPresent()) {
            Inventario inventarioExistente = existente.get();
            cantidadTotal += inventarioExistente.getCantidadStock();

            // Elimina el documento anterior
            inventarioRepository.delete(inventarioExistente);
        }

        // Crea nuevo inventario actualizado
        Inventario nuevoInventario = new Inventario();
        nuevoInventario.setCasaId(casaId);
        nuevoInventario.setProductoId(productoId);
        nuevoInventario.setNombreProducto(nombreProducto);
        nuevoInventario.setCantidadStock(cantidadTotal);

        return inventarioRepository.save(nuevoInventario);
    }


    public Inventario disminuirStock(String casaId, String productoId, int cantidad) {
        Inventario inventario = inventarioRepository.findByCasaIdAndProductoId(casaId, productoId)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado para productoId: " + productoId));

        int nuevaCantidad = inventario.getCantidadStock() - cantidad;
        inventario.setCantidadStock(Math.max(nuevaCantidad, 0));
        return inventarioRepository.save(inventario);
    }

    public Inventario actualizarInventario(String id, int nuevaCantidadStock) {
        Inventario inventario = inventarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventario no encontrado con id: " + id));

        inventario.setCantidadStock(nuevaCantidadStock);
        return inventarioRepository.save(inventario);
    }

}
