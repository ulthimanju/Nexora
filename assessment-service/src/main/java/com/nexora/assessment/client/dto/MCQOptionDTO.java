package com.nexora.assessment.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MCQOptionDTO {
    private String label;
    private String text;
    private boolean isCorrect;
}
