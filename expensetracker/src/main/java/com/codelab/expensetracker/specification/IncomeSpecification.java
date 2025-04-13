package com.codelab.expensetracker.specification;


import com.codelab.expensetracker.models.Income;
import com.codelab.expensetracker.models.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IncomeSpecification {
    public static Specification<Income> withFilters(
            User user,
            Double minAmount,
            Double maxAmount,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("user"), user));

            // Handle amount range
            if (minAmount != null && maxAmount != null) {
                predicates.add(cb.between(root.get("amount"), minAmount, maxAmount));
            } else if (minAmount != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("amount"), minAmount));
            } else if (maxAmount != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("amount"), maxAmount));
            }

            // Handle date range
            if (startDate != null && endDate != null) {
                predicates.add(cb.between(root.get("dateTime"),
                        startDate.atStartOfDay(),
                        endDate.atTime(23, 59, 59)));
            } else if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dateTime"), startDate.atStartOfDay()));
            } else if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dateTime"), endDate.atTime(23, 59, 59)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

