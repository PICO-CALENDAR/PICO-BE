package com.pico.server.service;

import com.pico.server.properties.S3Properties;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class S3Service {

    private static final String USER_IMAGE = "user-image";
    private static final String PHOTO = "photos";

    private final S3Presigner preSigner;
    private final S3Properties s3Properties;
    private final S3Client s3Client;
    public String getUserImage(String filename) {
        return getPreSignedUrl(USER_IMAGE, filename);
    }

    public String putUserImage(MultipartFile uploadFile, String fileName) {
        return putPreSignedUrl(uploadFile, USER_IMAGE, fileName);
    }

    public String putPhotoMultipartImage(MultipartFile uploadFile, String fileName)
        throws IOException {
        PutObjectRequest objectRequest =  putObjectRequest(uploadFile, PHOTO, fileName);
        s3Client.putObject(objectRequest, RequestBody.fromBytes(uploadFile.getBytes()));
        return s3Client.utilities().getUrl(builder -> builder.bucket(s3Properties.bucket()).key(String.join("/", USER_IMAGE, fileName))).toExternalForm();
    }

    public void deletePhotoMultipartImage(String fileName) {
        DeleteObjectRequest objectRequest = deleteObjectRequest(PHOTO, fileName);
        s3Client.deleteObject(objectRequest);
    }

    public String putUserMultipartImage(MultipartFile uploadFile, String fileName)
        throws IOException {
        PutObjectRequest objectRequest =  putObjectRequest(uploadFile, USER_IMAGE, fileName);
        s3Client.putObject(objectRequest, RequestBody.fromBytes(uploadFile.getBytes()));
        return s3Client.utilities().getUrl(builder -> builder.bucket(s3Properties.bucket()).key(String.join("/", USER_IMAGE, fileName))).toExternalForm();
    }

    public void deleteUserMultipartImage(String fileName) {
        DeleteObjectRequest objectRequest = deleteObjectRequest(USER_IMAGE, fileName);
        s3Client.deleteObject(objectRequest);
    }

    private PutObjectRequest putObjectRequest(MultipartFile uploadFile, String folder, String filename) {
        return PutObjectRequest.builder()
            .bucket(s3Properties.bucket())
            .key(String.join("/", folder, filename))
            .contentType(uploadFile.getContentType())
            .contentLength(uploadFile.getSize())
            .acl(ObjectCannedACL.PUBLIC_READ)
            .build();
    }

    private DeleteObjectRequest deleteObjectRequest(String folder, String filename) {
        return DeleteObjectRequest.builder()
            .bucket(s3Properties.bucket())
            .key(String.join("/", folder, filename))
            .build();
    }

    private String getPreSignedUrl(String folder, String filename) {
        if (filename == null || filename.isEmpty()) {
            return null;
        }
        String url = preSigner
            .presignGetObject(getObjectPresignRequest(folder, filename))
            .url()
            .toString();

        preSigner.close();
        return url;
    }


    private GetObjectPresignRequest getObjectPresignRequest(String folder, String filename) {
        return GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(1))
            .getObjectRequest(objectRequest ->
                objectRequest
                    .bucket(s3Properties.bucket())
                    .key(String.join("/", folder, filename)))
            .build();
    }

    private String putPreSignedUrl(MultipartFile uploadfile, String folder, String filename) {
        if (filename == null || filename.isEmpty()) {
            return null;
        }
        String url = preSigner
            .presignPutObject(putObjectPresignRequest(uploadfile, folder, filename))
            .url()
            .toString();

        preSigner.close();
        return url;
    }

    private PutObjectPresignRequest putObjectPresignRequest(MultipartFile uploadFile, String folder, String filename) {
        return PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(1))
            .putObjectRequest(objectRequest ->
                objectRequest
                    .bucket(s3Properties.bucket())
                    .key(String.join("/", folder, filename))
                    .contentType(uploadFile.getContentType())
                    .contentLength(uploadFile.getSize()))
            .build();
    }
}
