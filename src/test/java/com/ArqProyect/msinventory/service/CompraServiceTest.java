package com.ArqProyect.msinventory.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.ArqProyect.msinventory.dto.CompraCreacionDTO;
import com.ArqProyect.msinventory.dto.ItemCompraEventoDTO;
import com.ArqProyect.msinventory.model.Compra;
import com.ArqProyect.msinventory.repository.CompraRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class CompraServiceTest {

    @Mock
    private CompraRepository compraRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CompraService compraService;

    @Test
    void listarCompras_devuelveListaDeCompras() {
        Compra compra = new Compra();
        compra.setId("1L");
        compra.setCasaId("casa-1");

        when(compraRepository.findAll()).thenReturn(List.of(compra));

        List<Compra> resultado = compraService.listarCompras();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("casa-1", resultado.get(0).getCasaId());
    }

    @Test
    void obtenerCompraPorId_existente_devuelveCompra() {
        Compra compra = new Compra();
        compra.setId("1L");
        compra.setCasaId("casa-2");

        when(compraRepository.findById("1L")).thenReturn(Optional.of(compra));

        Compra compraBuscada = compraService.obtenerCompraPorId("1L");

        assertNotNull(compraBuscada);
        assertEquals("casa-2", compraBuscada.getCasaId());
    }

    @Test
    void obtenerCompraPorId_noExistente_lanzaExcepcion() {
        when(compraRepository.findById("99L")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            compraService.obtenerCompraPorId("99L");
        });
    }

    @Test
    void eliminarCompra_existente_noLanzaExcepcion() {
        Compra compra = new Compra();
        compra.setId("1L");

        when(compraRepository.findById("1L")).thenReturn(Optional.of(compra));
        doNothing().when(compraRepository).delete(compra);

        assertDoesNotThrow(() -> compraService.eliminarCompra("1L"));
        verify(compraRepository, times(1)).delete(compra);
    }

    @Test
    void eliminarCompra_noExistente_lanzaExcepcion() {
        when(compraRepository.findById("999L")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            compraService.eliminarCompra("999L");
        });
    }


    @Test
    void crearCompra_conItemsValidos_noLanzaExcepcion() {
        // ---------- DTO de entrada ----------
        CompraCreacionDTO dto = new CompraCreacionDTO();
        dto.setCasaId("casa-123");
        dto.setFechaCompra(new Date());          // ← ya no es null

        // ---------- Item válido ----------
        ItemCompraEventoDTO item = new ItemCompraEventoDTO();
        item.setProductoId("prod-1");
        item.setNombreProducto("Jabón");
        item.setCantidad(2);
        item.setPrecioUnitario(1.5);
        item.setEsCompartido(false);
        item.setPropietarioId("usuario-99");     // requerido porque no es compartido

        dto.setItems(List.of(item));             // ← evita el NPE en getItems()

        // ---------- mocks ----------
        when(compraRepository.save(any(Compra.class))).thenReturn(new Compra());

        // ---------- verificación ----------
        assertDoesNotThrow(() -> compraService.crearCompraDesdeDTO(dto));
    }



    @Test
    void crearCompra_conItemNoCompartidoSinPropietario_lanzaExcepcion() {
        // Arrange
        CompraCreacionDTO dto = new CompraCreacionDTO();
        ItemCompraEventoDTO item = new ItemCompraEventoDTO();
        item.setEsCompartido(false);  // No compartido
        item.setPropietarioId(null);  // Pero sin propietario

        dto.setItems(List.of(item));

        // Act + Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            compraService.crearCompraDesdeDTO(dto);
        });

        assertEquals("Items no compartidos deben tener propietarioId", exception.getMessage());
    }

    @Test
    void crearCompra_conItemCompartidoSinPropietario_noLanzaExcepcion() {
        ItemCompraEventoDTO item = new ItemCompraEventoDTO();
        item.setEsCompartido(true); // Compartido: no necesita propietario
        item.setPropietarioId(null);

        CompraCreacionDTO dto = new CompraCreacionDTO();
        dto.setItems(List.of(item));
        dto.setFechaCompra(new Date()); // <-- Aquí se soluciona el NPE

        when(compraRepository.save(any(Compra.class))).thenReturn(new Compra()); // mock necesario

        assertDoesNotThrow(() ->
                compraService.crearCompraDesdeDTO(dto));
    }

}
