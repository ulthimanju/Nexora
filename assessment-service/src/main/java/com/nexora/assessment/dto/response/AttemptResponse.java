package com.nexora.assessment.dto.response;

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
public class AttemptResponse {

    private UUID attemptId;
    private UUID assessmentId;
    private LocalDateTime startedAt;
    private LocalDateTime expiresAt;
    private Boolean isSubmitted;
}
