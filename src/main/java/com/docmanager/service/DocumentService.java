package com.docmanager.service;

import com.docmanager.dto.request.UpdateDocumentRequest;
import com.docmanager.dto.response.DocumentResponse;
import com.docmanager.entity.Document;
import com.docmanager.entity.User;
import com.docmanager.exception.AppExceptions;
import com.docmanager.mapper.DocumentMapper;
import com.docmanager.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;
    private final FileStorageService fileStorageService;

    @Transactional
    public DocumentResponse uploadDocument(MultipartFile file, String description, String category, User user) {
        String storedName = fileStorageService.storeFile(file);

        Document document = Document.builder()
                .originalName(file.getOriginalFilename())
                .storedName(storedName)
                .filePath(fileStorageService.getFilePath(storedName))
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .description(description)
                .category(category)
                .uploadedBy(user)
                .build();

        Document saved = documentRepository.save(document);
        log.info("Documento subido: {} por {}", saved.getOriginalName(), user.getUsername());
        return documentMapper.toResponse(saved);
    }

    public Page<DocumentResponse> getAllDocuments(Pageable pageable) {
        return documentRepository.findAllByDeletedFalse(pageable)
                .map(documentMapper::toResponse);
    }

    public Page<DocumentResponse> searchDocuments(String name, LocalDateTime startDate,
                                                   LocalDateTime endDate, String category, Pageable pageable) {
        return documentRepository.search(name, startDate, endDate, category, pageable)
                .map(documentMapper::toResponse);
    }

    public DocumentResponse getDocumentById(Long id) {
        Document document = documentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppExceptions.ResourceNotFoundException("Documento no encontrado con id: " + id));
        return documentMapper.toResponse(document);
    }

    public Resource downloadDocument(Long id) {
        Document document = documentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppExceptions.ResourceNotFoundException("Documento no encontrado con id: " + id));
        return fileStorageService.loadFileAsResource(document.getStoredName());
    }

    public Document getDocumentEntity(Long id) {
        return documentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppExceptions.ResourceNotFoundException("Documento no encontrado con id: " + id));
    }

    @Transactional
    public DocumentResponse updateDocument(Long id, UpdateDocumentRequest request) {
        Document document = documentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppExceptions.ResourceNotFoundException("Documento no encontrado con id: " + id));

        if (request.getDescription() != null) {
            document.setDescription(request.getDescription());
        }
        if (request.getCategory() != null) {
            document.setCategory(request.getCategory());
        }

        Document updated = documentRepository.save(document);
        log.info("Documento actualizado: {}", updated.getOriginalName());
        return documentMapper.toResponse(updated);
    }

    @Transactional
    public void deleteDocument(Long id) {
        Document document = documentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new AppExceptions.ResourceNotFoundException("Documento no encontrado con id: " + id));
        document.setDeleted(true);
        documentRepository.save(document);
        log.info("Documento eliminado (soft delete): {}", document.getOriginalName());
    }
}
