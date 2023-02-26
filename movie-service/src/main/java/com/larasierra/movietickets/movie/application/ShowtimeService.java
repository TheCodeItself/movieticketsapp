package com.larasierra.movietickets.movie.application;

import com.larasierra.movietickets.movie.domain.Showtime;
import com.larasierra.movietickets.movie.external.jpa.ShowtimeRepository;
import com.larasierra.movietickets.movie.model.screen.DefaultScreenResponse;
import com.larasierra.movietickets.movie.model.showtime.CreateShowtimeRequest;
import com.larasierra.movietickets.movie.model.showtime.DefaultShowtimeResponse;
import com.larasierra.movietickets.movie.model.showtime.UpdateShowtimeRequest;
import com.larasierra.movietickets.shared.exception.AppBadRequestException;
import com.larasierra.movietickets.shared.exception.AppResourceNotFoundException;
import com.larasierra.movietickets.shared.util.IdUtil;
import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ShowtimeService {
    private final ShowtimeRepository showtimeRepository;
    private final MovieService movieService;
    private final ScreenService screenService;

    public ShowtimeService(ShowtimeRepository showtimeRepository, MovieService movieService, ScreenService screenService) {
        this.showtimeRepository = showtimeRepository;
        this.movieService = movieService;
        this.screenService = screenService;
    }

    @PreAuthorize("hasRole('internal')")
    public DefaultShowtimeResponse create(CreateShowtimeRequest request) {
        movieService.findById(request.movieId())
                .orElseThrow(() -> new AppBadRequestException("invalid movieId param"));
        DefaultScreenResponse screen = screenService.findById(request.screenId())
                .orElseThrow(() -> new AppBadRequestException("invalid screenId param"));

        if (!Objects.equals(screen.theaterId(), request.theaterId())) {
            throw new AppBadRequestException("invalid theaterId param");
        }

        // TODO: 19/02/2023 validate potential conflict based on movie runtime and existing showtimes

        var showtime = new Showtime(
            IdUtil.next(),
            request.screenId(),
            request.theaterId(),
            request.movieId(),
            request.startDate(),
            request.standardPrice(),
            request.preferentialPrice(),
            OffsetDateTime.now()
        );

        showtime = showtimeRepository.save(showtime);

        return toDefaultResponse(showtime);
    }

    @PreAuthorize("hasRole('internal')")
    @Transactional
    public DefaultShowtimeResponse update(String showtimeId, UpdateShowtimeRequest request) {
        // TODO: 19/02/2023 it is only possible to update if it has not sold any ticket fot the given showtime
        // TODO: 19/02/2023 validate potential conflict based on the new startDate

        return showtimeRepository.findById(showtimeId)
                .map(showtime -> {
                    showtime.setScreenId(request.screenId());
                    showtime.setStartDate(request.startDate());
                    showtime.setStandardPrice(request.standardPrice());
                    showtime.setPreferentialPrice(request.preferentialPrice());
                    return showtime;
                })
                .map(this::toDefaultResponse)
                .orElseThrow(AppResourceNotFoundException::new);
    }

    @PreAuthorize("hasRole('internal')")
    public void delete(String showtimeId) {
        // TODO: 19/02/2023 it is only possible to update if it has not sold any ticket fot the given showtime
        // TODO: 19/02/2023 it must delete also related entities (seats)
        showtimeRepository.deleteById(showtimeId);
    }

    @PreAuthorize("permitAll()")
    public Optional<DefaultShowtimeResponse> findById(String showtimeId) {
        return showtimeRepository.findById(showtimeId)
                .map(this::toDefaultResponse);
    }

    @PreAuthorize("permitAll()")
    public List<DefaultShowtimeResponse> findAllByTheaterIdAndMovieIdAndDate(String theaterId, @Nullable String movieId, @Nullable OffsetDateTime startDate) {
        // TODO: 19/02/2023 this implementation must consider the user's preferred time-zone
        if (startDate == null) {
            startDate = OffsetDateTime.now();
        }
        var beginningOfTheDay = startDate.truncatedTo(ChronoUnit.DAYS);
        var endDate = startDate.plusDays(1L);

        return showtimeRepository.findAllByTheaterIdAndStartDate(theaterId, movieId, beginningOfTheDay, endDate).stream()
                .map(this::toDefaultResponse)
                .toList();
    }

    private DefaultShowtimeResponse toDefaultResponse(Showtime showtime) {
        return new DefaultShowtimeResponse(
                showtime.getShowtimeId(),
                showtime.getScreenId(),
                showtime.getTheaterId(),
                showtime.getMovieId(),
                showtime.getStartDate(),
                showtime.getStandardPrice(),
                showtime.getPreferentialPrice(),
                showtime.getCreatedAt()
        );
    }
}
