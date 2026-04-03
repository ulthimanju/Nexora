package com.nexora.assessment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionOptionResponse {

    private UUID id;
    private String text;
    private Integer orderIndex;
    // Note: isCorrect is NEVER exposed
}
