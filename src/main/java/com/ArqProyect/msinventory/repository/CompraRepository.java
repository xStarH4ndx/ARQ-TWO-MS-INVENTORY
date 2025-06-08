package com.ArqProyect.msinventory.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ArqProyect.msinventory.model.Compra;

public interface CompraRepository extends MongoRepository<Compra, String> {
    // Método para encontrar compras por casaId
    List<Compra> findByCasaId(String casaId);
    
}
