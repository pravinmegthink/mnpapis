package com.megthink.gateway.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.megthink.gateway.model.FileStorage;

@Service
public class FileStorageService {

	private static final Logger _logger = LoggerFactory.getLogger(FileStorageService.class);

	private final Path fileStorageLocation;

	@Autowired
	public FileStorageService(FileStorage fileStoragePojo) {
		this.fileStorageLocation = Paths.get(fileStoragePojo.getUploadDir()).toAbsolutePath().normalize();

		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception ex) {
			// throw new FileStorageException("Unable to create the directory where the
			// uploaded files will be stored.", ex);
		}
	}

	public String storeFile(MultipartFile file, String systemGeneratedFileName) {
		try {
			Path targetLocation = this.fileStorageLocation.resolve(systemGeneratedFileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			return systemGeneratedFileName;
		} catch (IOException ex) {
			_logger.error("throwing error when we try to upload doc - " + ex);
		}
		return systemGeneratedFileName;
	}

	public Resource loadFileAsResource(String fileName) {
		try {
			Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				// throw new MentionedFileNotFoundException("File not found " + fileName);
			}
		} catch (MalformedURLException ex) {
			// throw new MentionedFileNotFoundException("File not found " + fileName, ex);
		}
		return null;
	}

}