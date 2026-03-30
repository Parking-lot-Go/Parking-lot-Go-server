package car.demo.domain.user.service;

import car.demo.domain.auth.dto.KakaoUserResponse;
import car.demo.domain.user.dto.UserDto;
import car.demo.domain.user.entity.LoginType;
import car.demo.domain.user.entity.Role;
import car.demo.domain.user.entity.User;
import car.demo.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserDto signUpOrLogin(KakaoUserResponse kakaoUser) {
        String socialId = String.valueOf(kakaoUser.getId());
        return userRepository.findBySocialIdAndLoginType(socialId, LoginType.KAKAO)
                .map(user -> UserDto.authUserDTO(user, false))
                .orElseGet(() -> {
                    String nickname = "User_" + UUID.randomUUID().toString().substring(0, 8);
                    String profileImageUrl = null;

                    if (kakaoUser.getKakaoAccount() != null && kakaoUser.getKakaoAccount().getProfile() != null) {
                        if (StringUtils.hasText(kakaoUser.getKakaoAccount().getProfile().getNickname())) {
                            nickname = kakaoUser.getKakaoAccount().getProfile().getNickname();
                        }
                        if (StringUtils.hasText(kakaoUser.getKakaoAccount().getProfile().getProfileImageUrl())) {
                            profileImageUrl = kakaoUser.getKakaoAccount().getProfile().getProfileImageUrl();
                        }
                    }

                    User newUser = User.builder()
                            .socialId(socialId)
                            .nickname(nickname)
                            .profileImageUrl(profileImageUrl)
                            .loginType(LoginType.KAKAO)
                            .role(Role.ROLE_USER)
                            .isActive(true)
                            .build();
                    return UserDto.authUserDTO(userRepository.save(newUser), true);
                });
    }

    public UserDto getById(Long userId) {
        return userRepository.findById(userId)
                .map(UserDto::toDto)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}
