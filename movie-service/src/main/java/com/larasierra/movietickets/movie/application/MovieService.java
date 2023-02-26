package com.larasierra.movietickets.movie.application;

import com.larasierra.movietickets.movie.domain.Movie;
import com.larasierra.movietickets.movie.external.jpa.MovieRepository;
import com.larasierra.movietickets.movie.model.movie.DefaultMovieRequest;
import com.larasierra.movietickets.movie.model.movie.DefaultMovieResponse;
import com.larasierra.movietickets.shared.exception.AppResourceNotFoundException;
import com.larasierra.movietickets.shared.util.IdUtil;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class MovieService {
    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @PreAuthorize("hasRole('internal')")
    public DefaultMovieResponse create(DefaultMovieRequest request) {
        var movie = new Movie(
                IdUtil.next(),
                request.title(),
                request.country(),
                request.genre(),
                request.runtime(),
                request.rating(),
                request.synopsis(),
                request.releaseDate(),
                OffsetDateTime.now()
        );

        movie = movieRepository.save(movie);
        return toDefaultResponse(movie);
    }

    @PreAuthorize("hasRole('internal')")
    @Transactional
    public DefaultMovieResponse update(String movieId, DefaultMovieRequest request) {
        // TODO: 19/02/2023 additional logic must be implemented to deal with runtime and release date updates

        return movieRepository.findById(movieId)
                .map(movie -> {
                    movie.setTitle(request.title());
                    movie.setCountry(request.country());
                    movie.setRating(request.rating());
                    movie.setGenre(request.genre());
                    movie.setRuntime(request.runtime());
                    movie.setSynopsis(request.synopsis());
                    movie.setReleaseDate(request.releaseDate());
                    return movie;
                })
                .map(this::toDefaultResponse)
                .orElseThrow(AppResourceNotFoundException::new);
    }

    @PreAuthorize("permitAll()")
    public Optional<DefaultMovieResponse> findById(String movieId) {
        return movieRepository.findById(movieId)
                .map(this::toDefaultResponse);
    }

    private DefaultMovieResponse toDefaultResponse(Movie movie) {
        return new DefaultMovieResponse(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getCountry(),
                movie.getGenre(),
                movie.getRuntime(),
                movie.getRating(),
                movie.getSynopsis(),
                movie.getReleaseDate(),
                movie.getCreatedAt()
        );
    }
}
