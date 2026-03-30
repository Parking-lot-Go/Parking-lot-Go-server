package car.demo.domain.auth.repository;

import car.demo.domain.auth.entity.Token;
import car.demo.domain.auth.entity.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByUserIdAndType(Long userId, TokenType type);
    List<Token> findAllByUserId(Long userId);
    Optional<Token> findByToken(String token);
    void deleteByToken(String token);
}
