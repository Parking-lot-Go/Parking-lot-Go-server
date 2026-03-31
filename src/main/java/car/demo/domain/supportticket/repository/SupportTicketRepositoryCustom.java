package car.demo.domain.supportticket.repository;

import car.demo.domain.supportticket.dto.SupportTicketSearchCondition;
import car.demo.domain.supportticket.entity.SupportTicket;
import java.util.List;

public interface SupportTicketRepositoryCustom {

    List<SupportTicket> search(SupportTicketSearchCondition condition);
}
