package com.larasierra.movietickets.movie.controller;

import com.larasierra.movietickets.movie.application.ShowtimeService;
import com.larasierra.movietickets.movie.model.showtime.CreateShowtimeRequest;
import com.larasierra.movietickets.movie.model.showtime.DefaultShowtimeResponse;
import com.larasierra.movietickets.movie.model.showtime.UpdateShowtimeRequest;
import com.larasierra.movietickets.shared.exception.AppResourceNotFoundException;
import com.larasierra.movietickets.shared.validation.ValidId;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@Validated
@RestController
public class ShowtimeController {

    private final ShowtimeService showtimeService;

    public ShowtimeController(ShowtimeService showtimeService) {
        this.showtimeService = showtimeService;
    }

    @PostMapping("/showtime")
    public ResponseEntity<DefaultShowtimeResponse> create(@Valid @RequestBody CreateShowtimeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(showtimeService.create(request));
    }

    @PostMapping("/showtime/{id}")
    public DefaultShowtimeResponse update(
            @ValidId @PathVariable("id") String showtimeId,
            @Valid @RequestBody UpdateShowtimeRequest request
    ) {
        return showtimeService.update(showtimeId, request);
    }

    @DeleteMapping("/showtime/{id}")
    public ResponseEntity<Void> delete(@ValidId @PathVariable("id") String showtimeId) {
        showtimeService.delete(showtimeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/showtime/{id}")
    public DefaultShowtimeResponse findById(@ValidId @PathVariable("id") String showtimeId) {
        return showtimeService.findById(showtimeId)
                .orElseThrow(AppResourceNotFoundException::new);
    }

    @GetMapping("/showtime")
    public List<DefaultShowtimeResponse> findByTheaterIdAndDate(
            @ValidId @RequestParam("theaterId") String theaterId,
            @ValidId @RequestParam(value = "movieId", required = false) String movieId,
            @RequestParam(required = false) OffsetDateTime startDate
    ) {
        return showtimeService.findAllByTheaterIdAndMovieIdAndDate(theaterId, movieId, startDate);
    }
}
