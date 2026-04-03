package com.nexora.assessment.dto.response;

import com.nexora.assessment.domain.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionResponse {

    private UUID id;
    private QuestionType type;
    private String text;
    private String codeTemplate;
    private Integer marks;
    private Integer orderIndex;
    private List<QuestionOptionResponse> options;
}
