package car.demo.domain.supportticket.service;

import car.demo.domain.auth.service.JwtPrincipal;
import car.demo.domain.supportticket.dto.SupportTicketRequest;
import car.demo.domain.supportticket.dto.SupportTicketResponse;
import car.demo.domain.supportticket.entity.SupportTicketCategory;
import car.demo.domain.supportticket.entity.SupportTicketStatus;
import car.demo.domain.supportticket.entity.SupportTicketType;
import java.util.List;

public interface SupportTicketService {

    SupportTicketResponse.Detail createTicket(JwtPrincipal principal, SupportTicketRequest.Create request);

    List<SupportTicketResponse.Detail> getTickets(
            JwtPrincipal principal,
            SupportTicketType type,
            SupportTicketCategory category,
            SupportTicketStatus status,
            Boolean mine
    );

    SupportTicketResponse.Detail getTicket(JwtPrincipal principal, Long id);

    SupportTicketResponse.Detail updateTicket(JwtPrincipal principal, Long id, SupportTicketRequest.Update request);

    SupportTicketResponse.Detail deleteTicket(JwtPrincipal principal, Long id);

    SupportTicketResponse.Detail changeStatus(JwtPrincipal principal, Long id, SupportTicketRequest.ChangeStatus request);
}
