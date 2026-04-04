package com.nexora.assessment.messaging;

import com.nexora.assessment.event.AssessmentCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AssessmentCompletedConsumer {

    // TODO: This is a development stub. Move to progress-service when it exists.

    @RabbitListener(queues = "nexora.assessment.completed.queue")
    public void handleAssessmentCompleted(AssessmentCompletedEvent event) {
        log.info("[STUB] AssessmentCompleted received: userId={}, courseId={}, score={}/{}",
                event.getUserId(), event.getCourseId(), event.getScore(), event.getTotalMarks());
        // TODO: Forward to progress-service or update progress DB
    }
}
