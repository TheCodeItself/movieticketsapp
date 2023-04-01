package com.larasierra.movietickets.moviemedia.application;

import com.larasierra.movietickets.moviemedia.domain.MovieMedia;
import com.larasierra.movietickets.moviemedia.external.apiclient.MovieApiClient;
import com.larasierra.movietickets.moviemedia.external.jpa.MovieMediaRepository;
import com.larasierra.movietickets.moviemedia.external.objectstorage.MovieMediaS3Repository;
import com.larasierra.movietickets.moviemedia.model.CreateMovieMediaRequest;
import com.larasierra.movietickets.moviemedia.model.DefaultMovieMediaResponse;
import com.larasierra.movietickets.moviemedia.model.movie.MovieApiResponse;
import com.larasierra.movietickets.shared.exception.AppBadRequestException;
import com.larasierra.movietickets.shared.exception.AppInternalErrorException;
import com.larasierra.movietickets.shared.exception.AppResourceNotFoundException;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.awscore.exception.AwsServiceException;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieMediaServiceTest {

    @Mock
    private MovieMediaRepository movieMediaRepository;
    @Mock
    private MovieMediaS3Repository movieMediaS3Repository;
    @Mock
    private MovieApiClient movieApiClient;

    @InjectMocks
    private MovieMediaService movieMediaService;

    @Test
    void create() throws IOException {
        // given
        when(movieApiClient.findById(anyString())).thenReturn(movieApiResponse());
        when(movieMediaRepository.save(any(MovieMedia.class))).then(returnsFirstArg());
        doNothing().when(movieMediaS3Repository).upload(anyString(), anyLong(), any());
        doNothing().when(movieMediaRepository).markAvailable(anyString());

        var request = new CreateMovieMediaRequest("0111222333444", "1");

        var multipartFile = new MockMultipartFile("test.png", "test.png", "image/png", new byte[1]);

        // when
        DefaultMovieMediaResponse response = movieMediaService.create(request, multipartFile);

        // then
        assertNotNull(response, "response object is not null");
        assertNotNull(response.movieMediaId(), "movieMediaId is not null");
        assertEquals(request.movieId(), response.movieId(), "response's movieId is the same");
        assertEquals(request.mediaRole(), response.mediaRole(), "response's mediaRole is the same");
    }

    @Test
    void create_throwsWhenMovieNotFound() {
        // given
        when(movieApiClient.findById(anyString())).thenThrow(FeignException.NotFound.class);

        var request = new CreateMovieMediaRequest("0111222333444", "1");
        var multipartFile = new MockMultipartFile("test.png", "test.png", "image/png", new byte[1]);

        // when
        Executable exec = () -> movieMediaService.create(request, multipartFile);

        // then
        AppBadRequestException exception = assertThrows(AppBadRequestException.class, exec);
        assertTrue(exception.getPublicMessage().contains("invalid movie id param"));
    }

    @Test
    void create_throwsOnInvalidFileExtension() {
        // given
        when(movieApiClient.findById(anyString())).thenReturn(movieApiResponse());

        var request = new CreateMovieMediaRequest("0111222333444", "1");

        var invalidFileExtension = "test.invalid";
        var multipartFile = new MockMultipartFile("test.png", invalidFileExtension, "image/png", new byte[1]);

        // when
        Executable exec = () -> movieMediaService.create(request, multipartFile);

        // then
        AppBadRequestException exception = assertThrows(AppBadRequestException.class, exec);
        assertTrue(exception.getPublicMessage().contains("invalid file extension"));
    }

    @Test
    void create_throwsOnInvalidMediaRole() {
        // given
        when(movieApiClient.findById(anyString())).thenReturn(movieApiResponse());
        var invalidMediaRole = "invalid";
        var request = new CreateMovieMediaRequest("0111222333444", invalidMediaRole);

        var multipartFile = new MockMultipartFile("test.png", "test.png", "image/png", new byte[1]);

        // when
        Executable exec = () -> movieMediaService.create(request, multipartFile);

        // then
        AppBadRequestException exception = assertThrows(AppBadRequestException.class, exec);
        assertTrue(exception.getPublicMessage().contains("invalid media role"));
    }

    @Test
    void create_throwsOnFailedUpload() throws IOException {
        // given
        when(movieApiClient.findById(anyString())).thenReturn(movieApiResponse());
        when(movieMediaRepository.save(isA(MovieMedia.class))).then(returnsFirstArg());
        doThrow(AwsServiceException.class).when(movieMediaS3Repository).upload(anyString(), anyLong(), any());

        var request = new CreateMovieMediaRequest("0111222333444", "1");
        var multipartFile = new MockMultipartFile("test.png", "test.png", "image/png", new byte[1]);

        // when
        Executable exec = () -> movieMediaService.create(request, multipartFile);

        // then
        assertThrows(AppInternalErrorException.class, exec);
        verify(movieMediaRepository).deleteByMovieMediaId(anyString());
    }

    private MovieApiResponse movieApiResponse() {
        return new MovieApiResponse(
            "0111222333444",
            "title-test",
            "country",
            "genre",
            120,
            "rating",
            "synopsis",
            OffsetDateTime.now(),
            OffsetDateTime.now()
        );
    }

    @Test
    void delete() {
        // given
        var movieMediaId = "0111222333444";
        var url = movieMediaId + ".png";
        var movieMedia = new MovieMedia();
        movieMedia.setMovieMediaId(movieMediaId);
        movieMedia.setUrl(url);

        when(movieMediaRepository.findById(movieMediaId)).thenReturn(Optional.of(movieMedia));

        // when
        movieMediaService.delete(movieMediaId);

        // then
        verify(movieMediaRepository).deleteByMovieMediaId(movieMediaId);
        verify(movieMediaS3Repository).delete(url);
    }

    @Test
    void delete_throwsWhenNotFound() {
        // given
        when(movieMediaRepository.findById(anyString())).thenReturn(Optional.empty());

        // when
        Executable exec = () -> movieMediaService.delete("0111222333444");

        // then
        assertThrows(AppResourceNotFoundException.class, exec);
    }
}
