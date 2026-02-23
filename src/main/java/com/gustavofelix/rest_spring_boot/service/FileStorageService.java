package com.gustavofelix.rest_spring_boot.service;

import com.gustavofelix.rest_spring_boot.config.FileStorageConfig;
import com.gustavofelix.rest_spring_boot.controllers.FileController;
import com.gustavofelix.rest_spring_boot.exception.FileStorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(FileStorageConfig fileStorageConfig) {
        Path path = Paths.get(fileStorageConfig.getUploadDir()).toAbsolutePath().normalize(); // Cria um caminho absoluto sem as sequências perigosas como "../".

        this.fileStorageLocation = path;

        try {
            logger.info("Creating Directory {}", this.fileStorageLocation);
            Files.createDirectories(this.fileStorageLocation); // Cria o diretório, caso não exista
        } catch (IOException e) {
            logger.error("Could not create the directory where files will be created!");
            throw new FileStorageException("Could not create the directory where files will be created!");
        }
    }

    public String storeFile(MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename()); // Remove caracteres inválidos do nome do arquivo.
        try {
            if (fileName.contains("..")) {
                logger.warn("Cannot store file outside current directory {}.", fileName);
                throw new FileStorageException("Sorry Filename Contains a Invalid path Sequence " + fileName + "."); //Impede nomes como: ../../etc/passwd
            }
            logger.info("Saving file in disk!");
            Path targetLocation = this.fileStorageLocation.resolve(fileName); // Aplica o nome do diretório com o nome do arquivo - EX "/uploads/fileName"
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING); // Salva o arquivo, e caso existir, ele será sobrescrito.
            return fileName;
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", e);
        }
    }

}
