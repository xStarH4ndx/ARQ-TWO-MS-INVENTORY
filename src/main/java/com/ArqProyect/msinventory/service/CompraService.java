package com.ArqProyect.msinventory.service;

import com.ArqProyect.msinventory.dto.CompraCreacionDTO;
import com.ArqProyect.msinventory.dto.ItemCompraEventoDTO;
import com.ArqProyect.msinventory.model.Compra;
import com.ArqProyect.msinventory.model.ItemCompra;
import com.ArqProyect.msinventory.model.Inventario;
import com.ArqProyect.msinventory.repository.CompraRepository;
import com.ArqProyect.msinventory.repository.InventarioRepository; // Importa el repositorio de Inventario
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importa la anotación Transactional

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompraService {
    private final CompraRepository compraRepository;
    private final InventarioRepository inventarioRepository; // Inyecta el InventarioRepository
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
    @Transactional // Asegura que la creación de la compra y la actualización del inventario sean atómicas
    public Compra crearCompraDesdeDTO(CompraCreacionDTO compraCreacionDTO) {
        validarCompraLogicaNegocio(compraCreacionDTO);
        Compra compra = convertirDTOaEntidad(compraCreacionDTO);
        Compra nuevaCompra = compraRepository.save(compra);
        actualizarInventario(nuevaCompra.getItemsCompra(), compraCreacionDTO.getCasaId()); // Actualizar inventario
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

    private void actualizarInventario(List<ItemCompra> items, String casaId) {
        for (ItemCompra item : items) {
            // Buscar si ya existe un registro de inventario para este producto y casa
            List<Inventario> inventarios = inventarioRepository.findByCasaId(casaId).orElse(null);
            if (inventarios != null) {
                Inventario inventarioExistente = inventarios.stream()
                        .filter(i -> i.getProductoId().equals(item.getProductoId()))
                        .findFirst()
                        .orElse(null);

                if (inventarioExistente != null) {
                    // Si existe, actualizamos el stock
                    inventarioExistente.setCantidadStock(inventarioExistente.getCantidadStock() + item.getCantidad());
                    inventarioRepository.save(inventarioExistente);
                } else {
                    // Si no existe, creamos un nuevo registro de inventario
                    Inventario nuevoInventario = new Inventario();
                    nuevoInventario.setCasaId(casaId);
                    nuevoInventario.setProductoId(item.getProductoId());
                    nuevoInventario.setNombreProducto(item.getNombreProducto());
                    nuevoInventario.setCantidadStock(item.getCantidad());
                    inventarioRepository.save(nuevoInventario);
                }
            } else {
                // Si no existe, creamos un nuevo registro de inventario
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