package io.volunteerapp.volunteer_app.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.volunteerapp.volunteer_app.service.CloudinaryService;

@RestController
@RequestMapping("/api/v1/images")
public class TestUploadImage {
    private final CloudinaryService cloudinaryService;

    public TestUploadImage(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    /**
     * Upload một ảnh
     * POST /api/v1/images/upload
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadSingleImage(
            @RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        try {
            String imageUrl = cloudinaryService.uploadImage(file);
            response.put("success", true);
            response.put("message", "Upload ảnh thành công");
            response.put("imageUrl", imageUrl);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Lỗi khi upload ảnh: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Upload nhiều ảnh
     * POST /api/v1/images/upload-multiple
     */
    @PostMapping("/upload-multiple")
    public ResponseEntity<Map<String, Object>> uploadMultipleImages(
            @RequestParam("files") List<MultipartFile> files) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<String> imageUrls = cloudinaryService.uploadImages(files);
            response.put("success", true);
            response.put("message", "Upload " + imageUrls.size() + " ảnh thành công");
            response.put("imageUrls", imageUrls);
            response.put("count", imageUrls.size());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Lỗi khi upload ảnh: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Xóa một ảnh
     * DELETE /api/v1/images/delete
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteSingleImage(
            @RequestParam("imageUrl") String imageUrl) {
        Map<String, Object> response = new HashMap<>();
        try {
            cloudinaryService.deleteImage(imageUrl);
            response.put("success", true);
            response.put("message", "Xóa ảnh thành công");
            response.put("deletedUrl", imageUrl);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Lỗi khi xóa ảnh: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Xóa nhiều ảnh
     * DELETE /api/v1/images/delete-multiple
     */
    @DeleteMapping("/delete-multiple")
    public ResponseEntity<Map<String, Object>> deleteMultipleImages(
            @RequestParam("imageUrls") List<String> imageUrls) {
        Map<String, Object> response = new HashMap<>();
        try {
            cloudinaryService.deleteImages(imageUrls);
            response.put("success", true);
            response.put("message", "Xóa " + imageUrls.size() + " ảnh thành công");
            response.put("deletedUrls", imageUrls);
            response.put("count", imageUrls.size());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi xóa ảnh: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
