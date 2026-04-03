package com.nexora.assessment.event;

import com.nexora.assessment.constants.LogMessages;
import com.nexora.assessment.constants.ServiceConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AssessmentEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishAssessmentCompleted(AssessmentCompletedEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    ServiceConstants.ASSESSMENT_EXCHANGE,
                    ServiceConstants.ASSESSMENT_COMPLETED_ROUTING_KEY,
                    event
            );
            log.info(LogMessages.EVENT_PUBLISHED, "AssessmentCompleted", event.getUserId(), event.getAssessmentId());
        } catch (Exception e) {
            log.error(LogMessages.EVENT_PUBLISH_FAILED, e.getMessage(), e);
        }
    }
}
