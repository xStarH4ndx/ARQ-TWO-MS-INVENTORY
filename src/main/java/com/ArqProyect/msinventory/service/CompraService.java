package com.ArqProyect.msinventory.service;

import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.ArqProyect.msinventory.model.Compra;
import com.ArqProyect.msinventory.repository.CompraRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompraService {
    private final CompraRepository compraRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    private static final String INVENTORY_EXCHANGE = "inventory.exchange";
    private static final String COMPRA_ROUTING_KEY = "compra.registrada"; // ROUTING KEY PARA MSPAYMENTS

    public List<Compra> listarCompras() {
        return compraRepository.findAll();
    }

    public Compra obtenerCompraPorId(String id) {
        return compraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compra no encontrada con ID: " + id));
    }

    public Compra crearCompra(Compra compra) {
        Compra nuevaCompra = compraRepository.save(compra);

        try{
            String compraJson = objectMapper.writeValueAsString(nuevaCompra);
            rabbitTemplate.convertAndSend(INVENTORY_EXCHANGE, COMPRA_ROUTING_KEY, compraJson);
            System.out.println("Compra enviada a RabbitMQ: " + compraJson);
        } catch (Exception e) {
            System.err.println("Error al enviar la compra a RabbitMQ: " + e.getMessage());
        }
        return nuevaCompra;
    }

    public void eliminarCompra(String id) {
        compraRepository.deleteById(id);
    }
}
