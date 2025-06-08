package com.ArqProyect.msinventory.service;

import com.ArqProyect.msinventory.model.Inventario;
import com.ArqProyect.msinventory.repository.InventarioRepository;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks
    private InventarioService inventarioService;

    @Test
    void testListarInventarioCasa() {
        List<Inventario> lista = List.of(new Inventario("1", "casa123", "prod123", "Pan", 10));
        when(inventarioRepository.findByCasaId("casa123")).thenReturn(Optional.of(lista));

        Optional<List<Inventario>> result = inventarioService.listarInventarioCasa("casa123");

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
    }

    @Test
    void testAumentarStockExistente() {
        Inventario inventario = new Inventario("1", "casa1", "prod1", "Agua", 5);
        when(inventarioRepository.findByCasaIdAndProductoId("casa1", "prod1"))
                .thenReturn(Optional.of(inventario));

        Inventario nuevoInventario = new Inventario();
        when(inventarioRepository.save(any(Inventario.class)))
                .thenAnswer(i -> i.getArgument(0));

        Inventario resultado = inventarioService.aumentarStock("casa1", "prod1", "Agua", 3);

        assertEquals(8, resultado.getCantidadStock());
        assertNotNull(nuevoInventario);
    }

    @Test
    void testDisminuirStock() {
        Inventario inventario = new Inventario("1", "casa1", "prod1", "Azucar", 10);
        when(inventarioRepository.findByCasaIdAndProductoId("casa1", "prod1"))
                .thenReturn(Optional.of(inventario));
        when(inventarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Inventario resultado = inventarioService.disminuirStock("casa1", "prod1", 3);

        assertEquals(7, resultado.getCantidadStock());
    }

    @Test
    void testActualizarInventario() {
        Inventario inventario = new Inventario("1", "casa1", "prod1", "Leche", 5);
        when(inventarioRepository.findById("1")).thenReturn(Optional.of(inventario));
        when(inventarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Inventario resultado = inventarioService.actualizarInventario("1", 20);

        assertEquals(20, resultado.getCantidadStock());
    }

    @Test
    void testActualizarInventario_NotFound() {
        when(inventarioRepository.findById("99")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> inventarioService.actualizarInventario("99", 10));
    }
}
