package car.demo.domain.supportticket.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SupportTicketCategory {
    INQUIRY_ACCOUNT(SupportTicketType.INQUIRY),
    INQUIRY_APP_USAGE(SupportTicketType.INQUIRY),
    INQUIRY_BUG_REPORT(SupportTicketType.INQUIRY),
    INQUIRY_OTHER(SupportTicketType.INQUIRY),
    FEATURE_NEW_CAPABILITY(SupportTicketType.FEATURE_REQUEST),
    FEATURE_UI_UX(SupportTicketType.FEATURE_REQUEST),
    FEATURE_PERFORMANCE(SupportTicketType.FEATURE_REQUEST),
    FEATURE_OTHER(SupportTicketType.FEATURE_REQUEST);

    private final SupportTicketType ticketType;

    public boolean matches(SupportTicketType ticketType) {
        return this.ticketType == ticketType;
    }
}
