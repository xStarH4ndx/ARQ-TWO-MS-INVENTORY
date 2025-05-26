package com.ArqProyect.msinventory.model;
import lombok.*;

//clase embebida para los items de compra
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCompra {
    private String productoId;
    private String nombreProducto;
    private Integer cantidad;
    private Double precioUnitario;
    private Boolean esCompartido;
    private String propietarioId;
}
