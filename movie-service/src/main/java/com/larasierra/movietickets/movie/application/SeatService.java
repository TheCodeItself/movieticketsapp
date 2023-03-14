package com.larasierra.movietickets.movie.application;

import com.larasierra.movietickets.movie.domain.Seat;
import com.larasierra.movietickets.movie.external.jpa.SeatRepository;
import com.larasierra.movietickets.movie.model.screen.DefaultScreenResponse;
import com.larasierra.movietickets.movie.model.seat.*;
import com.larasierra.movietickets.movie.model.showtime.DefaultShowtimeResponse;
import com.larasierra.movietickets.shared.exception.AppBadRequestException;
import com.larasierra.movietickets.shared.exception.AppInternalErrorException;
import com.larasierra.movietickets.shared.exception.AppResourceLockedException;
import com.larasierra.movietickets.shared.util.AuthInfo;
import com.larasierra.movietickets.shared.util.PurchaseTokenUtil;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SeatService {
    private final SeatRepository seatRepository;
    private final ShowtimeService showtimeService;
    private final ScreenService screenService;
    private final AuthInfo authInfo;
    private final PurchaseTokenUtil purchaseTokenUtil;

    public SeatService(SeatRepository seatRepository, ShowtimeService showtimeService, ScreenService screenService, AuthInfo authInfo, PurchaseTokenUtil purchaseTokenUtil) {
        this.seatRepository = seatRepository;
        this.showtimeService = showtimeService;
        this.screenService = screenService;
        this.authInfo = authInfo;
        this.purchaseTokenUtil = purchaseTokenUtil;
    }

    @PreAuthorize("hasRole('internal')")
    @Transactional
    public void createSeats(String showtimeId) {
        DefaultShowtimeResponse showtime = showtimeService.findById(showtimeId)
                .orElseThrow(() -> new AppBadRequestException("invalid showtimeId param"));
        DefaultScreenResponse screen = screenService.findById(showtime.screenId())
                .orElseThrow(AppInternalErrorException::new);

        List<Seat> seats = generateSeats(showtimeId, screen.seatingCapacity());

        // create seats for the showtime
        seatRepository.saveAll(seats);
    }

    /**
     * Every seat id will start with the showtime, following by the seat number, separated by a "-" (0SRT4T4FR9L27-A14).
     * The seat number by default will be the row letter following by the seat number in that row (2nd row and 5th seat will be B5)
     * @param showtimeId the id of the showtime for which the seats will be generated
     * @param seatingCapacity the number of seats
     * @return a list of Seat
     */
    private List<Seat> generateSeats(String showtimeId, short seatingCapacity) {
        int maxSeatsPerRow = 20;
        // number of rows with the maxSeatsPerRow
        int fullRows = seatingCapacity / maxSeatsPerRow;
        // number of seats in the last row in the case that that row does not fit the maxSeatsPerRow
        int lastRowSeatCapacity = seatingCapacity % maxSeatsPerRow;

        List<Seat> seats = new ArrayList<>(seatingCapacity);

        // 65 represents the letter 'A' in ascii
        for (int row = 65; row < 65 + fullRows; row++) {
            String rowPrefix = showtimeId + "-" + (char)row;
            for (int seatNum = 1; seatNum <= maxSeatsPerRow; seatNum++) {
                Seat seat = Seat.builder()
                        .seatId(rowPrefix + seatNum)
                        .showtimeId(showtimeId)
                        .available(true)
                        .purchaseToken(null)
                        .orderId(null)
                        .createdAt(OffsetDateTime.now())
                        .build();

                seats.add(seat);
            }
        }

        // last row
        String lastRowPrefix = showtimeId + "-" + (char)(65 + fullRows);
        for (int seatNum = 1; seatNum <= lastRowSeatCapacity; seatNum++) {
            Seat seat = Seat.builder()
                    .seatId(lastRowPrefix + seatNum)
                    .showtimeId(showtimeId)
                    .available(true)
                    .purchaseToken(null)
                    .orderId(null)
                    .createdAt(OffsetDateTime.now())
                    .build();

            seats.add(seat);
        }

        return seats;
    }

    @PreAuthorize("hasRole('enduser')")
    @Transactional
    public void reserveForCart(String seatId, ReserveSeatForCartRequest request) {
        // validate that the user is the token's owner
        purchaseTokenUtil.validateToken(authInfo.userId(), request.purchaseToken());

        int count = seatRepository.reserveForCart(seatId, request.seatToken(), request.purchaseToken());
        if (count < 1) {
            throw new AppResourceLockedException();
        }
    }

    @PreAuthorize("hasRole('enduser')")
    public void removePurchaseToken(String seatId, RemovePurchaseTokenRequest request) {
        // validate that the user is the token's owner
        purchaseTokenUtil.validateToken(authInfo.userId(), request.purchaseToken());
        seatRepository.removePurchaseToken(seatId, request.purchaseToken());
    }

    @PreAuthorize("hasRole('enduser')")
    @Transactional
    public List<FullSeatResponse> reserveForOrder(ReserveSeatForOrderRequest request) {
        // validate that the user is the token's owner
        // is not required to validate the expiration, the user can reserve the seats if no other use has yet reserve the same seats
        purchaseTokenUtil.validateToken(authInfo.userId(), request.userToken());

        List<Seat> seats = seatRepository.reserveForCart(request.seats(), request.userToken());

        if (seats.size() != request.seats().size()) {
            throw new AppResourceLockedException();
        }

        return seats.stream()
                .map(this::toFullResponse)
                .toList();
    }

    /**
     * Find the seat information for the given seatNumber. The seat will include the purchase token if it has expired and is still available for be purchase.
     * @param seatId the id of the seat
     * @return the seat for the given id
     */
    @PreAuthorize("permitAll()")
    public Optional<PublicSeatResponse> findById(String seatId) {
        return seatRepository.findById(seatId)
                .map(seat -> toPublicResponse(seat, true));
    }

    /**
     * Find all the seats for a given showtimeId. Each seat will include the purchase token if it has expired and is still available for be purchase.
     *
     * @param showtimeId the id of the showtime to which the seats belong
     * @return a list of the seats found
     */
    @PreAuthorize("permitAll()")
    public FindAllByShowtimeResponse findAllByShowtimeId(String showtimeId) {
        var seats = seatRepository.findAllByShowtimeId(showtimeId).stream()
                .map(seat -> toPublicResponse(seat, false))
                .toList();
        return new FindAllByShowtimeResponse(
            showtimeId,
            seats
        );
    }

    private FullSeatResponse toFullResponse(Seat seat) {
        return new FullSeatResponse(
                seat.getSeatId(),
                seat.getShowtimeId(),
                seat.getAvailable(),
                seat.getPurchaseToken(),
                seat.getOrderId(),
                seat.getCreatedAt()
        );
    }

    private PublicSeatResponse toPublicResponse(Seat seat, boolean includeShowtimeId) {
        var isAvailable = isSeatAvailable(seat);
        String purchaseToken = isAvailable ? seat.getPurchaseToken()
                                           : null;
        return new PublicSeatResponse(
                seat.getSeatNumber(),
                includeShowtimeId ? seat.getShowtimeId() : null,
                isAvailable,
                purchaseToken
        );
    }

    /**
     * Determine if the seat is available based on the available, orderId and purchaseToken values.
     * If available is false or orderId is not null, this method returns false. If the purchaseToken is not null, this method returns true if the token is not expired
     * @param seat the seat to be evaluated
     * @return true only if the seat is available
     */
    private boolean isSeatAvailable(Seat seat) {
        if (!seat.getAvailable() || seat.getOrderId() != null) {
            return false;
        }

        if (seat.getPurchaseToken() == null) {
            return true;
        }

        // it available only if expiration date is before the current date
        return !purchaseTokenUtil.isTokenExpired(seat.getPurchaseToken());
    }
}
