package accel_tech.net.objectstorage_api.serviceImpl;

import accel_tech.net.objectstorage_api.dto.*;
import accel_tech.net.objectstorage_api.entity.Platform;
import accel_tech.net.objectstorage_api.exception.BadRequestException;
import accel_tech.net.objectstorage_api.exception.OpenShiftIntegrationException;
import accel_tech.net.objectstorage_api.exception.ResourceNotFoundException;
import accel_tech.net.objectstorage_api.repository.PlatformRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.*;

import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenShiftIntegrationService {

    private final RestTemplate restTemplate;
    private final PlatformRepository platformRepository;

    public List<BucketObjectDto> getObjects(String bucket, String platformId, String projectId) {
        Platform platform = getPlatform(platformId);

        HttpHeaders headers = createHeaders(platform);
        String projectName = convertProjectIdToProjectName(projectId);
        String url = platform.getApiUrl() + "/api/v1/namespaces/" + projectName + "/secrets/" + bucket;

        ResponseEntity<BucketKeysResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<BucketKeysResponse>() {}
                );
        BucketKeysResponse data = response.getBody();

        AwsCredentials credentials = AwsBasicCredentials.create(
                new String(Base64.getDecoder().decode( data.getAccessKey())),
                new String(Base64.getDecoder().decode( data.getSecretKey()))
        );

        S3Client s3Client = S3Client.builder()
                .endpointOverride(URI.create(platform.getGlobalEndpoint()))
                .region(Region.of(platform.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .forcePathStyle(true)
                .build();

        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                .bucket(bucket)
                .build();
        ListObjectsV2Response listObjectsV2Response = s3Client.listObjectsV2(listObjectsV2Request);
        List<S3Object> contents = listObjectsV2Response.contents();
        System.out.println("Number of objects in the bucket: " + contents.stream().count());
        s3Client.close();

        return getObjectsFromS3Response(contents);
    }

    public  BucketCreationResponse createBucket (BucketCreationDto bucketCreationDto) {
        Platform platform = findPlatform(bucketCreationDto.getRegion());

        String namespaceName = convertProjectIdToProjectName(bucketCreationDto.getProjectId());
        createNamespaceIfAbsent(namespaceName, platform);

        ObjectBucketClaim obc = new ObjectBucketClaim();
        String name = bucketCreationDto.getBucketName();

        Metadata obcMetadata = new Metadata();
        obcMetadata.setName(name);
        obcMetadata.setNamespace(namespaceName);
        obcMetadata.setLabels(Map.of(
                "pending-bind-alert", "true",
                "hub.heritage.africa/bucket", "true"
        ));

        obc.setMetadata(obcMetadata);

        Spec spec = new Spec();
        spec.setBucketName(name);
        spec.setStorageClassName(platform.getStorageClassName());
        obc.setSpec(spec);
        log.info("Creating bucket: " + obc);
        String url = platform.getApiUrl() + "/apis/objectbucket.io/v1alpha1/namespaces/" + namespaceName +"/objectbucketclaims";

        HttpHeaders headers = createHeaders(platform);
        ResponseEntity<OBCResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(obc, headers),
                new ParameterizedTypeReference<OBCResponse>() {}
        );
        HttpStatusCode statusCode = response.getStatusCode();
        if (statusCode != HttpStatus.CREATED) {
            throw  new BadRequestException("FAILED TO CREATE OBJECTBUCKETCLAIM");
        }
        BucketCreationResponse bucketCreationResponse = new BucketCreationResponse();
        bucketCreationResponse.setBucketId(response.getBody().getMetadata().getUid());
        bucketCreationResponse.setPlatformId(platform.get_id());
        bucketCreationResponse.setEndpoint(platform.getGlobalEndpoint());
        return bucketCreationResponse;
    }

    public void createNamespaceIfAbsent(String namespaceName, Platform platform) {
        Namespace ns = new Namespace();
        Metadata nsMetadata = new Metadata();
        nsMetadata.setName(namespaceName);
        ns.setMetadata(nsMetadata);

        String url = platform.getApiUrl() + "/api/v1/namespaces/";

        HttpHeaders headers = createHeaders(platform);
        try{
            ResponseEntity<?> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(ns, headers),
                    new ParameterizedTypeReference<ObjectUtils.Null>() {}
            );
           return;


        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                return;
            }
        }
        throw  new BadRequestException("FAILED TO CREATE NAMESPACE");

        // Create Rolebinding after
    }


    public BucketKeys getKeys(String bucket, String platformId, String projectId) {
        Platform platform = getPlatform(platformId);
        HttpHeaders headers = createHeaders(platform);
        String projectName = convertProjectIdToProjectName(projectId);
        String url = platform.getApiUrl() + "/api/v1/namespaces/" + projectName + "/secrets/" + bucket;
        ResponseEntity<BucketKeysResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<BucketKeysResponse>() {}
        );
        BucketKeysResponse data = response.getBody();
        BucketKeys bucketKeys = new BucketKeys();
        bucketKeys.setAccessKey(new String(Base64.getDecoder().decode( data.getAccessKey())));
        bucketKeys.setSecretKey(new String(Base64.getDecoder().decode( data.getSecretKey())));

        return bucketKeys;
    }

    public BucketUsageResponse getUsage(String bucket, String platformId, String projectId) {
        Platform platform = getPlatform(platformId);

        HttpHeaders headers = createHeaders(platform);
        String projectName = convertProjectIdToProjectName(projectId);
        String url = platform.getApiUrl() + "/api/v1/namespaces/" + projectName + "/secrets/" + bucket;

        ResponseEntity<BucketKeysResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<BucketKeysResponse>() {}
        );
        BucketKeysResponse data = response.getBody();

        AwsCredentials credentials = AwsBasicCredentials.create(
                new String(Base64.getDecoder().decode( data.getAccessKey())),
                new String(Base64.getDecoder().decode( data.getSecretKey()))
        );

        S3Client s3Client = S3Client.builder()
                .endpointOverride(URI.create(platform.getGlobalEndpoint()))
                .region(Region.of(platform.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .forcePathStyle(true)
                .build();

        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                .bucket(bucket)
                .build();

        ListObjectsV2Response listObjectsV2Response = s3Client.listObjectsV2(listObjectsV2Request);
        List<S3Object> contents = listObjectsV2Response.contents();

        s3Client.close();

        BucketUsageResponse bucketUsageResponse = new BucketUsageResponse();
        bucketUsageResponse.setBytesTotal(0);
        bucketUsageResponse.setObjectsCount(contents.stream().count());

        contents.stream().forEach(item -> {
            bucketUsageResponse.setBytesTotal(bucketUsageResponse.getBytesTotal() + item.size());
                    });
        return bucketUsageResponse;
    }

    public ObjectOperationRequest deleteBucket(ObjectOperationRequest request) {
        Platform platform = getPlatform(request.getPlatformId());
        String bucketName = request.getBucketName();

        HttpHeaders headers = createHeaders(platform);
        String projectName = convertProjectIdToProjectName(request.getProjectId());

        String url = platform.getApiUrl() + "/apis/objectbucket.io/v1alpha1/namespaces/" + projectName +"/objectbucketclaims/"+ bucketName;

        ResponseEntity<OBCResponse> response = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<OBCResponse>() {}
        );
        HttpStatusCode status = response.getStatusCode();

        if(status != HttpStatus.OK && status != HttpStatus.ACCEPTED && status != HttpStatus.NOT_FOUND) {
            throw new OpenShiftIntegrationException("Failed to delete bucket");
        }

        ObjectOperationRequest objectOperationRequest = new ObjectOperationRequest();
        objectOperationRequest.setBucketName(bucketName);
        objectOperationRequest.setBucketId(response.getBody().getMetadata().getUid());

        return  objectOperationRequest;
    }

    public SignedUrlResponse getObjectSignedUrl(String bucket, String key, String platformId, String projectId) {
        Platform platform = getPlatform(platformId);

        HttpHeaders headers = createHeaders(platform);
        String projectName = convertProjectIdToProjectName(projectId);
        String url = platform.getApiUrl() + "/api/v1/namespaces/" + projectName + "/secrets/" + bucket;

        ResponseEntity<BucketKeysResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<BucketKeysResponse>() {}
        );
        BucketKeysResponse data = response.getBody();

        AwsCredentials credentials = AwsBasicCredentials.create(
                new String(Base64.getDecoder().decode( data.getAccessKey())),
                new String(Base64.getDecoder().decode( data.getSecretKey()))
        );


        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();


        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .getObjectRequest(getObjectRequest)
                .build();


        S3Presigner s3Presigner = S3Presigner.builder()
                .endpointOverride(URI.create(platform.getGlobalEndpoint()))
                .region(Region.of(platform.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);


        SignedUrlResponse signedUrlResponse = new SignedUrlResponse();
        signedUrlResponse.setSignedUrl(presignedRequest.url().toString());

        s3Presigner.close();
        return signedUrlResponse;
    }

    public SignedUrlResponse getUploadSignedUrl(String bucket, String key, String platformId, String projectId) {
        Platform platform = getPlatform(platformId);

        HttpHeaders headers = createHeaders(platform);
        String projectName = convertProjectIdToProjectName(projectId);
        String url = platform.getApiUrl() + "/api/v1/namespaces/" + projectName + "/secrets/" + bucket;

        ResponseEntity<BucketKeysResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<BucketKeysResponse>() {}
        );
        BucketKeysResponse data = response.getBody();

        AwsCredentials credentials = AwsBasicCredentials.create(
                new String(Base64.getDecoder().decode( data.getAccessKey())),
                new String(Base64.getDecoder().decode( data.getSecretKey()))
        );


        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();


        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .putObjectRequest(putObjectRequest)
                .build();


        S3Presigner s3Presigner = S3Presigner.builder()
                .endpointOverride(URI.create(platform.getGlobalEndpoint()))
                .region(Region.of(platform.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);


        SignedUrlResponse signedUrlResponse = new SignedUrlResponse();
        signedUrlResponse.setSignedUrl(presignedRequest.url().toString());

        s3Presigner.close();

        return signedUrlResponse;
    }

    public ObjectOperationRequest deleteObject(ObjectOperationRequest request) {
        Platform platform = getPlatform(request.getPlatformId());
        String bucket = request.getBucketName();
        String key = request.getKey();
        HttpHeaders headers = createHeaders(platform);
        String projectName = convertProjectIdToProjectName(request.getProjectId());
        String url = platform.getApiUrl() + "/api/v1/namespaces/" + projectName + "/secrets/" + bucket;

        ResponseEntity<BucketKeysResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<BucketKeysResponse>() {}
        );
        BucketKeysResponse data = response.getBody();

        AwsCredentials credentials = AwsBasicCredentials.create(
                new String(Base64.getDecoder().decode( data.getAccessKey())),
                new String(Base64.getDecoder().decode( data.getSecretKey()))
        );

        S3Client s3Client = S3Client.builder()
                .endpointOverride(URI.create(platform.getGlobalEndpoint()))
                .region(Region.of(platform.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .forcePathStyle(true)
                .build();

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        DeleteObjectResponse deleteObjectResponse = s3Client.deleteObject(deleteObjectRequest);
        //log.info(deleteObjectResponse.toString());

        ObjectOperationRequest objectOperationRequest = new ObjectOperationRequest();
        objectOperationRequest.setBucketName(bucket);
        objectOperationRequest.setObjectName(key);


        s3Client.close();

        return objectOperationRequest;
    }

    private Platform getPlatform(String platformId) {
        return platformRepository.findById(platformId)
                .orElseThrow(() -> new ResourceNotFoundException("Platform not found with id: " + platformId));
    }

    private Platform findPlatform(String region) {
        Platform platform = platformRepository.findPlatformByRegion(region);
        if (platform == null) {
            throw new ResourceNotFoundException("Platform not found with region: " + region);
        }
        return platform;
    }

    private HttpHeaders createHeaders(Platform platform) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + platform.getApiToken());
        return headers;
    }

    public List<BucketObjectDto> getObjectsFromS3Response(List<S3Object> s3Objects) {
        List<BucketObjectDto> objects = new ArrayList<>();
        s3Objects.forEach(object -> {
            BucketObjectDto convertedObject = new BucketObjectDto();
            convertedObject.setName(object.key());
            convertedObject.setSize(object.size());
            convertedObject.setDateModified(object.lastModified());
            objects.add(convertedObject);
        });
        return objects;
    }

    private String convertProjectIdToProjectName(String projectId) {
        return "osn-" + projectId;
    }

}