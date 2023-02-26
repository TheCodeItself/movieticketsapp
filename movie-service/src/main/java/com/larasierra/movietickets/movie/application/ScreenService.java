package com.larasierra.movietickets.movie.application;

import com.larasierra.movietickets.movie.domain.Screen;
import com.larasierra.movietickets.movie.external.jpa.ScreenRepository;
import com.larasierra.movietickets.movie.model.screen.CreateScreenRequest;
import com.larasierra.movietickets.movie.model.screen.DefaultScreenResponse;
import com.larasierra.movietickets.movie.model.screen.UpdateScreenRequest;
import com.larasierra.movietickets.shared.exception.AppBadRequestException;
import com.larasierra.movietickets.shared.exception.AppResourceNotFoundException;
import com.larasierra.movietickets.shared.util.IdUtil;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ScreenService {
    private final ScreenRepository screenRepository;
    private final TheaterService theaterService;

    public ScreenService(ScreenRepository screenRepository, TheaterService theaterService) {
        this.screenRepository = screenRepository;
        this.theaterService = theaterService;
    }
    
    @PreAuthorize("hasRole('internal')")
    public DefaultScreenResponse create(CreateScreenRequest request) {
        // check for the existence of the theater
        theaterService.findById(request.theaterId())
                .orElseThrow(() -> new AppBadRequestException("invalid theaterId param"));
        
       var screen = new Screen(IdUtil.next(), request.theaterId(), request.screenName(), request.seatingCapacity(), OffsetDateTime.now());
       
       screen = screenRepository.save(screen);
       
       return toDefaultResponse(screen);
    }
    
    @PreAuthorize("hasRole('internal')")
    @Transactional
    public DefaultScreenResponse update(String screenId, UpdateScreenRequest request) {
        // TODO: 19/02/2023 if seatingCapacity changes, additional validation if current showtimes must be considered
        return screenRepository.findById(screenId)
                .map(screen -> {
                    screen.setScreenName(request.screenName());
                    screen.setSeatingCapacity(request.seatingCapacity());
                    return screen;
                })
                .map(this::toDefaultResponse)
                .orElseThrow(AppResourceNotFoundException::new);
    }

    @PreAuthorize("hasRole('internal')")
    public void delete(String screenId) {
        // TODO: 19/02/2023 validate showtimes
        screenRepository.deleteById(screenId);
    }

    @PreAuthorize("permitAll()")
    public Optional<DefaultScreenResponse> findById(String screenId) {
        return screenRepository.findById(screenId)
                .map(this::toDefaultResponse);
    }

    @PreAuthorize("permitAll()")
    public List<DefaultScreenResponse> findAllByTheater(String theaterId) {
        return screenRepository.findAllByTheaterId(theaterId).stream()
                .map(this::toDefaultResponse)
                .toList();
    }

    private DefaultScreenResponse toDefaultResponse(Screen screen) {
        return new DefaultScreenResponse(
                screen.getScreenId(),
                screen.getTheaterId(),
                screen.getScreenName(),
                screen.getSeatingCapacity(), 
                screen.getCreatedAt()
        );
    }
}
