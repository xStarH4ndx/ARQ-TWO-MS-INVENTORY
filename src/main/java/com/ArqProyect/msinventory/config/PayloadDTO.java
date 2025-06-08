package com.ArqProyect.msinventory.config;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class PayloadDTO {
    private String action;
    private JsonNode body;
}