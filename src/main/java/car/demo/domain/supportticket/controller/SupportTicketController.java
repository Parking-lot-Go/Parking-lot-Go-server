package car.demo.domain.supportticket.controller;

import car.demo.domain.auth.service.JwtPrincipal;
import car.demo.domain.supportticket.dto.SupportTicketRequest;
import car.demo.domain.supportticket.dto.SupportTicketResponse;
import car.demo.domain.supportticket.entity.SupportTicketCategory;
import car.demo.domain.supportticket.entity.SupportTicketStatus;
import car.demo.domain.supportticket.entity.SupportTicketType;
import car.demo.domain.supportticket.service.SupportTicketService;
import car.demo.global.dto.CommonResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/support-tickets")
public class SupportTicketController {

    private final SupportTicketService supportTicketService;

    @PostMapping
    public CommonResponse<SupportTicketResponse.Detail> createTicket(
            @AuthenticationPrincipal JwtPrincipal principal,
            @RequestBody @Valid SupportTicketRequest.Create request
    ) {
        return CommonResponse.ok(supportTicketService.createTicket(principal, request));
    }

    @GetMapping
    public CommonResponse<List<SupportTicketResponse.Detail>> getTickets(
            @AuthenticationPrincipal JwtPrincipal principal,
            @RequestParam(required = false) SupportTicketType type,
            @RequestParam(required = false) SupportTicketCategory category,
            @RequestParam(required = false) SupportTicketStatus status,
            @RequestParam(required = false) Boolean mine
    ) {
        return CommonResponse.ok(supportTicketService.getTickets(principal, type, category, status, mine));
    }

    @GetMapping("/{id}")
    public CommonResponse<SupportTicketResponse.Detail> getTicket(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long id
    ) {
        return CommonResponse.ok(supportTicketService.getTicket(principal, id));
    }

    @PatchMapping("/{id}")
    public CommonResponse<SupportTicketResponse.Detail> updateTicket(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long id,
            @RequestBody @Valid SupportTicketRequest.Update request
    ) {
        return CommonResponse.ok(supportTicketService.updateTicket(principal, id, request));
    }

    @DeleteMapping("/{id}")
    public CommonResponse<SupportTicketResponse.Detail> deleteTicket(
            @AuthenticationPrincipal JwtPrincipal principal,
            @PathVariable Long id
    ) {
        return CommonResponse.ok(supportTicketService.deleteTicket(principal, id));
    }
}
