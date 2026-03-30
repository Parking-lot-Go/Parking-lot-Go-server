package car.demo.domain.auth.service;

public interface JwtPrincipal {
    Long getId();
    String getRoleAsString();
    default String getNickname() { return null; }
    default String getPlatform() { return null; }
}
