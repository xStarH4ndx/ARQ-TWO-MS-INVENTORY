package com.ArqProyect.msinventory.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "compras")
public class Compra {
    @Id
    private String id;
    private String casaId;
    private String fechaCompra;
    private List<ItemCompra> itemsCompra;    
}
