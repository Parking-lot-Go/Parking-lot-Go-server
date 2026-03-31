package car.demo.domain.supportticket.repository;

import car.demo.domain.supportticket.entity.SupportTicket;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long>, SupportTicketRepositoryCustom {

    @Query("select st from SupportTicket st join fetch st.user where st.id = :id and st.deletedAt is null")
    Optional<SupportTicket> findActiveById(@Param("id") Long id);
}
