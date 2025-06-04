package com.ArqProyect.msinventory.config;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ArqProyect.msinventory.dto.CompraCreacionDTO;
import com.ArqProyect.msinventory.model.Producto; // Asumo que recibirás un Producto para crear
import com.ArqProyect.msinventory.service.CompraService;
import com.ArqProyect.msinventory.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.NoSuchElementException;

// DTOs para recibir los payloads desde la API Gateway
// Asegúrate de que estos coincidan con lo que tu API Gateway envía
@lombok.Data
class ProductoCreacionPayload {
    private String nombre;
    private String codigo;
    private String categoria;
    private String descripcion;
}

@lombok.Data
class ProductoGetPayload {
    private String id; // Si el ID viene en un objeto, si no, puedes recibir un String directamente
}


@Component
public class InventoryConsumer {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CompraService compraService; // Asegúrate de tener un servicio para manejar compras

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    // Listener para la cola de creación de productos
    @RabbitListener(queues = "producto.create.queue")
    public void handleCreateProductMessage(String message) {
        System.out.println("MS-INVENTORY: Mensaje recibido para crear producto: " + message);
        try {
            ProductoCreacionPayload payload = objectMapper.readValue(message, ProductoCreacionPayload.class);
            // Convertir payload a entidad Producto y guardarla
            Producto nuevoProducto = new Producto();
            nuevoProducto.setNombre(payload.getNombre());
            nuevoProducto.setCategoria(payload.getCategoria());
            nuevoProducto.setDescripcion(payload.getDescripcion());
            productoService.crearProducto(nuevoProducto); // Usar el servicio para guardar
            System.out.println("MS-INVENTORY: Producto creado exitosamente: " + nuevoProducto.getNombre());
        } catch (IOException e) {
            System.err.println("MS-INVENTORY: Error al deserializar mensaje de creación de producto: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("MS-INVENTORY: Error al procesar mensaje de creación de producto: " + e.getMessage());
        }
    }

    // Listener para la cola de obtención de productos
    @RabbitListener(queues = "producto.get.queue")
    public void handleGetProductMessage(String message) {
        System.out.println("MS-INVENTORY: Mensaje recibido para obtener producto: " + message);
        try {
            // Si el ID se envía directamente como String:
            String productId = message; // Asumiendo que el mensaje es solo el ID del producto
            Producto producto = productoService.obtenerProductoPorId(productId);
            if (producto != null) {
                System.out.println("MS-INVENTORY: Producto encontrado: " + producto.getNombre());
                // En un escenario real, aquí enviarías una respuesta de vuelta al gateway
                // Esto requeriría un patrón de Request-Reply, que es más complejo.
                // Por ahora, solo lo logueamos.
            } else {
                System.out.println("MS-INVENTORY: Producto no encontrado con ID: " + productId);
            }
        } catch (NoSuchElementException e) {
            System.err.println("MS-INVENTORY: Producto no encontrado: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("MS-INVENTORY: Error al procesar mensaje de obtención de producto: " + e.getMessage());
        }
    }

    // Listener para la cola de compra
    @RabbitListener(queues = "compra.create.queue")
    public void handleCompraMessage(String message) {
        try {
            CompraCreacionDTO compraDto = objectMapper.readValue(message, CompraCreacionDTO.class);
            compraService.crearCompraDesdeDTO(compraDto);
            rabbitTemplate.convertAndSend(
                "apigateway.exchange",
                "payments.compra.playload",
                objectMapper.writeValueAsString(compraDto)
            );
            System.out.println("MS-INVENTORY: Compra procesada y enviada a payments: " + compraDto);
        }catch (Exception e) {
            System.err.println("MS-INVENTORY: Error al procesar mensaje de compra: " + e.getMessage());
        }
    }


}