package car.demo.domain.user.entity;

import car.demo.global.utils.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "users")
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nickname;

    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginType loginType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private String socialId;

    private boolean isActive;

    public boolean isActive() {
        return isActive;
    }
}
