package com.ArqProyect.mssubjects.service;

import com.ArqProyect.mssubjects.model.Producto;
import com.ArqProyect.mssubjects.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ProductoService {
    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public List<Producto> listarProductos() {
        return productoRepository.findAll();
    }

    public Producto crearProducto(Producto producto) {
        return productoRepository.save(producto);
    }
}
