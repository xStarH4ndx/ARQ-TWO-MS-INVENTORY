package com.ArqProyect.msinventory.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ArqProyect.msinventory.model.Compra;

public interface CompraRepository extends MongoRepository<Compra, String> {
    
}
