package com.larasierra.movietickets.movie.application;

import com.larasierra.movietickets.movie.domain.Theater;
import com.larasierra.movietickets.movie.external.jpa.TheaterRepository;
import com.larasierra.movietickets.movie.model.theater.CreateTheaterRequest;
import com.larasierra.movietickets.movie.model.theater.DefaultTheaterResponse;
import com.larasierra.movietickets.movie.model.theater.UpdateTheaterRequest;
import com.larasierra.movietickets.shared.exception.AppResourceNotFoundException;
import com.larasierra.movietickets.shared.util.IdUtil;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TheaterService {
    private final TheaterRepository theaterRepository;

    public TheaterService(TheaterRepository theaterRepository) {
        this.theaterRepository = theaterRepository;
    }

    @PreAuthorize("hasRole('internal')")
    public DefaultTheaterResponse create(CreateTheaterRequest request) {
        var theater = new Theater(
                IdUtil.next(),
                request.theaterName(), 
                request.city(), 
                request.direction(), 
                OffsetDateTime.now()
        );

        theater = theaterRepository.save(theater);

        return toDefaultResponse(theater);
    }

    @PreAuthorize("hasRole('internal')")
    @Transactional
    public DefaultTheaterResponse update(String theaterId, UpdateTheaterRequest request) {
        return theaterRepository.findById(theaterId)
                .map(theater -> {
                    theater.setTheaterName(request.theaterName());
                    theater.setDirection(request.direction());
                    return theater;
                })
                .map(this::toDefaultResponse)
                .orElseThrow(AppResourceNotFoundException::new);
    }

    @PreAuthorize("permitAll()")
    public Optional<DefaultTheaterResponse> findById(String theaterId) {
        return theaterRepository.findById(theaterId)
                .map(this::toDefaultResponse);
    }

    @PreAuthorize("permitAll()")
    public List<DefaultTheaterResponse> findAllByCity(String city) {
        return theaterRepository.findAllByCity(city).stream()
                .map(this::toDefaultResponse)
                .toList();
    }

    private DefaultTheaterResponse toDefaultResponse(Theater theater) {
        return new DefaultTheaterResponse(
                theater.getTheaterId(),
                theater.getTheaterName(),
                theater.getCity(),
                theater.getDirection(),
                theater.getCreatedAt()
        );
    }
}
