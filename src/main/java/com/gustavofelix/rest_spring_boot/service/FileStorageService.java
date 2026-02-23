package com.gustavofelix.rest_spring_boot.service;

import com.gustavofelix.rest_spring_boot.config.FileStorageConfig;
import com.gustavofelix.rest_spring_boot.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(FileStorageConfig fileStorageConfig) {
        Path path = Paths.get(fileStorageConfig.getUploadDir()).toAbsolutePath().normalize(); // Cria um caminho absoluto sem as sequências perigosas como "../".

        this.fileStorageLocation = path;

        try {
            Files.createDirectories(this.fileStorageLocation); // Cria o diretório, caso não exista
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename()); // Remove caracteres inválidos do nome do arquivo.
        try {
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry Filename Contains a Invalid path Sequence " + fileName + "."); //Impede nomes como: ../../etc/passwd
            }
            Path targetLocation = this.fileStorageLocation.resolve(fileName); // Aplica o nome do diretório com o nome do arquivo - EX "/uploads/fileName"
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING); // Salva o arquivo, e caso existir, ele será sobrescrito.
            return fileName;
        } catch (Exception e) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", e);
        }
    }

}
