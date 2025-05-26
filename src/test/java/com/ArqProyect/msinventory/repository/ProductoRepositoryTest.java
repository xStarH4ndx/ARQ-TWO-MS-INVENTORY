package com.ArqProyect.msinventory.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import com.ArqProyect.msinventory.model.Producto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest  // Configura test con Mongo embebido (Flapdoodle)
class ProductoRepositoryTest {

    @Autowired
    private ProductoRepository productoRepository;

    @BeforeEach
    void clean() {
        productoRepository.deleteAll();
    }

    @Test
    void testGuardarYBuscarProducto() {
        Producto producto = new Producto();
        producto.setNombre("Producto Test");
        producto.setDescripcion("Descripción de prueba");
        producto.setCategoria("Categoría Test");

        Producto guardado = productoRepository.save(producto);

        assertNotNull(guardado.getId());
        assertEquals("Producto Test", guardado.getNombre());

        List<Producto> productos = productoRepository.findAll();
        assertEquals(1, productos.size());
        assertEquals("Producto Test", productos.get(0).getNombre());
    }

    @Test
    void testEliminarProducto() {
        Producto producto = new Producto();
        producto.setNombre("Producto a eliminar");

        Producto guardado = productoRepository.save(producto);
        assertEquals(1, productoRepository.count());

        productoRepository.deleteById(guardado.getId());
        assertEquals(0, productoRepository.count());
    }
}
