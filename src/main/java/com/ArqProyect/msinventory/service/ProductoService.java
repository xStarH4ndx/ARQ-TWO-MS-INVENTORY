package com.ArqProyect.msinventory.service;

import org.springframework.stereotype.Service;

import com.ArqProyect.msinventory.dto.ProductoCreacionDTO;
import com.ArqProyect.msinventory.model.Producto;
import com.ArqProyect.msinventory.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductoService {
    private final ProductoRepository productoRepository;

    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    public Producto obtenerProductoPorId(String id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }

    public Producto crearProducto(ProductoCreacionDTO payload) {
        Producto nuevoProducto = new Producto();
        nuevoProducto.setNombre(payload.getNombre());
        nuevoProducto.setCategoria(payload.getCategoria());
        nuevoProducto.setDescripcion(payload.getDescripcion());
        return productoRepository.save(nuevoProducto);
    }

    public void deleteProducto(String id) {
        productoRepository.deleteById(id);
    }
}
