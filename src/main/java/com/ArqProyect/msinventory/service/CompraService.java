package com.ArqProyect.msinventory.service;

import com.ArqProyect.msinventory.dto.CompraCreacionDTO;
import com.ArqProyect.msinventory.dto.ItemCompraEventoDTO;
import com.ArqProyect.msinventory.model.Compra;
import com.ArqProyect.msinventory.model.ItemCompra;
import com.ArqProyect.msinventory.model.Inventario;
import com.ArqProyect.msinventory.repository.CompraRepository;
import com.ArqProyect.msinventory.repository.InventarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompraService {

    private final CompraRepository compraRepository;
    private final InventarioRepository inventarioRepository;

    public List<Compra> listarCompras(String casaId) {
        return compraRepository.findByCasaId(casaId);
    }

    public Compra obtenerCompraPorId(String id) {
        return compraRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Compra no encontrada con id: " + id));
    }

    @Transactional
    public Compra crearCompraDesdeDTO(CompraCreacionDTO compraCreacionDTO) {
        validarCompraLogicaNegocio(compraCreacionDTO);
        Compra compra = convertirDTOaEntidad(compraCreacionDTO);
        Compra nuevaCompra = compraRepository.save(compra);
        actualizarInventario(nuevaCompra.getItemsCompra(), compraCreacionDTO.getCasaId());
        return nuevaCompra;
    }

    private Compra convertirDTOaEntidad(CompraCreacionDTO dto) {
        Compra compra = new Compra();
        compra.setCasaId(dto.getCasaId());
        compra.setFechaCompra(Instant.now());
        compra.setItemsCompra(convertirItemDTOsAEntidades(dto.getItems()));
        return compra;
    }

    private List<ItemCompra> convertirItemDTOsAEntidades(List<ItemCompraEventoDTO> itemDTOs) {
        return itemDTOs.stream()
                .map(this::convertirItemDTOaEntidad)
                .collect(Collectors.toList());
    }

    private ItemCompra convertirItemDTOaEntidad(ItemCompraEventoDTO dto) {
        return new ItemCompra(
                dto.getProductoId(),
                dto.getNombreProducto(),
                dto.getCantidad(),
                dto.getPrecioUnitario(),
                dto.getEsCompartido(),
                dto.getPropietarioId()
        );
    }

    public void eliminarCompra(String id) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Compra no encontrada con id: " + id));
        revertirInventario(compra.getItemsCompra(), compra.getCasaId());
        compraRepository.delete(compra);
    }

    private void revertirInventario(List<ItemCompra> items, String casaId) {
        for (ItemCompra item : items) {
            List<Inventario> inventarios = inventarioRepository.findByCasaId(casaId).orElse(null);
            if (inventarios != null) {
                Inventario inventarioExistente = inventarios.stream()
                        .filter(i -> i.getProductoId().equals(item.getProductoId()))
                        .findFirst()
                        .orElse(null);

                if (inventarioExistente != null) {
                    int nuevaCantidad = inventarioExistente.getCantidadStock() - item.getCantidad();
                    inventarioExistente.setCantidadStock(Math.max(nuevaCantidad, 0)); // evitar valores negativos
                    inventarioRepository.save(inventarioExistente);
                }
            }
        }
    }


    private void validarCompraLogicaNegocio(CompraCreacionDTO dto) {
        for (ItemCompraEventoDTO item : dto.getItems()) {
            if (!item.getEsCompartido() && item.getPropietarioId() == null) {
                throw new IllegalArgumentException("Items no compartidos deben tener propietarioId");
            }
        }
    }

    private void actualizarInventario(List<ItemCompra> items, String casaId) {
        for (ItemCompra item : items) {
            List<Inventario> inventarios = inventarioRepository.findByCasaId(casaId).orElse(null);
            if (inventarios != null) {
                Inventario inventarioExistente = inventarios.stream()
                        .filter(i -> i.getProductoId().equals(item.getProductoId()))
                        .findFirst()
                        .orElse(null);

                if (inventarioExistente != null) {
                    inventarioExistente.setCantidadStock(inventarioExistente.getCantidadStock() + item.getCantidad());
                    inventarioRepository.save(inventarioExistente);
                } else {
                    Inventario nuevoInventario = new Inventario();
                    nuevoInventario.setCasaId(casaId);
                    nuevoInventario.setProductoId(item.getProductoId());
                    nuevoInventario.setNombreProducto(item.getNombreProducto());
                    nuevoInventario.setCantidadStock(item.getCantidad());
                    inventarioRepository.save(nuevoInventario);
                }
            } else {
                Inventario nuevoInventario = new Inventario();
                nuevoInventario.setCasaId(casaId);
                nuevoInventario.setProductoId(item.getProductoId());
                nuevoInventario.setNombreProducto(item.getNombreProducto());
                nuevoInventario.setCantidadStock(item.getCantidad());
                inventarioRepository.save(nuevoInventario);
            }
        }
    }
}
