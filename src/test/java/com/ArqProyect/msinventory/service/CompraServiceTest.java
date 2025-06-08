package com.ArqProyect.msinventory.service;

import com.ArqProyect.msinventory.dto.CompraCreacionDTO;
import com.ArqProyect.msinventory.dto.ItemCompraEventoDTO;
import com.ArqProyect.msinventory.model.Compra;
import com.ArqProyect.msinventory.repository.CompraRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

public class CompraServiceTest {

    @Mock
    private CompraRepository compraRepository;

    @InjectMocks
    private CompraService compraService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crearCompra_conDatosValidos_debeGuardarCompra() {
        ItemCompraEventoDTO itemDTO = new ItemCompraEventoDTO(
                "producto123", "Producto Prueba", 2, 1000.0, false, "usuario123");

        CompraCreacionDTO compraDTO = new CompraCreacionDTO(
                "casa123", new Date(), List.of(itemDTO));

        Compra compraMock = new Compra();
        when(compraRepository.save(any(Compra.class))).thenReturn(compraMock);

        Compra resultado = compraService.crearCompraDesdeDTO(compraDTO);

        assertNotNull(resultado);
        verify(compraRepository, times(1)).save(any(Compra.class));
    }

    @Test
    void crearCompra_conListaVacia_debeLanzarExcepcion() {
        CompraCreacionDTO compraDTO = new CompraCreacionDTO(
                "casa123", new Date(), new ArrayList<>());

        assertThrows(IllegalArgumentException.class, () -> {
            compraService.crearCompraDesdeDTO(compraDTO);
        });

        verify(compraRepository, never()).save(any());
    }

    @Test
    void crearCompra_conItemCompartidoYPropietarioIdNull_debeLanzarExcepcion() {
        ItemCompraEventoDTO itemDTO = new ItemCompraEventoDTO(
                "producto456", "Producto Compartido", 3, 1500.0, true, null);

        CompraCreacionDTO compraDTO = new CompraCreacionDTO(
                "casa456", new Date(), List.of(itemDTO));

        assertThrows(IllegalArgumentException.class, () -> {
            compraService.crearCompraDesdeDTO(compraDTO);
        });

        verify(compraRepository, never()).save(any());
    }

    @Test
    void crearCompra_conMultiplesItems_debeGuardarCorrectamente() {
        ItemCompraEventoDTO item1 = new ItemCompraEventoDTO(
                "p1", "Arroz", 1, 500.0, false, "u1");
        ItemCompraEventoDTO item2 = new ItemCompraEventoDTO(
                "p2", "Azúcar", 2, 700.0, true, "u2");

        CompraCreacionDTO compraDTO = new CompraCreacionDTO(
                "casa789", new Date(), List.of(item1, item2));

        when(compraRepository.save(any(Compra.class))).thenReturn(new Compra());

        Compra resultado = compraService.crearCompraDesdeDTO(compraDTO);

        assertNotNull(resultado);
        verify(compraRepository).save(any());
    }
}
