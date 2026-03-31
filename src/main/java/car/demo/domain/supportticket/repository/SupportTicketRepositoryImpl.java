package car.demo.domain.supportticket.repository;

import car.demo.domain.supportticket.dto.SupportTicketSearchCondition;
import car.demo.domain.supportticket.entity.SupportTicket;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class SupportTicketRepositoryImpl implements SupportTicketRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<SupportTicket> search(SupportTicketSearchCondition condition) {
        var builder = entityManager.getCriteriaBuilder();
        var query = builder.createQuery(SupportTicket.class);
        var root = query.from(SupportTicket.class);
        root.fetch("user");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(builder.isNull(root.get("deletedAt")));
        addUserPredicate(condition, builder, root, predicates);
        addTypePredicate(condition, builder, root, predicates);
        addCategoryPredicate(condition, builder, root, predicates);
        addStatusPredicate(condition, builder, root, predicates);

        query.distinct(true);
        query.select(root);
        query.where(predicates.toArray(Predicate[]::new));
        query.orderBy(builder.desc(root.get("createdAt")), builder.desc(root.get("id")));
        return entityManager.createQuery(query).getResultList();
    }

    private void addUserPredicate(
            SupportTicketSearchCondition condition,
            jakarta.persistence.criteria.CriteriaBuilder builder,
            jakarta.persistence.criteria.Root<SupportTicket> root,
            List<Predicate> predicates
    ) {
        if (condition.userId() != null) {
            predicates.add(builder.equal(root.get("user").get("id"), condition.userId()));
        }
    }

    private void addTypePredicate(
            SupportTicketSearchCondition condition,
            jakarta.persistence.criteria.CriteriaBuilder builder,
            jakarta.persistence.criteria.Root<SupportTicket> root,
            List<Predicate> predicates
    ) {
        if (condition.ticketType() != null) {
            predicates.add(builder.equal(root.get("ticketType"), condition.ticketType()));
        }
    }

    private void addCategoryPredicate(
            SupportTicketSearchCondition condition,
            jakarta.persistence.criteria.CriteriaBuilder builder,
            jakarta.persistence.criteria.Root<SupportTicket> root,
            List<Predicate> predicates
    ) {
        if (condition.category() != null) {
            predicates.add(builder.equal(root.get("category"), condition.category()));
        }
    }

    private void addStatusPredicate(
            SupportTicketSearchCondition condition,
            jakarta.persistence.criteria.CriteriaBuilder builder,
            jakarta.persistence.criteria.Root<SupportTicket> root,
            List<Predicate> predicates
    ) {
        if (condition.status() != null) {
            predicates.add(builder.equal(root.get("status"), condition.status()));
        }
    }
}
