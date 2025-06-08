package com.ArqProyect.msinventory.config;

import com.ArqProyect.msinventory.dto.ProductoCreacionDTO;
import com.ArqProyect.msinventory.model.Producto;
import com.ArqProyect.msinventory.service.ProductoService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InventoryConsumer {

    private final ProductoService productoService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "msinventory.queue")
    public Object handleInventoryQueue(MessageDTO messageDTO) {
        try {
            System.out.println("MS-INVENTORY: Mensaje recibido deserializado: " + messageDTO);

            PayloadDTO payload = messageDTO.getData();
            if (payload == null) {
                return "Error: 'data' no encontrado en mensaje";
            }

            String action = payload.getAction();
            JsonNode data = payload.getBody();

            System.out.println("MS-INVENTORY: Acción: " + action);
            System.out.println("MS-INVENTORY: Data -> " + (data != null ? data.toPrettyString() : "null"));

            switch (action) {
                case "crearProducto":
                    return handleCrearProducto(data);

                case "obtenerProducto":
                    return handleObtenerProducto(data);

                case "eliminarProducto":
                    return handleEliminarProducto(data);

                case "listarProductos":
                    return handleListarProductos();

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

