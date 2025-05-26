package com.ArqProyect.msinventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCompraEventoDTO {

    @NotBlank(message = "El ID del producto es obligatorio")
    private String productoId;

    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombreProducto;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor o igual a 1")
    private Integer cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    private Double precioUnitario;

    @NotNull(message = "Debe indicar si el item es compartido")
    private Boolean esCompartido;

    private String propietarioId; // Podría ser opcional dependiendo de tu lógica
}