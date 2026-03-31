package car.demo.domain.supportticket.service;

import car.demo.domain.auth.service.JwtPrincipal;
import car.demo.domain.supportticket.entity.SupportTicket;
import car.demo.domain.user.entity.Role;
import car.demo.global.exception.ErrorCode;
import car.demo.global.exception.custom.AuthenticationException;
import car.demo.global.exception.custom.AuthorizationException;
import org.springframework.stereotype.Component;

@Component
public class SupportTicketAccessChecker {

    public Long requireUserId(JwtPrincipal principal) {
        if (principal == null || principal.getId() == null) {
            throw new AuthenticationException(ErrorCode.AUTHENTICATION_REQUIRED);
        }
        return principal.getId();
    }

    public boolean isAdmin(JwtPrincipal principal) {
        return principal != null && Role.ROLE_ADMIN.name().equals(principal.getRoleAsString());
    }

    public void requireAdmin(JwtPrincipal principal) {
        requireUserId(principal);
        if (!isAdmin(principal)) {
            throw new AuthorizationException(ErrorCode.ACCESS_DENIED);
        }
    }

    public void requireOwner(SupportTicket ticket, Long userId) {
        if (!ticket.isOwnedBy(userId)) {
            throw new AuthorizationException(ErrorCode.ACCESS_DENIED);
        }
    }
}
