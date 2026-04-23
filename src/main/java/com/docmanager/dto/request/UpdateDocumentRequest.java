package com.docmanager.dto.request;

import lombok.Data;

@Data
public class UpdateDocumentRequest {
    private String description;
    private String category;
}
