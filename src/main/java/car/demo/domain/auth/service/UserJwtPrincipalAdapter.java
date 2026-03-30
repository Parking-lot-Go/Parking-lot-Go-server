package car.demo.domain.auth.service;

import car.demo.domain.user.dto.UserDto;
import lombok.RequiredArgsConstructor;

public class UserJwtPrincipalAdapter implements JwtPrincipal {
    private final UserDto userDto;

    public UserJwtPrincipalAdapter(UserDto userDto) {
        this.userDto = userDto;
    }

    @Override
    public Long getId() {
        return userDto.getUserId();
    }

    @Override
    public String getRoleAsString() {
        return userDto.getRole() != null ? userDto.getRole().name() : null;
    }

    @Override
    public String getNickname() {
        return userDto.getNickname();
    }

    public UserDto getUserDto() {
        return userDto;
    }
}
