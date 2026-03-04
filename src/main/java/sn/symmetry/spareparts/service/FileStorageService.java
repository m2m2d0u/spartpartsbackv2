package sn.symmetry.spareparts.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    /**
     * Upload a file to MinIO storage
     * @param file The file to upload
     * @param folder The folder/path within the bucket
     * @return The URL of the uploaded file
     */
    String uploadFile(MultipartFile file, String folder);

    /**
     * Delete a file from MinIO storage
     * @param fileUrl The URL of the file to delete
     */
    void deleteFile(String fileUrl);

    /**
     * Get the public URL for a file
     * @param objectName The object name in MinIO
     * @return The public URL
     */
    String getPublicUrl(String objectName);

    /**
     * Get file as base64 encoded string
     * @param fileUrl The URL of the file
     * @return Base64 encoded file content
     */
    String getFileAsBase64(String fileUrl);

    /**
     * Get file bytes from MinIO
     * @param objectName The object name in MinIO
     * @return File bytes
     */
    byte[] getFileBytes(String objectName);
}
