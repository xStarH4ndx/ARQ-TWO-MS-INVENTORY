package com.ArqProyect.mssubjects.model;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "productos")
@Data

public class Producto {
    @Id
    private String id;
    private String nombre;
    private String descripcion;
    private String categoria;
}
