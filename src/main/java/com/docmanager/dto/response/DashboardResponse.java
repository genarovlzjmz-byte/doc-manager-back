package com.docmanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private long totalDocuments;
    private long totalUsers;
    private long totalStorageBytes;
    private String totalStorageFormatted;
}
