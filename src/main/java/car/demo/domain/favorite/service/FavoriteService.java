package car.demo.domain.favorite.service;

import car.demo.domain.SeoulAPI.dto.CursorResponseDto;
import car.demo.domain.SeoulAPI.dto.ParkingLotResponseDto;
import car.demo.domain.SeoulAPI.entity.ParkingLot;
import car.demo.domain.SeoulAPI.repository.ParkingLotRepository;
import car.demo.domain.favorite.entity.FavoriteParkingLot;
import car.demo.domain.favorite.repository.FavoriteRepository;
import car.demo.domain.user.entity.User;
import car.demo.domain.user.repository.UserRepository;
import car.demo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ParkingLotRepository parkingLotRepository;

    @Transactional
    public boolean toggleFavorite(Long userId, Long parkingLotId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(ErrorCode.NOT_FOUND_ENTITY.getMessage()));
        
        ParkingLot parkingLot = parkingLotRepository.findById(parkingLotId)
                .orElseThrow(() -> new RuntimeException(ErrorCode.NOT_FOUND_ENTITY.getMessage()));

        Optional<FavoriteParkingLot> favoriteOpt = favoriteRepository.findByUserAndParkingLot(user, parkingLot);

        if (favoriteOpt.isPresent()) {
            favoriteRepository.delete(favoriteOpt.get());
            return false; // 즐겨찾기 해제됨
        } else {
            FavoriteParkingLot favorite = FavoriteParkingLot.builder()
                    .user(user)
                    .parkingLot(parkingLot)
                    .build();
            favoriteRepository.save(favorite);
            return true; // 즐겨찾기 등록됨
        }
    }

    public CursorResponseDto<ParkingLotResponseDto> getFavoriteParkingLots(Long userId, Long cursor, Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(ErrorCode.NOT_FOUND_ENTITY.getMessage()));

        int fetchSize = (size == null) ? 10 : size;

        List<FavoriteParkingLot> favorites = favoriteRepository.findAllByUserWithCursor(user, cursor, PageRequest.of(0, fetchSize + 1));

        boolean hasNext = favorites.size() > fetchSize;
        List<FavoriteParkingLot> content = hasNext ? favorites.subList(0, fetchSize) : favorites;

        Long nextCursor = null;
        if (hasNext && !content.isEmpty()) {
            nextCursor = content.get(content.size() - 1).getId();
        }

        List<ParkingLotResponseDto> responseDtos = content.stream()
                .map(f -> ParkingLotResponseDto.fromEntity(f.getParkingLot()))
                .toList();

        return CursorResponseDto.of(responseDtos, nextCursor, hasNext);
    }
}
