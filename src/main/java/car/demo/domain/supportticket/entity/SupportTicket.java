package car.demo.domain.supportticket.entity;

import car.demo.domain.user.entity.User;
import car.demo.global.utils.BaseTimeEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(
        name = "support_ticket",
        indexes = {
                @Index(name = "idx_support_ticket_user_deleted_created", columnList = "user_id, deleted_at, created_at"),
                @Index(name = "idx_support_ticket_type_category_status", columnList = "ticket_type, category, status"),
                @Index(name = "idx_support_ticket_deleted_created", columnList = "deleted_at, created_at")
        }
)
public class SupportTicket extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SupportTicketType ticketType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private SupportTicketCategory category;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 500)
    private String extraContent1;

    @Column(nullable = false, length = 500)
    private String extraContent2;

    @Column(nullable = false, length = 50)
    private String appVersion;

    @Column(nullable = false, length = 100)
    private String osVersion;

    @Column(nullable = false, length = 100)
    private String deviceModel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SupportTicketStatus status;

    @Column(length = 1000)
    private String adminMemo;

    private LocalDateTime deletedAt;

    public static SupportTicket create(
            User user,
            SupportTicketType ticketType,
            SupportTicketCategory category,
            String title,
            String content,
            String extraContent1,
            String extraContent2,
            String appVersion,
            String osVersion,
            String deviceModel
    ) {
        return SupportTicket.builder()
                .user(user)
                .ticketType(ticketType)
                .category(category)
                .title(title)
                .content(content)
                .extraContent1(extraContent1)
                .extraContent2(extraContent2)
                .appVersion(appVersion)
                .osVersion(osVersion)
                .deviceModel(deviceModel)
                .status(SupportTicketStatus.PENDING)
                .build();
    }

    public void updateEditableFields(
            SupportTicketType ticketType,
            SupportTicketCategory category,
            String title,
            String content,
            String extraContent1,
            String extraContent2,
            String appVersion,
            String osVersion,
            String deviceModel
    ) {
        this.ticketType = ticketType;
        this.category = category;
        this.title = title;
        this.content = content;
        this.extraContent1 = extraContent1;
        this.extraContent2 = extraContent2;
        this.appVersion = appVersion;
        this.osVersion = osVersion;
        this.deviceModel = deviceModel;
    }

    public void changeStatus(SupportTicketStatus status, String adminMemo) {
        this.status = status;
        this.adminMemo = adminMemo;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isOwnedBy(Long userId) {
        return user != null && user.getId().equals(userId);
    }
}
