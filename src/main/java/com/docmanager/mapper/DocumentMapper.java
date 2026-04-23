package com.docmanager.mapper;

import com.docmanager.dto.response.DocumentResponse;
import com.docmanager.entity.Document;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DocumentMapper {

    @Mapping(target = "uploadedBy", source = "uploadedBy.fullName")
    DocumentResponse toResponse(Document document);

    List<DocumentResponse> toResponseList(List<Document> documents);
}
