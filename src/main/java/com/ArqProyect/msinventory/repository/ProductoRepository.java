package com.ArqProyect.msinventory.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ArqProyect.msinventory.model.Producto;

public interface ProductoRepository extends MongoRepository<Producto, String> {
}
