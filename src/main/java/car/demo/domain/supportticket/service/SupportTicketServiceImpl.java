package car.demo.domain.supportticket.service;

import car.demo.domain.auth.service.JwtPrincipal;
import car.demo.domain.supportticket.dto.SupportTicketRequest;
import car.demo.domain.supportticket.dto.SupportTicketResponse;
import car.demo.domain.supportticket.dto.SupportTicketSearchCondition;
import car.demo.domain.supportticket.entity.SupportTicket;
import car.demo.domain.supportticket.entity.SupportTicketCategory;
import car.demo.domain.supportticket.entity.SupportTicketStatus;
import car.demo.domain.supportticket.entity.SupportTicketType;
import car.demo.domain.supportticket.repository.SupportTicketRepository;
import car.demo.domain.user.entity.User;
import car.demo.domain.user.repository.UserRepository;
import car.demo.global.exception.ErrorCode;
import car.demo.global.exception.custom.BusinessException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SupportTicketServiceImpl implements SupportTicketService {

    private final SupportTicketRepository supportTicketRepository;
    private final UserRepository userRepository;
    private final SupportTicketAccessChecker accessChecker;

    @Override
    @Transactional
    public SupportTicketResponse.Detail createTicket(JwtPrincipal principal, SupportTicketRequest.Create request) {
        Long userId = accessChecker.requireUserId(principal);
        validateCategory(request.ticketType(), request.category());
        User user = findUser(userId);
        SupportTicket ticket = supportTicketRepository.save(toTicket(user, request));
        return SupportTicketResponse.Detail.from(ticket);
    }

    @Override
    public List<SupportTicketResponse.Detail> getTickets(
            JwtPrincipal principal,
            SupportTicketType type,
            SupportTicketCategory category,
            SupportTicketStatus status,
            Boolean mine
    ) {
        Long userId = resolveSearchUserId(principal, mine);
        SupportTicketSearchCondition condition = new SupportTicketSearchCondition(userId, type, category, status);
        return supportTicketRepository.search(condition).stream()
                .map(SupportTicketResponse.Detail::from)
                .toList();
    }

    @Override
    public SupportTicketResponse.Detail getTicket(JwtPrincipal principal, Long id) {
        SupportTicket ticket = findActiveTicket(id);
        if (!accessChecker.isAdmin(principal)) {
            accessChecker.requireOwner(ticket, accessChecker.requireUserId(principal));
        }
        return SupportTicketResponse.Detail.from(ticket);
    }

    @Override
    @Transactional
    public SupportTicketResponse.Detail updateTicket(JwtPrincipal principal, Long id, SupportTicketRequest.Update request) {
        Long userId = accessChecker.requireUserId(principal);
        SupportTicket ticket = findActiveTicket(id);
        accessChecker.requireOwner(ticket, userId);
        SupportTicketType ticketType = resolveTicketType(ticket, request.ticketType());
        SupportTicketCategory category = resolveCategory(ticket, request.category());
        validateCategory(ticketType, category);
        ticket.updateEditableFields(
                ticketType,
                category,
                valueOrDefault(request.title(), ticket.getTitle()),
                valueOrDefault(request.content(), ticket.getContent()),
                valueOrDefault(request.extraContent1(), ticket.getExtraContent1()),
                valueOrDefault(request.extraContent2(), ticket.getExtraContent2()),
                valueOrDefault(request.appVersion(), ticket.getAppVersion()),
                valueOrDefault(request.osVersion(), ticket.getOsVersion()),
                valueOrDefault(request.deviceModel(), ticket.getDeviceModel())
        );
        return SupportTicketResponse.Detail.from(ticket);
    }

    @Override
    @Transactional
    public SupportTicketResponse.Detail deleteTicket(JwtPrincipal principal, Long id) {
        Long userId = accessChecker.requireUserId(principal);
        SupportTicket ticket = findActiveTicket(id);
        accessChecker.requireOwner(ticket, userId);
        ticket.softDelete();
        return SupportTicketResponse.Detail.from(ticket);
    }

    @Override
    @Transactional
    public SupportTicketResponse.Detail changeStatus(JwtPrincipal principal, Long id, SupportTicketRequest.ChangeStatus request) {
        accessChecker.requireAdmin(principal);
        SupportTicket ticket = findActiveTicket(id);
        ticket.changeStatus(request.status(), request.adminMemo());
        return SupportTicketResponse.Detail.from(ticket);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_ENTITY));
    }

    private SupportTicket findActiveTicket(Long id) {
        return supportTicketRepository.findActiveById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_ENTITY));
    }

    private Long resolveSearchUserId(JwtPrincipal principal, Boolean mine) {
        Long userId = accessChecker.requireUserId(principal);
        if (!accessChecker.isAdmin(principal)) {
            return userId;
        }
        return Boolean.TRUE.equals(mine) ? userId : null;
    }

    private SupportTicketType resolveTicketType(SupportTicket ticket, SupportTicketType ticketType) {
        return valueOrDefault(ticketType, ticket.getTicketType());
    }

    private SupportTicketCategory resolveCategory(SupportTicket ticket, SupportTicketCategory category) {
        return valueOrDefault(category, ticket.getCategory());
    }

    private <T> T valueOrDefault(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    private void validateCategory(SupportTicketType ticketType, SupportTicketCategory category) {
        if (!category.matches(ticketType)) {
            throw new BusinessException(ErrorCode.INVALID_SUPPORT_TICKET_CATEGORY);
        }
    }

    private SupportTicket toTicket(User user, SupportTicketRequest.Create request) {
        return SupportTicket.create(
                user,
                request.ticketType(),
                request.category(),
                request.title(),
                request.content(),
                request.extraContent1(),
                request.extraContent2(),
                request.appVersion(),
                request.osVersion(),
                request.deviceModel()
        );
    }
}
