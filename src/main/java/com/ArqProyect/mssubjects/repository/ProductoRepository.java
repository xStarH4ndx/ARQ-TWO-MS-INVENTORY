package com.ArqProyect.mssubjects.repository;

import com.ArqProyect.mssubjects.model.Producto;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductoRepository extends MongoRepository<Producto, String> {
}
