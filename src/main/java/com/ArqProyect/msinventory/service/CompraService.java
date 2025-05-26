package com.ArqProyect.msinventory.service;

import com.ArqProyect.msinventory.dto.CompraCreacionDTO;
import com.ArqProyect.msinventory.dto.ItemCompraEventoDTO;
import com.ArqProyect.msinventory.model.Compra;
import com.ArqProyect.msinventory.model.ItemCompra;
import com.ArqProyect.msinventory.repository.CompraRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompraService {
    private final CompraRepository compraRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    private static final String INVENTORY_EXCHANGE = "inventory.exchange";
    private static final String COMPRA_ROUTING_KEY = "compra.registrada"; // ROUTING KEY PARA MSPAYMENTS

    public List<Compra> listarCompras() {
        return compraRepository.findAll();
    }

    public Compra obtenerCompraPorId(String id) {
        return compraRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Compra no encontrada con id: " + id));
    }

    // Método para crear una Compra a partir del DTO y enviar el evento
    public Compra crearCompraDesdeDTO(CompraCreacionDTO compraCreacionDTO) {
        validarCompraLogicaNegocio(compraCreacionDTO);
        Compra compra = convertirDTOaEntidad(compraCreacionDTO);
        Compra nuevaCompra = compraRepository.save(compra);
        enviarEventoNuevaCompra(nuevaCompra);
        return nuevaCompra;
    }

    // SE MANDA LA COMPRA AL EXCHANGE DE RABBITMQ PARA QUE MSPAYMENTS LA PROCESE
    private void enviarEventoNuevaCompra(Compra compra) {
        try {
            String compraJson = objectMapper.writeValueAsString(compra);
            rabbitTemplate.convertAndSend(INVENTORY_EXCHANGE, COMPRA_ROUTING_KEY, compraJson);
            System.out.println("Compra enviada a RabbitMQ: " + compraJson);
        } catch (Exception e) {
            System.err.println("Error al enviar la compra a RabbitMQ: " + e.getMessage());
        }
    }

    private Compra convertirDTOaEntidad(CompraCreacionDTO dto) {
        Compra compra = new Compra();
        compra.setCasaId(dto.getCasaId());
        compra.setFechaCompra(new java.sql.Date(dto.getFechaCompra().getTime()));
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
        compraRepository.delete(compra);
    }

    private void validarCompraLogicaNegocio(CompraCreacionDTO dto) {
    for (ItemCompraEventoDTO item : dto.getItems()) {
        if (!item.getEsCompartido() && item.getPropietarioId() == null) {
            throw new IllegalArgumentException("Items no compartidos deben tener propietarioId");
        }
    }
}

}