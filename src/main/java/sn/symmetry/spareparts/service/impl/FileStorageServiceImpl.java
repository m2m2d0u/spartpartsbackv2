package sn.symmetry.spareparts.service.impl;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sn.symmetry.spareparts.config.MinioProperties;
import sn.symmetry.spareparts.service.FileStorageService;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        try {
            // Ensure bucket exists
            ensureBucketExists();

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String filename = UUID.randomUUID() + extension;
            String objectName = folder + "/" + filename;

            // Upload file
            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(
                    PutObjectArgs.builder()
                        .bucket(minioProperties.getBucket())
                        .object(objectName)
                        .stream(inputStream, file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
                );
            }

            log.info("File uploaded successfully: {}", objectName);
            return getPublicUrl(objectName);

        } catch (Exception e) {
            log.error("Error uploading file to MinIO", e);
            throw new RuntimeException("Failed to upload file: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        try {
            // Extract object name from URL
            String objectName = extractObjectNameFromUrl(fileUrl);
            if (objectName == null) {
                log.warn("Could not extract object name from URL: {}", fileUrl);
                return;
            }

            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .object(objectName)
                    .build()
            );

            log.info("File deleted successfully: {}", objectName);

        } catch (Exception e) {
            log.error("Error deleting file from MinIO: {}", fileUrl, e);
            throw new RuntimeException("Failed to delete file: " + e.getMessage(), e);
        }
    }

    @Override
    public String getPublicUrl(String objectName) {
        return minioProperties.getPublicUrl() + "/" + minioProperties.getBucket() + "/" + objectName;
    }

    private void ensureBucketExists() {
        try {
            boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .build()
            );

            if (!exists) {
                minioClient.makeBucket(
                    MakeBucketArgs.builder()
                        .bucket(minioProperties.getBucket())
                        .build()
                );
                log.info("Bucket created: {}", minioProperties.getBucket());
            }
        } catch (Exception e) {
            log.error("Error checking/creating bucket", e);
            throw new RuntimeException("Failed to ensure bucket exists: " + e.getMessage(), e);
        }
    }

    @Override
    public String getFileAsBase64(String fileUrl) {
        try {
            byte[] fileBytes = getFileBytesFromUrl(fileUrl);
            return Base64.getEncoder().encodeToString(fileBytes);
        } catch (Exception e) {
            log.error("Error getting file as base64: {}", fileUrl, e);
            throw new RuntimeException("Failed to get file as base64: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] getFileBytes(String objectName) {
        try {
            try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(minioProperties.getBucket())
                    .object(objectName)
                    .build()
            )) {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] data = new byte[1024];
                int nRead;
                while ((nRead = stream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                return buffer.toByteArray();
            }
        } catch (Exception e) {
            log.error("Error getting file bytes: {}", objectName, e);
            throw new RuntimeException("Failed to get file bytes: " + e.getMessage(), e);
        }
    }

    private byte[] getFileBytesFromUrl(String fileUrl) {
        String objectName = extractObjectNameFromUrl(fileUrl);
        if (objectName == null) {
            throw new IllegalArgumentException("Invalid file URL: " + fileUrl);
        }
        return getFileBytes(objectName);
    }

    private String extractObjectNameFromUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }

        // URL format: http://localhost:9000/spareparts/folder/filename.ext
        String bucketPrefix = "/" + minioProperties.getBucket() + "/";
        int bucketIndex = fileUrl.indexOf(bucketPrefix);

        if (bucketIndex == -1) {
            return null;
        }

        return fileUrl.substring(bucketIndex + bucketPrefix.length());
    }
}
