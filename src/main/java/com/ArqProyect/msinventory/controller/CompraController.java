package com.ArqProyect.msinventory.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ArqProyect.msinventory.model.Compra;
import com.ArqProyect.msinventory.service.CompraService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/compras")
@RequiredArgsConstructor
public class CompraController {
    private final CompraService compraService;

    @GetMapping
    public List<Compra> listarCompras() {
        return compraService.listarCompras();
    }

    @GetMapping("/{id}")
    public Compra obtenerCompraPorId(String id) {
        return compraService.obtenerCompraPorId(id);
    }

    @PostMapping
    public Compra crearCompra(Compra compra) {
        return compraService.crearCompra(compra);
    }

    @DeleteMapping("/{id}")
    public void eliminarCompra(String id) {
        compraService.eliminarCompra(id);
    }
}
