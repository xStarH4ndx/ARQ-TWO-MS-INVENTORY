package com.ArqProyect.mssubjects.service;

import com.ArqProyect.mssubjects.model.Producto;
import com.ArqProyect.mssubjects.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCrearProducto() {
        Producto producto = new Producto();
        producto.setNombre("Arroz");
        producto.setDescripcion("Grano largo");
        producto.setCategoria("Alimentos");

        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        Producto resultado = productoService.crearProducto(producto);

        assertNotNull(resultado);
        assertEquals("Arroz", resultado.getNombre());
        verify(productoRepository, times(1)).save(producto);
    }

    @Test
    void testListarProductos() {
        Producto producto1 = new Producto();
        producto1.setId("1");
        producto1.setNombre("Producto A");

        Producto producto2 = new Producto();
        producto2.setId("2");
        producto2.setNombre("Producto B");

        when(productoRepository.findAll()).thenReturn(Arrays.asList(producto1, producto2));

        List<Producto> productos = productoService.listarProductos();

        assertEquals(2, productos.size());
        verify(productoRepository, times(1)).findAll();
    }

}
