package com.larasierra.movietickets.movie.controller;

import com.larasierra.movietickets.movie.application.MovieService;
import com.larasierra.movietickets.movie.model.movie.DefaultMovieRequest;
import com.larasierra.movietickets.movie.model.movie.DefaultMovieResponse;
import com.larasierra.movietickets.shared.exception.AppResourceNotFoundException;
import com.larasierra.movietickets.shared.validation.ValidId;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostMapping("/movie")
    public ResponseEntity<DefaultMovieResponse> create(@Valid @RequestBody DefaultMovieRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(movieService.create(request));
    }

    @PostMapping("/movie/{id}")
    public DefaultMovieResponse update(
            @ValidId @PathVariable("id") String movieId,
            @Valid @RequestBody DefaultMovieRequest request
    ) {
        return movieService.update(movieId, request);
    }

    @GetMapping("/movie/{id}")
    public DefaultMovieResponse findById(@ValidId @PathVariable("id") String movieId) {
        return movieService.findById(movieId)
                .orElseThrow(AppResourceNotFoundException::new);
    }
}
