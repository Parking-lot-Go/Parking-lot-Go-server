package car.demo.domain.user.repository;

import car.demo.domain.user.entity.LoginType;
import car.demo.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findBySocialIdAndLoginType(String socialId, LoginType loginType);
}
