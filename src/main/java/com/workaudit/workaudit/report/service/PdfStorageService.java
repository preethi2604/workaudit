package com.workaudit.workaudit.report.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class PdfStorageService {

    private final Path storageRoot;

    public PdfStorageService(@Value("${workaudit.storage.pdf-root:./pdf-storage}") String storageRootPath) {
        this.storageRoot = Paths.get(storageRootPath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.storageRoot);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create PDF storage folder: " + storageRootPath, e);
        }
    }


    public String store(MultipartFile file, String employeeId, LocalDate expectedDate) {
        String monthFolder = expectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        Path folder = storageRoot.resolve(monthFolder);

        try {
            Files.createDirectories(folder);

            String safeEmployeeId = (employeeId == null || employeeId.isBlank())
                    ? "unknown"
                    : employeeId.replaceAll("[^a-zA-Z0-9_-]", "_");

            String fileName = safeEmployeeId + "_" + expectedDate + "_" + UUID.randomUUID() + ".pdf";
            Path target = folder.resolve(fileName);


            Files.copy(file.getInputStream(), target);

            return monthFolder + "/" + fileName;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to store the PDF for employee " + employeeId, e);
        }
    }


    public Path resolve(String storedReference) {
        Path resolved = storageRoot.resolve(storedReference).normalize();
        if (!resolved.startsWith(storageRoot)) {
            throw new IllegalArgumentException("Invalid file reference");
        }
        return resolved;
    }

    public boolean exists(String storedReference) {
        return storedReference != null && Files.exists(resolve(storedReference));
    }
}