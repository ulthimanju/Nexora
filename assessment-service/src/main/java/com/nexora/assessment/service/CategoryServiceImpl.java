package com.nexora.assessment.service;

import com.nexora.assessment.config.CategoryThresholdConfig;
import com.nexora.assessment.constants.LogMessages;
import com.nexora.assessment.domain.enums.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryThresholdConfig thresholdConfig;

    @Override
    public Category computeCategory(int score) {
        Category category;

        if (score >= thresholdConfig.getExpert()) {
            category = Category.EXPERT;
        } else if (score >= thresholdConfig.getAdvanced()) {
            category = Category.ADVANCED;
        } else if (score >= thresholdConfig.getBasic()) {
            category = Category.BASIC;
        } else {
            category = Category.BEGINNER;
        }

        log.info(LogMessages.CATEGORY_COMPUTED, score, category);
        return category;
    }
}
