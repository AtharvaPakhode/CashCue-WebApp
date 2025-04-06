package com.codelab.expensetracker.specification;

import com.codelab.expensetracker.models.Category;
import com.codelab.expensetracker.models.Expense;
import com.codelab.expensetracker.models.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;
import org.springframework.stereotype.Component;

@Component
public class ExpenseSpecification {
    public static Specification<Expense> withFilters(
            User user,
            List<Category> category,
            Double minAmount,
            Double maxAmount,
            String paymentMethod,
            LocalDate startDate,
            LocalDate endDate
    ) {
        return (root, query, cb) -> {
            
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("user"), user));

            if (category != null && !category.isEmpty()) {
                predicates.add(root.get("category").get("categoryName").in(
                        category.stream().map(Category::getCategoryName).toList()
                ));
            }

            if (minAmount != null && maxAmount != null) {
                predicates.add(cb.between(root.get("amount"), minAmount, maxAmount));
            }

            if (paymentMethod != null && !paymentMethod.isEmpty()) {
                predicates.add(cb.equal(root.get("paymentMethod"), paymentMethod));
            }

            if (startDate != null && endDate != null) {
                predicates.add(cb.between(root.get("dateTime"), startDate.atStartOfDay(), endDate.atTime(23, 59, 59)));
            }

            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
