package com.ArqProyect.msinventory.controller;

import com.ArqProyect.msinventory.dto.ProductoCreacionDTO;
import com.ArqProyect.msinventory.model.Producto;
import com.ArqProyect.msinventory.service.ProductoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/productos")
@RequiredArgsConstructor
public class ProductoController {
    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<Producto>> listarProductos() {
        return new ResponseEntity<>(productoService.listarProductos(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProductoById(@PathVariable String id) {
        Producto producto = productoService.obtenerProductoPorId(id);
        if (producto != null) {
            return new ResponseEntity<>(producto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> crearProducto(@Valid @RequestBody ProductoCreacionDTO productoCreacionDTO, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST);
        }
        Producto nuevoProducto = productoService.crearProducto(convertirDTOaEntidad(productoCreacionDTO));
        return new ResponseEntity<>(nuevoProducto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modificarProducto(@PathVariable String id, @Valid @RequestBody ProductoCreacionDTO productoCreacionDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getFieldErrors(), HttpStatus.BAD_REQUEST);
        }
        Producto productoExistente = productoService.obtenerProductoPorId(id);
        if (productoExistente != null) {
            Producto productoActualizado = convertirDTOaEntidad(productoCreacionDTO);
            productoActualizado.setId(id);
            Producto resultado = productoService.crearProducto(productoActualizado); // Usamos save para actualizar
            return new ResponseEntity<>(resultado, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable String id) {
        productoService.deleteProducto(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private Producto convertirDTOaEntidad(ProductoCreacionDTO dto) {
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setCategoria(dto.getCategoria());
        producto.setDescripcion(dto.getDescripcion());
        return producto;
    }
}
