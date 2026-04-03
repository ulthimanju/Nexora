package com.nexora.ai.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneratedAssessmentDTO {
    private String topic;
    private Instant generatedAt;
    private String provider;
    private List<MCQQuestionDTO> mcqQuestions;
    private List<CodingQuestionDTO> codingQuestions;
}
