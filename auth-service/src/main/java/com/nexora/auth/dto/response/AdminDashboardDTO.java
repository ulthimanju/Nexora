package com.nexora.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardDTO {
    private String status;
    private String message;
    private long totalUsers;
    private long adminCount;
    private long instructorCount;
    private long studentCount;
}
