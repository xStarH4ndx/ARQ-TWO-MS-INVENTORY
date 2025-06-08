package com.ArqProyect.msinventory.repository;
import com.ArqProyect.msinventory.model.Inventario;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface InventarioRepository extends MongoRepository<Inventario, String> {
    // Método para buscar inventario por casaId
    Optional<List<Inventario>> findByCasaId(String casaId);
    Optional<Inventario> findByCasaIdAndProductoId(String casaId, String productoId);
    
    
}
