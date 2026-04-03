package com.nexora.assessment.dto.response;

import com.nexora.assessment.domain.enums.AssessmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentResponse {

    private UUID id;
    private UUID courseId;
    private String title;
    private String description;
    private Integer totalMarks;
    private Integer mcqMarks;
    private Integer codingMarks;
    private Integer durationMinutes;
    private AssessmentStatus status;
}
