package com.larasierra.movietickets.movie.controller;

import com.larasierra.movietickets.movie.application.TheaterService;
import com.larasierra.movietickets.movie.model.theater.CreateTheaterRequest;
import com.larasierra.movietickets.movie.model.theater.DefaultTheaterResponse;
import com.larasierra.movietickets.movie.model.theater.UpdateTheaterRequest;
import com.larasierra.movietickets.shared.exception.AppResourceNotFoundException;
import com.larasierra.movietickets.shared.validation.ValidId;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
public class TheaterController {

    private final TheaterService theaterService;

    public TheaterController(TheaterService theaterService) {
        this.theaterService = theaterService;
    }

    @PostMapping("/theater")
    public ResponseEntity<DefaultTheaterResponse> create(@Valid @RequestBody CreateTheaterRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(theaterService.create(request));
    }

    @PostMapping("/theater/{id}")
    public DefaultTheaterResponse update(
            @ValidId @PathVariable("id") String theaterId,
            @Valid @RequestBody UpdateTheaterRequest request
    ) {
        return theaterService.update(theaterId, request);
    }

    @GetMapping("/theater/{id}")
    public DefaultTheaterResponse findById(@ValidId @PathVariable("id") String theaterId) {
        return theaterService.findById(theaterId)
                .orElseThrow(AppResourceNotFoundException::new);
    }

    @GetMapping("/theater")
    public List<DefaultTheaterResponse> findAllByCity(@RequestParam("city") String city) {
        return theaterService.findAllByCity(city);
    }
}
