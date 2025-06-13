package com.ArqProyect.msinventory.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompraCreacionDTO {

    @NotNull(message = "El ID de la casa es obligatorio")
    private String casaId;

    @NotEmpty(message = "Debe haber al menos un item en la compra")
    @Valid // Permite la validación en cascada de los ItemCompraEventoDTO
    private List<ItemCompraEventoDTO> items;
}