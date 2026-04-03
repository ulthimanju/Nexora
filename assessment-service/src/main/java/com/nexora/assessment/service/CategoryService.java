package com.nexora.assessment.service;

import com.nexora.assessment.domain.enums.Category;

public interface CategoryService {
    Category computeCategory(int score);
}
