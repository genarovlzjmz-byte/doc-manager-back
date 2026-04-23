package com.docmanager.service;

import com.docmanager.dto.response.DashboardResponse;
import com.docmanager.repository.DocumentRepository;
import com.docmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;

    public DashboardResponse getStats() {
        long totalDocs = documentRepository.countByDeletedFalse();
        long totalUsers = userRepository.findAllByDeletedFalse().size();
        long totalStorage = documentRepository.getTotalStorageUsed();

        return DashboardResponse.builder()
                .totalDocuments(totalDocs)
                .totalUsers(totalUsers)
                .totalStorageBytes(totalStorage)
                .totalStorageFormatted(formatFileSize(totalStorage))
                .build();
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }
}
