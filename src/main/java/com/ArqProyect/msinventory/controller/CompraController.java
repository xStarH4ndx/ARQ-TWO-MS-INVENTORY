package com.ArqProyect.msinventory.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ArqProyect.msinventory.dto.CompraCreacionDTO;
import com.ArqProyect.msinventory.model.Compra;
import com.ArqProyect.msinventory.service.CompraService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequestMapping("/compras")
@RequiredArgsConstructor
public class CompraController {
    private final CompraService compraService;

    @GetMapping
    public ResponseEntity<List<Compra>> listarCompras() {
        return new ResponseEntity<>(compraService.listarCompras(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Compra> obtenerCompraPorId(@PathVariable String id) {
        Compra compra = compraService.obtenerCompraPorId(id);
        if (compra != null) {
            return new ResponseEntity<>(compra, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<?> crearCompra(@Valid @RequestBody CompraCreacionDTO compraCreacionDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.BAD_REQUEST);
        }
        Compra nuevaCompra = compraService.crearCompraDesdeDTO(compraCreacionDTO);
        return new ResponseEntity<>(nuevaCompra, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCompra(@PathVariable String id) {
        compraService.eliminarCompra(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
