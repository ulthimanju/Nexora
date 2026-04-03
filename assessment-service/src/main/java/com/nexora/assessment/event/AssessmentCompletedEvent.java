package com.nexora.assessment.event;

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
public class AssessmentCompletedEvent {

    private UUID userId;
    private UUID courseId;
    private UUID assessmentId;
    private Integer score;
    private Integer totalMarks;
    private Category category;
    private LocalDateTime completedAt;
}
