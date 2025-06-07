// package com.ArqProyect.msinventory.controller;

// import java.util.List;

// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.ArqProyect.msinventory.model.Inventario;
// import com.ArqProyect.msinventory.service.InventarioService;

// import lombok.RequiredArgsConstructor;

// @RestController
// @RequestMapping("/inventario")
// @RequiredArgsConstructor
// public class InventarioController {
//     private final InventarioService inventarioService;

//     @GetMapping("/inventario/{casaId}")
//     public ResponseEntity<List<Inventario>> listarInventarioCasa(@PathVariable String casaId) {
//         List<Inventario> inventario = inventarioService.listarInventarioCasa(casaId);
//         return ResponseEntity.ok(inventario);
//     }
// }
