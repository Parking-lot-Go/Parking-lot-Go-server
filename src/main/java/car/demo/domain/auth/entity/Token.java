package car.demo.domain.auth.entity;

import car.demo.domain.user.dto.UserDto;
import car.demo.domain.user.entity.User;
import car.demo.global.utils.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "tokens")
public class Token extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String token;

    private LocalDateTime expiredAt;

    @Enumerated(EnumType.STRING)
    private TokenType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private boolean isDeleted;

    public static Token of(String token, LocalDateTime expiredAt, User user) {
        return Token.builder()
                .token(token)
                .expiredAt(expiredAt)
                .user(user)
                .type(TokenType.REFRESH_TOKEN)
                .isDeleted(false)
                .build();
    }

    public static Token of(String token, LocalDateTime expiredAt, UserDto userDto) {
        User user = User.builder().id(userDto.getUserId()).build();
        return of(token, expiredAt, user);
    }

    public void markDeleted() {
        this.isDeleted = true;
    }
}
