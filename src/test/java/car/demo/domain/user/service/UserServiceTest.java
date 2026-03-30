package car.demo.domain.user.service;

import car.demo.domain.auth.dto.KakaoUserResponse;
import car.demo.domain.user.dto.UserDto;
import car.demo.domain.user.entity.LoginType;
import car.demo.domain.user.entity.User;
import car.demo.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("카카오 프로필 정보가 있을 때 정상적으로 회원가입한다")
    void signUpWithKakaoProfile() {
        // given
        KakaoUserResponse kakaoUser = new KakaoUserResponse();
        ReflectionTestUtils.setField(kakaoUser, "id", 12345L);
        
        KakaoUserResponse.KakaoAccount account = new KakaoUserResponse.KakaoAccount();
        KakaoUserResponse.KakaoAccount.Profile profile = new KakaoUserResponse.KakaoAccount.Profile();
        ReflectionTestUtils.setField(profile, "nickname", "카카오닉네임");
        ReflectionTestUtils.setField(profile, "profileImageUrl", "http://image.com");
        ReflectionTestUtils.setField(account, "profile", profile);
        ReflectionTestUtils.setField(kakaoUser, "kakaoAccount", account);

        when(userRepository.findBySocialIdAndLoginType("12345", LoginType.KAKAO)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            ReflectionTestUtils.setField(user, "id", 1L);
            return user;
        });

        // when
        UserDto result = userService.signUpOrLogin(kakaoUser);

        // then
        assertThat(result.getNickname()).isEqualTo("카카오닉네임");
        assertThat(result.getProfileImageUrl()).isEqualTo("http://image.com");
        assertThat(result.getIsRegister()).isTrue();
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("카카오 프로필 정보가 없을 때 랜덤 닉네임으로 회원가입한다")
    void signUpWithRandomNicknameWhenProfileEmpty() {
        // given
        KakaoUserResponse kakaoUser = new KakaoUserResponse();
        ReflectionTestUtils.setField(kakaoUser, "id", 12345L);
        // kakaoAccount is null

        when(userRepository.findBySocialIdAndLoginType("12345", LoginType.KAKAO)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            ReflectionTestUtils.setField(user, "id", 1L);
            return user;
        });

        // when
        UserDto result = userService.signUpOrLogin(kakaoUser);

        // then
        assertThat(result.getNickname()).startsWith("User_");
        assertThat(result.getNickname().length()).isEqualTo(5 + 8 + 1 - 1); // User_ + 8 chars = 13
        assertThat(result.getNickname()).hasSize(13);
        assertThat(result.getProfileImageUrl()).isNull();
        assertThat(result.getIsRegister()).isTrue();
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("카카오 닉네임이 빈 문자열일 때 랜덤 닉네임으로 회원가입한다")
    void signUpWithRandomNicknameWhenNicknameIsBlank() {
        // given
        KakaoUserResponse kakaoUser = new KakaoUserResponse();
        ReflectionTestUtils.setField(kakaoUser, "id", 12345L);

        KakaoUserResponse.KakaoAccount account = new KakaoUserResponse.KakaoAccount();
        KakaoUserResponse.KakaoAccount.Profile profile = new KakaoUserResponse.KakaoAccount.Profile();
        ReflectionTestUtils.setField(profile, "nickname", ""); // Empty string
        ReflectionTestUtils.setField(account, "profile", profile);
        ReflectionTestUtils.setField(kakaoUser, "kakaoAccount", account);

        when(userRepository.findBySocialIdAndLoginType("12345", LoginType.KAKAO)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            ReflectionTestUtils.setField(user, "id", 1L);
            return user;
        });

        // when
        UserDto result = userService.signUpOrLogin(kakaoUser);

        // then
        assertThat(result.getNickname()).startsWith("User_");
        assertThat(result.getIsRegister()).isTrue();
    }
}
