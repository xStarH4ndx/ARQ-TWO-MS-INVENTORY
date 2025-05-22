package com.ArqProyect.mssubjects.controller;

import com.ArqProyect.mssubjects.model.Producto;
import com.ArqProyect.mssubjects.service.ProductoService;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/productos")
public class ProductoController {
    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public List<Producto> listarProductos() {
        return productoService.listarProductos();
    }

    @PostMapping
    public Producto crearProducto(@RequestBody Producto producto) {
        return productoService.crearProducto(producto);
    }
}
