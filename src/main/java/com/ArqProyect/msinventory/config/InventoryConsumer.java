package com.ArqProyect.msinventory.config;

import com.ArqProyect.msinventory.dto.CompraCreacionDTO;
import com.ArqProyect.msinventory.dto.ProductoCreacionDTO;
import com.ArqProyect.msinventory.model.Compra;
import com.ArqProyect.msinventory.model.Inventario;
import com.ArqProyect.msinventory.model.Producto;
import com.ArqProyect.msinventory.service.CompraService;
import com.ArqProyect.msinventory.service.InventarioService;
import com.ArqProyect.msinventory.service.ProductoService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InventoryConsumer {

    private final ProductoService productoService;
    private final CompraService compraService;
    private final InventarioService inventarioService;
    private final ObjectMapper objectMapper;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "msinventory.queue")
    public Object handleInventoryQueue(MessageDTO messageDTO) {
        try {
            PayloadDTO payload = messageDTO.getData();
            if (payload == null) {
                return "Error: 'data' no encontrado en mensaje";
            }

            String action = payload.getAction();
            JsonNode data = payload.getBody();

            switch (action) {
                // ACCIONES PRODUCTOS
                case "crearProducto":
                    return handleCrearProducto(data);

                case "obtenerProducto":
                    return handleObtenerProducto(data);

                case "eliminarProducto":
                    return handleEliminarProducto(data);

                case "listarProductos":
                    return handleListarProductos();
                
                // ACCIONES COMPRAS
                case "crearCompra":
                    return handleCrearCompra(data);

                case "listarCompras":
                    return handleListarCompras(data);

                case "obtenerCompra":
                    return handleObtenerCompra(data);

                case "eliminarCompra":
                    return handleEliminarCompra(data);
                
                // ACCIONES INVENTARIO
                case "listarInventario":
                    return handleListarInventario(data);

                case "actualizarInventario":
                    return handleActualizarInventario(data);

                default:
                    System.out.println("MS-INVENTORY: Acción no reconocida: " + action);
                    return "Acción no reconocida: " + action;
            }

        } catch (Exception e) {
            System.err.println("MS-INVENTORY: Error procesando mensaje: " + e.getMessage());
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
    // ACCIONES INVENTARIO
    private Inventario handleActualizarInventario(JsonNode data) {
        if (data == null || !data.has("id") || !data.has("nuevaCantidadStock")) {
            throw new IllegalArgumentException("El cuerpo del inventario debe contener 'id' y 'nuevaCantidadStock'");
        }
        String id = data.get("id").asText();
        int nuevaCantidadStock = data.get("nuevaCantidadStock").asInt();
        return inventarioService.actualizarInventario(id, nuevaCantidadStock);
    }

    private List<Inventario> handleListarInventario(JsonNode data) {
        if (data == null || !data.isTextual()) {
            throw new IllegalArgumentException("El campo 'casaId' es requerido para listarInventario");
        }
        String casaId = data.asText();
        return inventarioService.listarInventarioCasa(casaId).orElse(null);
    }

    // ACCIONES COMPRAS
    private String handleCrearCompra(JsonNode data) throws Exception {
        if (data == null) {
            throw new IllegalArgumentException("El cuerpo de la compra (body) es null");
        }

        CompraCreacionDTO compraDTO = objectMapper.treeToValue(data, CompraCreacionDTO.class);
        Compra nuevaCompra = compraService.crearCompraDesdeDTO(compraDTO);

        // 1. Construir el cuerpo del mensaje (body)
        ObjectNode gastoBody = objectMapper.createObjectNode();
        gastoBody.put("compraId", nuevaCompra.getId());
        gastoBody.put("casaId", nuevaCompra.getCasaId());
        gastoBody.put("fechaCompra", nuevaCompra.getFechaCompra().toString());
        JsonNode itemsCompraNode = objectMapper.valueToTree(nuevaCompra.getItemsCompra());
        gastoBody.set("itemsCompra", itemsCompraNode);

        // 2. Construir el PayloadDTO
        PayloadDTO payloadDTO = new PayloadDTO();
        payloadDTO.setAction("crearGastoCompra");
        payloadDTO.setBody(gastoBody);

        // 3. Construir el MessageDTO
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setData(payloadDTO);

        // 4. Enviar el mensaje a la cola gastoCompra.queue
        Object response = rabbitTemplate.convertSendAndReceive("gastoCompra.queue", messageDTO);
        String msg = "MS-INVENTORY: Compra creada con ID: " + nuevaCompra.getId() + ". Respuesta de ms-payments: " + response;
        System.out.println(msg);
        return msg;
    }


    private List<Compra> handleListarCompras(JsonNode data) {
        if (data == null || !data.isTextual()) {
            throw new IllegalArgumentException("El campo 'casaId' es requerido para listarCompras");
        }
        String casaId = data.asText();
        return compraService.listarCompras(casaId);
    }

    private Compra handleObtenerCompra(JsonNode data) {
        if (data == null || !data.isTextual()) {
            throw new IllegalArgumentException("El campo 'id' es requerido para obtenerCompra");
        }
        String compraId = data.asText();
        return compraService.obtenerCompraPorId(compraId);
    }

    private String handleEliminarCompra(JsonNode data) throws Exception {
        if (data == null || !data.isTextual()) {
            return "Error: el campo 'id' es requerido para eliminarCompra";
        }
        String compraId = data.asText();
        compraService.eliminarCompra(compraId);

        ObjectNode gastoBody = objectMapper.createObjectNode();
        gastoBody.put("compraId", compraId);
        PayloadDTO payloadDTO = new PayloadDTO();
        payloadDTO.setAction("eliminarGastoCompra");
        payloadDTO.setBody(gastoBody);
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setData(payloadDTO);
        Object response = rabbitTemplate.convertSendAndReceive("gastoCompra.queue", messageDTO);
        String msg = "MS-INVENTORY: Compra eliminada con ID: " + compraId + ". Respuesta de ms-payments: " + response;
        System.out.println(msg);
        return msg;
    }


    // ACCIONES PRODUCTOS
    private List<Producto> handleListarProductos() {
        return productoService.listarProductos();
    }

    private String handleCrearProducto(JsonNode data) throws Exception {
        if (data == null) {
            throw new IllegalArgumentException("El cuerpo del producto (body) es null");
        }
        ProductoCreacionDTO productoDTO = objectMapper.treeToValue(data, ProductoCreacionDTO.class);
        Producto nuevoProducto = productoService.crearProducto(productoDTO);
        String msg = "MS-INVENTORY: Producto creado con ID: " + nuevoProducto.getId();
        return msg;
    }

    private Producto handleObtenerProducto(JsonNode data) {
        if (data == null || !data.isTextual()) {
            throw new IllegalArgumentException("El campo 'id' es requerido para obtenerProducto");
        }
        String productId = data.asText();
        Producto producto = productoService.obtenerProductoPorId(productId);
        System.out.println("MS-INVENTORY: Producto obtenido: " + producto);
        return producto;
    }

    private String handleEliminarProducto(JsonNode data) {
        // Espera un JSON como {"id": "valorId"}
        if (data == null || !data.isTextual()) {
            return "Error: el campo 'id' es requerido para eliminarProducto";
        }
        String productId = data.asText();
        productoService.deleteProducto(productId);
        String msg = "MS-INVENTORY: Producto eliminado con ID: " + productId;
        System.out.println(msg);
        return msg;
    }

}

