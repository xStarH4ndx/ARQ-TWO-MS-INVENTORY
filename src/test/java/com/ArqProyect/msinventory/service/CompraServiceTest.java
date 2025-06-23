// CompraServiceTest.java
package com.ArqProyect.msinventory.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ArqProyect.msinventory.dto.CompraCreacionDTO;
import com.ArqProyect.msinventory.dto.ItemCompraEventoDTO;
import com.ArqProyect.msinventory.model.Compra;
import com.ArqProyect.msinventory.model.ItemCompra;
import com.ArqProyect.msinventory.repository.CompraRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

public class CompraServiceTest {

    @Mock
    private CompraRepository compraRepository;

    @Mock
    private InventarioService inventarioService;

    @InjectMocks
    private CompraService compraService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListarCompras() {
        List<Compra> compras = List.of(new Compra("1", "casa1", "fecha", List.of()));
        when(compraRepository.findByCasaId("casa1")).thenReturn(compras);

        List<Compra> result = compraService.listarCompras("casa1");
        assertEquals(1, result.size());
        verify(compraRepository).findByCasaId("casa1");
    }

    @Test
    void testObtenerCompraPorId() {
        Compra compra = new Compra("1", "casa1", "fecha", List.of());
        when(compraRepository.findById("1")).thenReturn(Optional.of(compra));

        Compra result = compraService.obtenerCompraPorId("1");
        assertEquals("1", result.getId());
    }

    @Test
    void testCrearCompraDesdeDTO() {
        ItemCompraEventoDTO itemDTO = new ItemCompraEventoDTO("prod1", "producto", 2, 10.0, false, "prop1");
        CompraCreacionDTO dto = new CompraCreacionDTO("casa1", List.of(itemDTO));

        when(compraRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        Compra result = compraService.crearCompraDesdeDTO(dto);
        assertEquals("casa1", result.getCasaId());
        verify(inventarioService).aumentarStock("casa1", "prod1", "producto", 2);
    }

    @Test
    void testEliminarCompra() {
        ItemCompra item = new ItemCompra("prod1", "producto", 2, 10.0, false, "prop1");
        Compra compra = new Compra("1", "casa1", "fecha", List.of(item));

        when(compraRepository.findById("1")).thenReturn(Optional.of(compra));

        compraService.eliminarCompra("1");
        verify(inventarioService).disminuirStock("casa1", "prod1", 2);
        verify(compraRepository).delete(compra);
    }
}
