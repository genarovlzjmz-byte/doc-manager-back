package com.docmanager.service;

import com.docmanager.exception.AppExceptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {

    @Value("${app.file.upload-dir}")
    private String uploadDir;

    @Value("${app.file.max-size}")
    private long maxFileSize;

    @Value("${app.file.allowed-types}")
    private String allowedTypes;

    private Path fileStoragePath;
    private List<String> allowedTypesList;

    @PostConstruct
    public void init() {
        fileStoragePath = Paths.get(uploadDir).toAbsolutePath().normalize();
        allowedTypesList = Arrays.asList(allowedTypes.split(","));
        try {
            Files.createDirectories(fileStoragePath);
            log.info("Directorio de uploads creado: {}", fileStoragePath);
        } catch (IOException e) {
            throw new AppExceptions.FileStorageException("No se pudo crear el directorio de uploads", e);
        }
    }

    public String storeFile(MultipartFile file) {
        validateFile(file);

        String originalName = file.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf("."));
        }
        String storedName = UUID.randomUUID().toString() + extension;

        try {
            Path targetLocation = fileStoragePath.resolve(storedName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("Archivo almacenado: {} -> {}", originalName, storedName);
            return storedName;
        } catch (IOException e) {
            throw new AppExceptions.FileStorageException("Error al almacenar archivo: " + originalName, e);
        }
    }

    public Resource loadFileAsResource(String storedName) {
        try {
            Path filePath = fileStoragePath.resolve(storedName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new AppExceptions.ResourceNotFoundException("Archivo no encontrado: " + storedName);
            }
        } catch (MalformedURLException e) {
            throw new AppExceptions.FileStorageException("Archivo no encontrado: " + storedName, e);
        }
    }

    public void deleteFile(String storedName) {
        try {
            Path filePath = fileStoragePath.resolve(storedName).normalize();
            Files.deleteIfExists(filePath);
            log.info("Archivo eliminado del filesystem: {}", storedName);
        } catch (IOException e) {
            log.error("Error al eliminar archivo: {}", storedName, e);
        }
    }

    public String getFilePath(String storedName) {
        return fileStoragePath.resolve(storedName).toString();
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new AppExceptions.BadRequestException("El archivo está vacío");
        }
        if (file.getSize() > maxFileSize) {
            throw new AppExceptions.BadRequestException(
                    "El archivo excede el tamaño máximo permitido (" + (maxFileSize / 1024 / 1024) + "MB)");
        }
        String contentType = file.getContentType();
        if (contentType == null || !allowedTypesList.contains(contentType)) {
            throw new AppExceptions.BadRequestException(
                    "Tipo de archivo no permitido: " + contentType + ". Permitidos: PDF, DOC, DOCX, XLS, XLSX, PPT, PPTX, JPG, PNG, GIF, TXT");
        }
    }
}
