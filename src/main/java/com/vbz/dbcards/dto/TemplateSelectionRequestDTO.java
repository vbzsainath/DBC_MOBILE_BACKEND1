package com.vbz.dbcards.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TemplateSelectionRequestDTO {

    @NotBlank
    private String templateSlug;
    
    private Long templateId;
}
