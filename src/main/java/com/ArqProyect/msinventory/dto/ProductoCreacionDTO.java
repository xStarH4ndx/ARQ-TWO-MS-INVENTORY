package com.ArqProyect.msinventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoCreacionDTO {

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre del producto debe tener entre 2 y 100 caracteres")
    private String nombre;

    @NotBlank(message = "La categoría del producto es obligatoria")
    private String categoria;

    @Size(max = 255, message = "La descripción no puede exceder los 255 caracteres")
    private String descripcion;
}
