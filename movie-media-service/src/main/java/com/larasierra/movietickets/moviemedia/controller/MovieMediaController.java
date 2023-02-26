package com.larasierra.movietickets.moviemedia.controller;

import com.larasierra.movietickets.moviemedia.application.MovieMediaService;
import com.larasierra.movietickets.moviemedia.model.CreateMovieMediaRequest;
import com.larasierra.movietickets.moviemedia.model.DefaultMovieMediaResponse;
import com.larasierra.movietickets.moviemedia.model.GetFileResponse;
import com.larasierra.movietickets.shared.validation.ValidId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.List;

@Validated
@RestController
public class MovieMediaController {

    private final MovieMediaService movieMediaService;

    public MovieMediaController(MovieMediaService movieMediaService) {
        this.movieMediaService = movieMediaService;
    }

    @PostMapping("/media")
    public ResponseEntity<DefaultMovieMediaResponse> upload(
            @Valid @RequestPart("data") CreateMovieMediaRequest request,
            @NotNull @RequestPart("file") MultipartFile file) throws IOException
    {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(movieMediaService.create(request, file));
    }

    @DeleteMapping("/media/{id}")
    public ResponseEntity<Void> delete(@ValidId @PathVariable("id") String movieMediaId) {
        movieMediaService.delete(movieMediaId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/media")
    public List<DefaultMovieMediaResponse> findAllByMovieId(@ValidId @RequestParam("movieId") String movieId) {
        return movieMediaService.findAllByMovieId(movieId);
    }

    @GetMapping("/media/file/{filename}")
    public ResponseEntity<StreamingResponseBody> getFileByName(@PathVariable("filename") String fileName) {
        GetFileResponse fileResponse = movieMediaService.getFile(fileName);

        StreamingResponseBody responseBody = outputStream -> {
            try (InputStream file = fileResponse.file()) {
                byte[] buffer = new byte[4096];
                int read;
                while ((read = file.read(buffer, 0, 4096)) >= 0) {
                    outputStream.write(buffer, 0, read);
                    outputStream.flush();
                }
            }
        };

        return ResponseEntity.ok()
                .contentLength(fileResponse.fileLength())
                .contentType(MediaType.parseMediaType(fileResponse.contentType()))
                .cacheControl(CacheControl.maxAge(Duration.ofHours(1L)))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(responseBody);
    }

}
