package com.ArqProyect.msinventory.service;

import com.ArqProyect.msinventory.dto.CompraCreacionDTO;
import com.ArqProyect.msinventory.dto.ItemCompraEventoDTO;
import com.ArqProyect.msinventory.model.Compra;
import com.ArqProyect.msinventory.model.ItemCompra;
import com.ArqProyect.msinventory.repository.CompraRepository;

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
    private final InventarioService inventarioService;

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

        // Actualizar el inventario con cada item usando el servicio (idempotente)
        for (ItemCompra item : nuevaCompra.getItemsCompra()) {
            inventarioService.aumentarStock(
                    compraCreacionDTO.getCasaId(),
                    item.getProductoId(),
                    item.getNombreProducto(),
                    (int) item.getCantidad()
            );
        }

        return nuevaCompra;
    }

    @Transactional
    public void eliminarCompra(String id) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Compra no encontrada con id: " + id));

        // Revertir stock del inventario por cada item
        for (ItemCompra item : compra.getItemsCompra()) {
            inventarioService.disminuirStock(
                    compra.getCasaId(),
                    item.getProductoId(),
                    (int) item.getCantidad()
            );
        }

        compraRepository.delete(compra);
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

    private void validarCompraLogicaNegocio(CompraCreacionDTO dto) {
        for (ItemCompraEventoDTO item : dto.getItems()) {
            if (!item.getEsCompartido() && item.getPropietarioId() == null) {
                throw new IllegalArgumentException("Items no compartidos deben tener propietarioId");
            }
        }
    }
}
