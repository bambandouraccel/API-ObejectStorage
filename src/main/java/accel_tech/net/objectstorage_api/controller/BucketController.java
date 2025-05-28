package accel_tech.net.objectstorage_api.controller;

import accel_tech.net.objectstorage_api.dto.*;
import accel_tech.net.objectstorage_api.entity.Platform;
import accel_tech.net.objectstorage_api.service.PlatformParam;
import accel_tech.net.objectstorage_api.serviceImpl.OpenShiftIntegrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class BucketController {

    private final OpenShiftIntegrationService openShiftService;

    @GetMapping("/getObjects")
    public ResponseEntity<?> getObjects(
            @RequestParam String bucket,
            @RequestParam String projectId,
            @PlatformParam Platform platform) {

        return ResponseEntity.ok(new ApiResponse<>(
                true,
                openShiftService.getObjects(bucket, platform.get_id(), projectId)));
    }

    @PostMapping(path = "/createBucket")
    public ResponseEntity<?> createBucket(
            @RequestBody BucketCreationDto request) {

        return ResponseEntity.ok(new ApiResponse<>(
                true, openShiftService.createBucket(request)));
    }

    @GetMapping("/getKeys")
    public ResponseEntity<ApiResponse<BucketKeys>> getKeys(
            @RequestParam String bucket,
            @RequestParam String platformId,
            @RequestParam String projectId) {

        BucketKeys response = openShiftService.getKeys(bucket, platformId, projectId);
        return ResponseEntity.ok(new ApiResponse<>(true, response));
    }

    @GetMapping("/getUsage")
    public ResponseEntity<ApiResponse<BucketUsageResponse>> getUsage(
            @RequestParam String bucket,
            @RequestParam String platformId,
            @RequestParam String projectId) {

        BucketUsageResponse response = openShiftService.getUsage(bucket, platformId, projectId);
        return ResponseEntity.ok(new ApiResponse<>(true, response));
    }

    @DeleteMapping("/deleteBucket")
    public ResponseEntity<ApiResponse<ObjectOperationRequest>> deleteBucket(@RequestBody ObjectOperationRequest request) {

        /*String response = openShiftService.deleteBucket(
                request.getBucketName(),
                request.getPlatformId(),
                request.getProjectId());*/
        return ResponseEntity.ok(new ApiResponse<>(true, openShiftService.deleteBucket(request)));
    }

    @GetMapping("/getUploadSignedUrl")
    public ResponseEntity<ApiResponse<SignedUrlResponse>> getObjectSignedUrlUP(
            @RequestParam String bucket,
            @RequestParam String key,
            @RequestParam String platformId,
            @RequestParam String projectId) {

        SignedUrlResponse response = openShiftService.getUploadSignedUrl(
                bucket, key, platformId, projectId);
        return ResponseEntity.ok(new ApiResponse<>(true, response));
    }

    @GetMapping("/getObjectSignedUrl")
    public ResponseEntity<ApiResponse<SignedUrlResponse>> getObjectSignedUrl(
            @RequestParam String bucket,
            @RequestParam String key,
            @RequestParam String platformId,
            @RequestParam String projectId) {

        SignedUrlResponse response = openShiftService.getObjectSignedUrl(
                bucket, key, platformId, projectId);
        return ResponseEntity.ok(new ApiResponse<>(true, response));
    }

    @DeleteMapping("/deleteObject")
    public ResponseEntity<ApiResponse<ObjectOperationRequest>> deleteObject(@RequestBody ObjectOperationRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, openShiftService.deleteObject(request)));
    }
}