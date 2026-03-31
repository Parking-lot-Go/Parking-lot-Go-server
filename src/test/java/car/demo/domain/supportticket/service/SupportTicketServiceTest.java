package car.demo.domain.supportticket.service;

import car.demo.domain.auth.service.JwtPrincipal;
import car.demo.domain.supportticket.dto.SupportTicketRequest;
import car.demo.domain.supportticket.dto.SupportTicketResponse;
import car.demo.domain.supportticket.entity.SupportTicket;
import car.demo.domain.supportticket.entity.SupportTicketCategory;
import car.demo.domain.supportticket.entity.SupportTicketStatus;
import car.demo.domain.supportticket.entity.SupportTicketType;
import car.demo.domain.supportticket.repository.SupportTicketRepository;
import car.demo.domain.user.entity.LoginType;
import car.demo.domain.user.entity.NaviType;
import car.demo.domain.user.entity.Role;
import car.demo.domain.user.entity.User;
import car.demo.domain.user.repository.UserRepository;
import car.demo.global.exception.custom.AuthorizationException;
import car.demo.global.exception.custom.BusinessException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupportTicketServiceTest {

    @Mock
    private SupportTicketRepository supportTicketRepository;

    @Mock
    private UserRepository userRepository;

    private SupportTicketService supportTicketService;

    @BeforeEach
    void setUp() {
        supportTicketService = new SupportTicketServiceImpl(
                supportTicketRepository,
                userRepository,
                new SupportTicketAccessChecker()
        );
    }

    @Test
    @DisplayName("support ticket create success")
    void createTicket() {
        // given
        JwtPrincipal principal = principal(10L, Role.ROLE_USER);
        SupportTicketRequest.Create request = new SupportTicketRequest.Create(
                SupportTicketType.INQUIRY,
                SupportTicketCategory.INQUIRY_BUG_REPORT,
                "Favorite button is broken",
                "Favorite state disappears after refresh.",
                "Only happens after relaunch.",
                "reply@example.com",
                "1.2.0",
                "Android 14",
                "Pixel 8"
        );
        User user = user(10L, Role.ROLE_USER);

        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(supportTicketRepository.save(any(SupportTicket.class))).thenAnswer(invocation -> {
            SupportTicket ticket = invocation.getArgument(0);
            ReflectionTestUtils.setField(ticket, "id", 1L);
            return ticket;
        });

        // when
        SupportTicketResponse.Detail result = supportTicketService.createTicket(principal, request);

        // then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.userId()).isEqualTo(10L);
        assertThat(result.ticketType()).isEqualTo(SupportTicketType.INQUIRY);
        assertThat(result.category()).isEqualTo(SupportTicketCategory.INQUIRY_BUG_REPORT);
        assertThat(result.status()).isEqualTo(SupportTicketStatus.PENDING);
        verify(supportTicketRepository).save(any(SupportTicket.class));
    }

    @Test
    @DisplayName("support ticket create fails when category does not match type")
    void createTicketFailsWhenCategoryMismatch() {
        // given
        JwtPrincipal principal = principal(10L, Role.ROLE_USER);
        SupportTicketRequest.Create request = new SupportTicketRequest.Create(
                SupportTicketType.INQUIRY,
                SupportTicketCategory.FEATURE_PERFORMANCE,
                "Mismatch",
                "Invalid category",
                "context",
                "reply@example.com",
                "1.2.0",
                "Android 14",
                "Pixel 8"
        );

        // when
        // then
        assertThatThrownBy(() -> supportTicketService.createTicket(principal, request))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("support ticket detail denies access for other user")
    void getTicketDeniedForOtherUser() {
        // given
        JwtPrincipal principal = principal(10L, Role.ROLE_USER);
        SupportTicket ticket = ticket(99L, 30L, SupportTicketStatus.PENDING);

        when(supportTicketRepository.findActiveById(99L)).thenReturn(Optional.of(ticket));

        // when
        // then
        assertThatThrownBy(() -> supportTicketService.getTicket(principal, 99L))
                .isInstanceOf(AuthorizationException.class);
    }

    @Test
    @DisplayName("support ticket update changes editable fields for owner")
    void updateTicket() {
        // given
        JwtPrincipal principal = principal(10L, Role.ROLE_USER);
        SupportTicket ticket = ticket(50L, 10L, SupportTicketStatus.PENDING);
        SupportTicketRequest.Update request = new SupportTicketRequest.Update(
                SupportTicketType.FEATURE_REQUEST,
                SupportTicketCategory.FEATURE_UI_UX,
                "Need a better filter UX",
                "Please expose filter chips on the list screen.",
                "Users need to tap through too many menus.",
                "Show chips above the list.",
                "1.2.1",
                null,
                null
        );

        when(supportTicketRepository.findActiveById(50L)).thenReturn(Optional.of(ticket));

        // when
        SupportTicketResponse.Detail result = supportTicketService.updateTicket(principal, 50L, request);

        // then
        assertThat(result.ticketType()).isEqualTo(SupportTicketType.FEATURE_REQUEST);
        assertThat(result.category()).isEqualTo(SupportTicketCategory.FEATURE_UI_UX);
        assertThat(result.osVersion()).isEqualTo("Android 14");
        assertThat(result.deviceModel()).isEqualTo("Pixel 8");
    }

    @Test
    @DisplayName("support ticket delete marks deletedAt for owner")
    void deleteTicket() {
        // given
        JwtPrincipal principal = principal(10L, Role.ROLE_USER);
        SupportTicket ticket = ticket(60L, 10L, SupportTicketStatus.PENDING);

        when(supportTicketRepository.findActiveById(60L)).thenReturn(Optional.of(ticket));

        // when
        SupportTicketResponse.Detail result = supportTicketService.deleteTicket(principal, 60L);

        // then
        assertThat(result.deletedAt()).isNotNull();
    }

    @Test
    @DisplayName("admin can change support ticket status")
    void changeStatusByAdmin() {
        // given
        JwtPrincipal principal = principal(1L, Role.ROLE_ADMIN);
        SupportTicket ticket = ticket(70L, 10L, SupportTicketStatus.PENDING);
        SupportTicketRequest.ChangeStatus request = new SupportTicketRequest.ChangeStatus(
                SupportTicketStatus.IN_PROGRESS,
                "Assigned to backend team."
        );

        when(supportTicketRepository.findActiveById(70L)).thenReturn(Optional.of(ticket));

        // when
        SupportTicketResponse.Detail result = supportTicketService.changeStatus(principal, 70L, request);

        // then
        assertThat(result.status()).isEqualTo(SupportTicketStatus.IN_PROGRESS);
        assertThat(result.adminMemo()).isEqualTo("Assigned to backend team.");
    }

    @Test
    @DisplayName("non admin cannot change support ticket status")
    void changeStatusDeniedForUser() {
        // given
        JwtPrincipal principal = principal(10L, Role.ROLE_USER);
        SupportTicketRequest.ChangeStatus request = new SupportTicketRequest.ChangeStatus(
                SupportTicketStatus.RESOLVED,
                "done"
        );

        // when
        // then
        assertThatThrownBy(() -> supportTicketService.changeStatus(principal, 70L, request))
                .isInstanceOf(AuthorizationException.class);
        verifyNoInteractions(supportTicketRepository);
    }

    private JwtPrincipal principal(Long userId, Role role) {
        return new JwtPrincipal() {
            @Override
            public Long getId() {
                return userId;
            }

            @Override
            public String getRoleAsString() {
                return role.name();
            }
        };
    }

    private SupportTicket ticket(Long ticketId, Long userId, SupportTicketStatus status) {
        SupportTicket ticket = SupportTicket.create(
                user(userId, Role.ROLE_USER),
                SupportTicketType.INQUIRY,
                SupportTicketCategory.INQUIRY_BUG_REPORT,
                "Old title",
                "Old content",
                "Old context",
                "old@example.com",
                "1.0.0",
                "Android 14",
                "Pixel 8"
        );
        ReflectionTestUtils.setField(ticket, "id", ticketId);
        ReflectionTestUtils.setField(ticket, "status", status);
        return ticket;
    }

    private User user(Long userId, Role role) {
        User user = User.builder()
                .nickname("tester")
                .profileImageUrl(null)
                .loginType(LoginType.KAKAO)
                .role(role)
                .socialId("social-" + userId)
                .naviType(NaviType.KAKAO)
                .isActive(true)
                .build();
        ReflectionTestUtils.setField(user, "id", userId);
        return user;
    }
}
