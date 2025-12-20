package io.volunteerapp.volunteer_app.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @SuppressWarnings("unchecked")
    public String uploadImage(MultipartFile file) throws IOException {
        // Validate file type
        validateImageFile(file);

        Map<String, Object> uploadParams = ObjectUtils.asMap(
                "resource_type", "image",
                "folder", "healthy-store",
                "use_filename", true,
                "unique_filename", true,
                "overwrite", false);

        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
        return uploadResult.get("secure_url").toString();
    }

    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File không được để trống");
        }

        String contentType = file.getContentType();
        List<String> allowedTypes = Arrays.asList(
                "image/jpeg", "image/png", "image/gif", "image/webp", "image/bmp");

        if (contentType == null || !allowedTypes.contains(contentType)) {
            throw new IllegalArgumentException("Chỉ chấp nhận file ảnh (JPEG, PNG, GIF, WebP, BMP)");
        }

        // Giới hạn kích thước file (10MB)
        long maxSizeInBytes = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSizeInBytes) {
            throw new IllegalArgumentException("Kích thước file không được vượt quá 10MB");
        }
    }

    /**
     * Upload nhiều ảnh
     */
    public List<String> uploadImages(List<MultipartFile> files) throws IOException {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("Danh sách file không được để trống");
        }

        List<String> uploadedUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            String url = uploadImage(file);
            uploadedUrls.add(url);
        }
        return uploadedUrls;
    }

    /**
     * Xóa một ảnh từ Cloudinary
     * @param imageUrl URL của ảnh cần xóa (có thể là secure_url hoặc public_id)
     */
    @SuppressWarnings("unchecked")
    public void deleteImage(String imageUrl) throws IOException {
        if (imageUrl == null || imageUrl.isEmpty()) {
            throw new IllegalArgumentException("URL ảnh không được để trống");
        }

        // Extract public_id from URL
        String publicId = extractPublicIdFromUrl(imageUrl);
        
        Map<String, Object> deleteParams = ObjectUtils.asMap(
                "resource_type", "image",
                "invalidate", true);

        cloudinary.uploader().destroy(publicId, deleteParams);
    }

    /**
     * Xóa nhiều ảnh từ Cloudinary
     */
    public void deleteImages(List<String> imageUrls) throws IOException {
        if (imageUrls == null || imageUrls.isEmpty()) {
            throw new IllegalArgumentException("Danh sách URL ảnh không được để trống");
        }

        for (String imageUrl : imageUrls) {
            try {
                deleteImage(imageUrl);
            } catch (Exception e) {
                // Log error but continue deleting other images
                System.err.println("Lỗi khi xóa ảnh: " + imageUrl + " - " + e.getMessage());
            }
        }
    }

    /**
     * Extract public_id from Cloudinary URL
     * Example: https://res.cloudinary.com/cloud_name/image/upload/v1234567890/folder/image.jpg
     * Returns: folder/image
     */
    private String extractPublicIdFromUrl(String imageUrl) {
        try {
            // If it's already a public_id (no http/https), return as is
            if (!imageUrl.startsWith("http://") && !imageUrl.startsWith("https://")) {
                return imageUrl;
            }

            // Extract public_id from URL
            // Cloudinary URL format: https://res.cloudinary.com/{cloud_name}/{resource_type}/upload/{version}/{public_id}.{format}
            String[] parts = imageUrl.split("/upload/");
            if (parts.length < 2) {
                throw new IllegalArgumentException("URL không hợp lệ: " + imageUrl);
            }

            String afterUpload = parts[1];
            // Remove version if present (v1234567890/)
            if (afterUpload.startsWith("v") && afterUpload.indexOf("/") > 0) {
                afterUpload = afterUpload.substring(afterUpload.indexOf("/") + 1);
            }

            // Remove file extension
            int lastDotIndex = afterUpload.lastIndexOf(".");
            if (lastDotIndex > 0) {
                afterUpload = afterUpload.substring(0, lastDotIndex);
            }

            return afterUpload;
        } catch (Exception e) {
            throw new IllegalArgumentException("Không thể trích xuất public_id từ URL: " + imageUrl, e);
        }
    }
}
