package com.ArqProyect.msinventory.config;

import com.ArqProyect.msinventory.dto.ProductoCreacionDTO;
import com.ArqProyect.msinventory.model.Producto;
import com.ArqProyect.msinventory.service.ProductoService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryConsumer {

    private final ProductoService productoService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "msinventory.queue")
    public Object handleInventoryQueue(String message) {
        try {
            JsonNode root = objectMapper.readTree(message);
            System.out.println("MS-INVENTORY: Root -> " + root.toPrettyString());

            JsonNode payload = root.get("data");
            String action = payload.get("action").asText();
            JsonNode data = payload.get("body");


            switch (action) {
                case "crearProducto":
                    return handleCrearProducto(data);

                case "obtenerProducto":
                    return handleObtenerProducto(data);

                case "saludar":
                    return handleSaludar(data);

                default:
                    System.out.println("MS-INVENTORY: Acción no reconocida: " + action);
                    return "Acción no reconocida: " + action;
            }

        } catch (Exception e) {
            System.err.println("MS-INVENTORY: Error procesando mensaje: " + e.getMessage());
            return "Error: " + e.getMessage();
        }
    }

    private Producto handleCrearProducto(JsonNode data) throws Exception {
        ProductoCreacionDTO productoDTO = objectMapper.treeToValue(data, ProductoCreacionDTO.class);
        Producto nuevoProducto = productoService.crearProducto(productoDTO);
        return nuevoProducto;
    }

    private Producto handleObtenerProducto(JsonNode data) {
        String productId = data.asText();
        Producto producto = productoService.obtenerProductoPorId(productId);
        return producto;
    }

    private String handleSaludar(JsonNode data) {
        System.out.println("MS-INVENTORY: Datos recibidos en saludar -> " + data.toString());

        try {
            // Verifica si es texto plano (como "Bruno")
            if (data.isTextual()) {
                return "Hola " + data.asText() + ", desde ms-inventory!";
            } else {
                return "Hola desconocido, el formato del nombre no es texto.";
            }
        } catch (Exception e) {
            return "Error al procesar saludo: " + e.getMessage();
        }
    }




}
