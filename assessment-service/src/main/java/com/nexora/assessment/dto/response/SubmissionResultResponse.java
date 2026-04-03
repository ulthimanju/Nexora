package com.nexora.assessment.dto.response;

import com.nexora.assessment.domain.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmissionResultResponse {

    private UUID submissionId;
    private UUID attemptId;
    private UUID assessmentId;
    private Integer mcqScore;
    private Integer codingScore;
    private Integer totalScore;
    private Integer totalMarks;
    private Category category;
    private LocalDateTime submittedAt;
}
