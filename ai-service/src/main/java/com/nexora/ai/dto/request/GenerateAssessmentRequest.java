package com.nexora.ai.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateAssessmentRequest {

    @NotBlank(message = "Topic is required")
    @Size(max = 100, message = "Topic must not exceed 100 characters")
    private String topic;

    @Size(max = 10, message = "Subtopics list must not exceed 10 items")
    private List<String> subtopics;

    @NotNull(message = "MCQ count is required")
    @Min(value = 1, message = "MCQ count must be at least 1")
    @Max(value = 50, message = "MCQ count must not exceed 50")
    private Integer mcqCount;

    @NotNull(message = "Coding count is required")
    @Min(value = 0, message = "Coding count must be at least 0")
    @Max(value = 10, message = "Coding count must not exceed 10")
    private Integer codingCount;

    private String difficultyHint;

    @NotBlank(message = "Language is required")
    private String language;
}
