package car.demo.domain.supportticket.dto;

import car.demo.domain.supportticket.entity.SupportTicketCategory;
import car.demo.domain.supportticket.entity.SupportTicketStatus;
import car.demo.domain.supportticket.entity.SupportTicketType;

public record SupportTicketSearchCondition(
        Long userId,
        SupportTicketType ticketType,
        SupportTicketCategory category,
        SupportTicketStatus status
) {
}
