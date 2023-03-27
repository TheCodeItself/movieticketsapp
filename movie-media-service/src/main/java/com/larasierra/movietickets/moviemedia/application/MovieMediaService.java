package com.larasierra.movietickets.moviemedia.application;

import com.larasierra.movietickets.moviemedia.domain.MovieMedia;
import com.larasierra.movietickets.moviemedia.domain.MovieMediaRole;
import com.larasierra.movietickets.moviemedia.external.apiclient.MovieApiClient;
import com.larasierra.movietickets.moviemedia.external.jpa.MovieMediaRepository;
import com.larasierra.movietickets.moviemedia.external.objectstorage.MovieMediaS3Repository;
import com.larasierra.movietickets.moviemedia.model.CreateMovieMediaRequest;
import com.larasierra.movietickets.moviemedia.model.DefaultMovieMediaResponse;
import com.larasierra.movietickets.moviemedia.model.GetFileResponse;
import com.larasierra.movietickets.shared.exception.AppBadRequestException;
import com.larasierra.movietickets.shared.exception.AppInternalErrorException;
import com.larasierra.movietickets.shared.exception.AppResourceNotFoundException;
import com.larasierra.movietickets.shared.util.IdUtil;
import feign.FeignException;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class MovieMediaService {
    private final MovieMediaRepository movieMediaRepository;
    private final MovieMediaS3Repository movieMediaS3Repository;
    private final MovieApiClient movieApiClient;

    public MovieMediaService(MovieMediaRepository movieMediaRepository, MovieMediaS3Repository movieMediaS3Repository, MovieApiClient movieApiClient) {
        this.movieMediaRepository = movieMediaRepository;
        this.movieMediaS3Repository = movieMediaS3Repository;
        this.movieApiClient = movieApiClient;
    }

    @PreAuthorize("hasRole('internal')")
    public DefaultMovieMediaResponse create(CreateMovieMediaRequest request, MultipartFile multipartFile) throws IOException {
        // check the existence of the movie with the given id
        try {
            movieApiClient.findById(request.movieId());
        } catch (FeignException.NotFound ex) {
            throw new AppBadRequestException("invalid movie id param");
        }

        String filenameExtension = StringUtils.getFilenameExtension(multipartFile.getOriginalFilename());

        if (!isValidMediaFileExtension(filenameExtension)) {
            throw new AppBadRequestException("invalid file extension");
        }

        // create a new MovieMedia in DB, so that we confirm a new unique ID
        String id = IdUtil.next();
        String url = id + "." + filenameExtension;
        String filename = getValidFileName(multipartFile.getOriginalFilename(), url);

        MovieMediaRole eventMediaRole = Arrays.stream(MovieMediaRole.values())
                .filter(mr -> Objects.equals(mr.getCode(), request.mediaRole()))
                .findFirst()
                .orElseThrow(() -> new AppBadRequestException("invalid media role"));

        var media = new MovieMedia(
                id,
                request.movieId(),
                url,
                filename,
                multipartFile.getContentType(),
                multipartFile.getSize(),
                eventMediaRole,
                false,
                OffsetDateTime.now()
        );
        media = movieMediaRepository.save(media);

        // upload the file to S3. If it fails delete the DB record
        try {
            movieMediaS3Repository.upload(url, multipartFile.getSize(), multipartFile.getInputStream());
        } catch (AwsServiceException | SdkClientException ex) {
            movieMediaRepository.deleteByMovieMediaId(id);
            throw new AppInternalErrorException();
        }

        // when the file has been uploaded successfully, update the DB record as available
        movieMediaRepository.markAvailable(id);
        media.setAvailable(true);

        return toDefaultResponse(media);
    }

    @PreAuthorize("hasRole('internal')")
    @Transactional
    public void delete(String eventMediaId) {
        MovieMedia media = movieMediaRepository.findById(eventMediaId)
                .orElseThrow(AppResourceNotFoundException::new);

        movieMediaRepository.deleteByMovieMediaId(eventMediaId);

        movieMediaS3Repository.delete(media.getUrl());
    }

    @PreAuthorize("permitAll()")
    public GetFileResponse getFile(@NotNull String movieMediaUrl) {
        MovieMedia eventMedia = movieMediaRepository.findById(StringUtils.stripFilenameExtension(movieMediaUrl))
                .filter(MovieMedia::getAvailable)
                .filter(media -> Objects.equals(media.getUrl(), movieMediaUrl))
                .orElseThrow(AppResourceNotFoundException::new);

        ResponseInputStream<GetObjectResponse> is = movieMediaS3Repository.download(eventMedia.getUrl());
        return new GetFileResponse(is, eventMedia.getFilename(), eventMedia.getContentType(), eventMedia.getFileLength());
    }

    @PreAuthorize("permitAll()")
    public List<DefaultMovieMediaResponse> findAllByMovieId(String movieId) {
        return movieMediaRepository.findAllByMovieId(movieId).stream()
                .filter(MovieMedia::getAvailable)
                .map(this::toDefaultResponse)
                .toList();
    }

    private DefaultMovieMediaResponse toDefaultResponse(MovieMedia media) {
        return new DefaultMovieMediaResponse(
                media.getMovieMediaId(),
                media.getMovieId(),
                media.getUrl(),
                media.getFilename(),
                media.getContentType(),
                media.getFileLength(),
                media.getMediaRole().getCode(),
                media.getCreatedAt()
        );
    }

    private boolean isValidMediaFileExtension(String filenameExtension) {
        for (String permittedFileExtension : PERMITTED_FILE_EXTENSIONS) {
            if (permittedFileExtension.equalsIgnoreCase(filenameExtension)) {
                return true;
            }
        }
        return false;
    }

    private String getValidFileName(@Nullable String originalFilename, @NotNull String fallback) {
        String filename = StringUtils.getFilename(originalFilename);

        if (filename == null || filename.isBlank()) {
            return fallback;
        }

        return filename.trim();
    }

    private final String[] PERMITTED_FILE_EXTENSIONS = {"png", "jpg", "mp4"};
}
