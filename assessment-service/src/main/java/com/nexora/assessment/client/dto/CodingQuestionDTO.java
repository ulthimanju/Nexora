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
public class CodingQuestionDTO {
    private int orderIndex;
    private String questionText;
    private int marks;
    private String difficulty;
    private String starterCode;
    private String language;
    private List<TestCaseDTO> testCases;
}
