package car.demo.domain.supportticket.dto;

import car.demo.domain.supportticket.entity.SupportTicketCategory;
import car.demo.domain.supportticket.entity.SupportTicketStatus;
import car.demo.domain.supportticket.entity.SupportTicketType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SupportTicketRequest {

    public record Create(
            @NotNull(message = "ticketType is required")
            SupportTicketType ticketType,

            @NotNull(message = "category is required")
            SupportTicketCategory category,

            @NotBlank(message = "title must not be blank")
            @Size(max = 100, message = "title must be 100 characters or fewer")
            String title,

            @NotBlank(message = "content must not be blank")
            @Size(max = 3000, message = "content must be 3000 characters or fewer")
            String content,

            @NotBlank(message = "extraContent1 must not be blank")
            @Size(max = 500, message = "extraContent1 must be 500 characters or fewer")
            String extraContent1,

            @NotBlank(message = "extraContent2 must not be blank")
            @Size(max = 500, message = "extraContent2 must be 500 characters or fewer")
            String extraContent2,

            @NotBlank(message = "appVersion must not be blank")
            @Size(max = 50, message = "appVersion must be 50 characters or fewer")
            String appVersion,

            @NotBlank(message = "osVersion must not be blank")
            @Size(max = 100, message = "osVersion must be 100 characters or fewer")
            String osVersion,

            @NotBlank(message = "deviceModel must not be blank")
            @Size(max = 100, message = "deviceModel must be 100 characters or fewer")
            String deviceModel
    ) {
    }

    public record Update(
            SupportTicketType ticketType,
            SupportTicketCategory category,

            @Pattern(regexp = ".*\\S.*", message = "title must not be blank")
            @Size(max = 100, message = "title must be 100 characters or fewer")
            String title,

            @Pattern(regexp = ".*\\S.*", message = "content must not be blank")
            @Size(max = 3000, message = "content must be 3000 characters or fewer")
            String content,

            @Pattern(regexp = ".*\\S.*", message = "extraContent1 must not be blank")
            @Size(max = 500, message = "extraContent1 must be 500 characters or fewer")
            String extraContent1,

            @Pattern(regexp = ".*\\S.*", message = "extraContent2 must not be blank")
            @Size(max = 500, message = "extraContent2 must be 500 characters or fewer")
            String extraContent2,

            @Pattern(regexp = ".*\\S.*", message = "appVersion must not be blank")
            @Size(max = 50, message = "appVersion must be 50 characters or fewer")
            String appVersion,

            @Pattern(regexp = ".*\\S.*", message = "osVersion must not be blank")
            @Size(max = 100, message = "osVersion must be 100 characters or fewer")
            String osVersion,

            @Pattern(regexp = ".*\\S.*", message = "deviceModel must not be blank")
            @Size(max = 100, message = "deviceModel must be 100 characters or fewer")
            String deviceModel
    ) {
    }

    public record ChangeStatus(
            @NotNull(message = "status is required")
            SupportTicketStatus status,

            @Pattern(regexp = ".*\\S.*", message = "adminMemo must not be blank")
            @Size(max = 1000, message = "adminMemo must be 1000 characters or fewer")
            String adminMemo
    ) {
    }
}
