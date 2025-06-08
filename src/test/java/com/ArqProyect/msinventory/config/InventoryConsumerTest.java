package com.ArqProyect.msinventory.config;

import com.ArqProyect.msinventory.dto.ProductoCreacionDTO;
import com.ArqProyect.msinventory.model.Inventario;
import com.ArqProyect.msinventory.model.Producto;
import com.ArqProyect.msinventory.service.CompraService;
import com.ArqProyect.msinventory.service.InventarioService;
import com.ArqProyect.msinventory.service.ProductoService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class InventoryConsumerTest {

    @Mock
    private ProductoService productoService;
    @Mock
    private CompraService compraService;
    @Mock
    private InventarioService inventarioService;
    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private InventoryConsumer inventoryConsumer;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testHandleCrearProducto() throws Exception {
        ProductoCreacionDTO dto = new ProductoCreacionDTO();
        Producto producto = new Producto();
        producto.setId("123");

        ObjectNode json = JsonNodeFactory.instance.objectNode();
        when(objectMapper.treeToValue(any(JsonNode.class), eq(ProductoCreacionDTO.class))).thenReturn(dto);
        when(productoService.crearProducto(dto)).thenReturn(producto);

        PayloadDTO payload = new PayloadDTO();
        payload.setAction("crearProducto");
        payload.setBody(json);

        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setData(payload);

        String result = (String) inventoryConsumer.handleInventoryQueue(messageDTO);
        assertTrue(result.contains("Producto creado"));
    }

    @Test
    public void testHandleListarProductos() {
        when(productoService.listarProductos()).thenReturn(Collections.emptyList());

        PayloadDTO payload = new PayloadDTO();
        payload.setAction("listarProductos");
        payload.setBody(null);

        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setData(payload);

        Object result = inventoryConsumer.handleInventoryQueue(messageDTO);
        assertNotNull(result);
        assertTrue(result instanceof java.util.List);
    }

    @Test
    public void testHandleActualizarInventario() {
        ObjectNode data = JsonNodeFactory.instance.objectNode();
        data.put("id", "inv123");
        data.put("nuevaCantidadStock", 50);

        Inventario inventario = new Inventario();
        when(inventarioService.actualizarInventario("inv123", 50)).thenReturn(inventario);

        PayloadDTO payload = new PayloadDTO();
        payload.setAction("actualizarInventario");
        payload.setBody(data);

        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setData(payload);

        Object result = inventoryConsumer.handleInventoryQueue(messageDTO);
        assertTrue(result instanceof Inventario);
    }

    @Test
    public void testHandleAccionNoReconocida() {
        PayloadDTO payload = new PayloadDTO();
        payload.setAction("accionFalsa");
        payload.setBody(null);

        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setData(payload);

        Object result = inventoryConsumer.handleInventoryQueue(messageDTO);
        assertEquals("Acción no reconocida: accionFalsa", result);
    }

    @Test
    public void testHandleNullPayload() {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setData(null);

        Object result = inventoryConsumer.handleInventoryQueue(messageDTO);
        assertTrue(result.toString().contains("Error"));
    }
}
