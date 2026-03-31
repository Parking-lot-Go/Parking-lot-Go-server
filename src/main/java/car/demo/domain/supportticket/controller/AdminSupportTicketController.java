package car.demo.domain.supportticket.controller;

import car.demo.domain.auth.service.JwtPrincipal;
import car.demo.domain.supportticket.dto.SupportTicketRequest;
import car.demo.domain.supportticket.dto.SupportTicketResponse;
import car.demo.domain.supportticket.service.SupportTicketService;
import car.demo.global.dto.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/support-tickets")
public class AdminSupportTicketController {

    private final SupportTicketService supportTicketService;

    @PatchMapping("/{id}/status")
    public CommonResponse<SupportTicketResponse.Detail> changeStatus(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long id,
            @RequestBody @Valid SupportTicketRequest.ChangeStatus request
    ) {
        return CommonResponse.ok(supportTicketService.changeStatus(principal, id, request));
    }
}
