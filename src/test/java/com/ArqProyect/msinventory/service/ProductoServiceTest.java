package com.ArqProyect.msinventory.service;

import com.ArqProyect.msinventory.dto.ProductoCreacionDTO;
import com.ArqProyect.msinventory.model.Producto;
import com.ArqProyect.msinventory.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void testListarProductos() {
        List<Producto> productos = List.of(new Producto("1", "Pan", "Alimento", "Pan integral"));
        when(productoRepository.findAll()).thenReturn(productos);

        List<Producto> resultado = productoService.listarProductos();

        assertEquals(1, resultado.size());
        verify(productoRepository).findAll();
    }

    @Test
    void testObtenerProductoPorId() {
        Producto producto = new Producto("1", "Agua", "Bebida", "Agua purificada");
        when(productoRepository.findById("1")).thenReturn(Optional.of(producto));

        Producto resultado = productoService.obtenerProductoPorId("1");

        assertEquals("Agua", resultado.getNombre());
    }

    @Test
    void testObtenerProductoPorId_NotFound() {
        when(productoRepository.findById("2")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productoService.obtenerProductoPorId("2"));
    }

    @Test
    void testCrearProducto() {
        ProductoCreacionDTO dto = new ProductoCreacionDTO("Cafe", "Bebida", "Cafe molido");
        Producto producto = new Producto(null, "Cafe", "Bebida", "Cafe molido");

        when(productoRepository.save(any(Producto.class)))
                .thenAnswer(invocation -> {
                    Producto p = invocation.getArgument(0);
                    p.setId("123");
                    return p;
                });

        Producto creado = productoService.crearProducto(dto);

        assertEquals("Cafe", creado.getNombre());
        assertEquals("123", creado.getId());
        assertNotNull(producto);
    }

    @Test
    void testEliminarProducto() {
        productoService.deleteProducto("123");
        verify(productoRepository).deleteById("123");
    }
}
