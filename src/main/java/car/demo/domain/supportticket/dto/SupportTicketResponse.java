package car.demo.domain.supportticket.dto;

import car.demo.domain.supportticket.entity.SupportTicket;
import car.demo.domain.supportticket.entity.SupportTicketCategory;
import car.demo.domain.supportticket.entity.SupportTicketStatus;
import car.demo.domain.supportticket.entity.SupportTicketType;
import java.time.LocalDateTime;

public class SupportTicketResponse {

    public record Detail(
            Long id,
            Long userId,
            SupportTicketType ticketType,
            SupportTicketCategory category,
            String title,
            String content,
            String extraContent1,
            String extraContent2,
            String appVersion,
            String osVersion,
            String deviceModel,
            SupportTicketStatus status,
            String adminMemo,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime deletedAt
    ) {
        public static Detail from(SupportTicket ticket) {
            return new Detail(
                    ticket.getId(),
                    ticket.getUser().getId(),
                    ticket.getTicketType(),
                    ticket.getCategory(),
                    ticket.getTitle(),
                    ticket.getContent(),
                    ticket.getExtraContent1(),
                    ticket.getExtraContent2(),
                    ticket.getAppVersion(),
                    ticket.getOsVersion(),
                    ticket.getDeviceModel(),
                    ticket.getStatus(),
                    ticket.getAdminMemo(),
                    ticket.getCreatedAt(),
                    ticket.getUpdatedAt(),
                    ticket.getDeletedAt()
            );
        }
    }
}
