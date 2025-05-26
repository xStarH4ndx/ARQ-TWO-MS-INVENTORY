package com.ArqProyect.msinventory.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ArqProyect.msinventory.model.Producto;
import com.ArqProyect.msinventory.repository.ProductoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

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
    void listarProductos_retornaLista() {
        List<Producto> productos = Arrays.asList(
            new Producto("1", "Prod1", "Cat1", "Desc1"),
            new Producto("2", "Prod2", "Cat2", "Desc2")
        );

        when(productoRepository.findAll()).thenReturn(productos);

        List<Producto> resultado = productoService.listarProductos();

        assertEquals(2, resultado.size());
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void obtenerProductoPorId_existente_retornaProducto() {
        Producto producto = new Producto("1", "Prod1", "Cat1", "Desc1");
        when(productoRepository.findById("1")).thenReturn(Optional.of(producto));

        Producto resultado = productoService.obtenerProductoPorId("1");

        assertNotNull(resultado);
        assertEquals("Prod1", resultado.getNombre());
        verify(productoRepository).findById("1");
    }

    @Test
    void obtenerProductoPorId_noExistente_lanzaExcepcion() {
        when(productoRepository.findById("99")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productoService.obtenerProductoPorId("99");
        });

        assertTrue(exception.getMessage().contains("Producto no encontrado"));
        verify(productoRepository).findById("99");
    }

    @Test
    void crearProducto_guardaYRetornaProducto() {
        Producto producto = new Producto(null, "ProdNuevo", "CatN", "DescN");
        Producto productoGuardado = new Producto("1", "ProdNuevo", "CatN", "DescN");

        when(productoRepository.save(producto)).thenReturn(productoGuardado);

        Producto resultado = productoService.crearProducto(producto);

        assertEquals("1", resultado.getId());
        assertEquals("ProdNuevo", resultado.getNombre());
        verify(productoRepository).save(producto);
    }

    @Test
    void deleteProducto_eliminaProducto() {
        doNothing().when(productoRepository).deleteById("1");

        productoService.deleteProducto("1");

        verify(productoRepository).deleteById("1");
    }
}
