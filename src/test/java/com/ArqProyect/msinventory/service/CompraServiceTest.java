package com.ArqProyect.msinventory.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ArqProyect.msinventory.dto.CompraCreacionDTO;
import com.ArqProyect.msinventory.dto.ItemCompraEventoDTO;
import com.ArqProyect.msinventory.model.Compra;

public class CompraServiceTest {

    private CompraService compraService;

    @BeforeEach
    public void setUp() {
        compraService = new CompraService(null, null);
    }

    @Test
    public void crearCompra_conDatosValidos_debeGuardarCompra() {
        CompraCreacionDTO compraDTO = new CompraCreacionDTO();
        ItemCompraEventoDTO item1 = new ItemCompraEventoDTO();
        item1.setEsCompartido(false);
        item1.setPropietarioId("1L");

        ItemCompraEventoDTO item2 = new ItemCompraEventoDTO();
        item2.setEsCompartido(true);
        item2.setPropietarioId("2L");

        compraDTO.setItems(Arrays.asList(item1, item2));

        Compra compra = compraService.crearCompraDesdeDTO(compraDTO);

        assertNotNull(compra);
        assertNotNull(compra.getItemsCompra());
        assertEquals(2, compra.getItemsCompra().size());
    }

    @Test
    public void crearCompra_conListaVacia_debeLanzarExcepcion() {
        CompraCreacionDTO compraDTO = new CompraCreacionDTO();
        compraDTO.setItems(new ArrayList<>()); // lista vacía

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            compraService.crearCompraDesdeDTO(compraDTO);
        });
        assertEquals("La lista de items no puede ser nula ni vacía", thrown.getMessage());
    }

    @Test
    public void crearCompra_conItemCompartidoYPropietarioIdNull_debeLanzarExcepcion() {
        CompraCreacionDTO compraDTO = new CompraCreacionDTO();
        ItemCompraEventoDTO item = new ItemCompraEventoDTO();
        item.setEsCompartido(true);
        item.setPropietarioId(null); // Aquí se fuerza el null para testear excepción

        compraDTO.setItems(Arrays.asList(item));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            compraService.crearCompraDesdeDTO(compraDTO);
        });
        assertEquals("PropietarioId no puede ser null", thrown.getMessage());
    }

    @Test
    public void crearCompra_conMultiplesItems_debeGuardarCorrectamente() {
        CompraCreacionDTO compraDTO = new CompraCreacionDTO();

        ItemCompraEventoDTO item1 = new ItemCompraEventoDTO();
        item1.setEsCompartido(false);
        item1.setPropietarioId("1L");

        ItemCompraEventoDTO item2 = new ItemCompraEventoDTO();
        item2.setEsCompartido(true);
        item2.setPropietarioId("2L");

        ItemCompraEventoDTO item3 = new ItemCompraEventoDTO();
        item3.setEsCompartido(false);
        item3.setPropietarioId("3L");

        compraDTO.setItems(Arrays.asList(item1, item2, item3));

        Compra compra = compraService.crearCompraDesdeDTO(compraDTO);

        assertNotNull(compra);
        assertEquals(3, compra.getItemsCompra().size());
    }
}
