package com.larasierra.movietickets.movie.controller;

import com.larasierra.movietickets.movie.application.ScreenService;
import com.larasierra.movietickets.movie.model.screen.CreateScreenRequest;
import com.larasierra.movietickets.movie.model.screen.DefaultScreenResponse;
import com.larasierra.movietickets.movie.model.screen.UpdateScreenRequest;
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
public class ScreenController {

    private final ScreenService screenService;

    public ScreenController(ScreenService screenService) {
        this.screenService = screenService;
    }

    @PostMapping("/screen")
    public ResponseEntity<DefaultScreenResponse> create(@Valid @RequestBody CreateScreenRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(screenService.create(request));
    }

    @PostMapping("/screen/{id}")
    public DefaultScreenResponse update(
            @ValidId @PathVariable("id") String screenId,
            @Valid @RequestBody UpdateScreenRequest request
    ) {
        return screenService.update(screenId, request);
    }

    @DeleteMapping("/screen/{id}")
    public ResponseEntity<Void> delete(@ValidId @PathVariable("id") String screenId) {
        screenService.delete(screenId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/screen/{id}")
    public DefaultScreenResponse findById(@ValidId @PathVariable("id") String screenId) {
        return screenService.findById(screenId)
                .orElseThrow(AppResourceNotFoundException::new);
    }

    @GetMapping("/screen")
    public List<DefaultScreenResponse> findAllByTheater(@RequestParam("theater") String theater) {
        return screenService.findAllByTheater(theater);
    }
}
