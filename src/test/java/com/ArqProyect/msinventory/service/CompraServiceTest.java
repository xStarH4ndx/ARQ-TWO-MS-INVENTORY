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
        compraDTO.setItems(Arrays.asList(new ItemCompraEventoDTO(), new ItemCompraEventoDTO()));

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
        compraDTO.setItems(Arrays.asList(new ItemCompraEventoDTO()));

        // Si la lógica de validar propietarioId nulo está dentro del service, simular ese caso
        // Aquí como ejemplo, asumimos que si propietarioId es null en algún item, lanza excepción.
        compraDTO.getItems().get(0).setPropietarioId(null);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            compraService.crearCompraDesdeDTO(compraDTO);
        });
        assertEquals("PropietarioId no puede ser null", thrown.getMessage());
    }

    @Test
    public void crearCompra_conMultiplesItems_debeGuardarCorrectamente() {
        CompraCreacionDTO compraDTO = new CompraCreacionDTO();
        compraDTO.setItems(Arrays.asList(new ItemCompraEventoDTO(), new ItemCompraEventoDTO(), new ItemCompraEventoDTO()));

        Compra compra = compraService.crearCompraDesdeDTO(compraDTO);

        assertNotNull(compra);
        assertEquals(3, compra.getItemsCompra().size());
    }
}
