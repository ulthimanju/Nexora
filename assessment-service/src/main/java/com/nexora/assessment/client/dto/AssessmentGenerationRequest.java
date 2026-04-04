package com.nexora.assessment.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentGenerationRequest {
    private String topic;
    private List<String> subtopics;
    private Integer mcqCount;
    private Integer codingCount;
    private String difficultyHint;
    private String language;
}
