package mocviet.service;

import jakarta.persistence.criteria.*;
import mocviet.entity.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    // Dùng cho luồng customer sử dụng DTO ở mocviet.dto.customer
    public static Specification<Product> findByCriteria(mocviet.dto.customer.ProductCriteriaDTO criteria) {
        return (Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // == Xử lý JOIN ==
            Join<Product, ProductVariant> variantJoin = null;
            String sortBy = criteria.getSortBy();
            boolean needsVariantJoin = (criteria.getMinPrice() != null
                                        || criteria.getMaxPrice() != null
                                        || criteria.getColorId() != null
                    || (sortBy != null && sortBy.startsWith("price_")));

            if (needsVariantJoin) {
                variantJoin = root.join("variants", JoinType.INNER);
                predicates.add(cb.equal(variantJoin.get("isActive"), true));
            }

            // == Thêm các điều kiện lọc (Predicates) ==
            predicates.add(cb.equal(root.get("isActive"), true));

            if (StringUtils.hasText(criteria.getName())) {
                predicates.add(cb.like(root.get("name"), "%" + criteria.getName() + "%"));
            }

            if (StringUtils.hasText(criteria.getCategorySlug())) {
                predicates.add(cb.equal(root.get("category").get("slug"), criteria.getCategorySlug()));
                predicates.add(cb.equal(root.get("category").get("isActive"), true));
            }

            if (StringUtils.hasText(criteria.getCollectionSlug())) {
                Join<Product, Category> collectionJoin = root.join("collection", JoinType.LEFT);
                predicates.add(cb.equal(collectionJoin.get("slug"), criteria.getCollectionSlug()));
                predicates.add(cb.equal(collectionJoin.get("isActive"), true));
            }

            if (variantJoin != null) {
                if (criteria.getMinPrice() != null) {
                    predicates.add(cb.ge(variantJoin.get("salePrice"), criteria.getMinPrice()));
                }
                if (criteria.getMaxPrice() != null) {
                    predicates.add(cb.le(variantJoin.get("salePrice"), criteria.getMaxPrice()));
                }
                if (criteria.getColorId() != null) {
                    Join<ProductVariant, Color> colorJoin = variantJoin.join("color", JoinType.INNER);
                    predicates.add(cb.equal(colorJoin.get("id"), criteria.getColorId()));
                    predicates.add(cb.equal(colorJoin.get("isActive"), true));
                }
            }

            // == GROUP BY và ORDER BY khi sort theo giá ==
            if (query.getResultType() != Long.class
                    && query.getResultType() != long.class
                    && needsVariantJoin
                    && sortBy != null
                    && sortBy.startsWith("price_")) {

                 query.groupBy(
                    root.get("id"),
                    root.get("name"),
                    root.get("slug"),
                        root.get("description"),
                        root.get("views"),
                    root.get("soldQty"),
                        root.get("avgRating"),
                    root.get("totalReviews"),
                        root.get("isActive"),
                        root.get("createdAt"),
                        root.get("category"),
                        root.get("collection")
                );

                    Expression<?> priceExpression;
                jakarta.persistence.criteria.Order priceOrder;
                if ("price_asc".equals(sortBy)) {
                        priceExpression = cb.min(variantJoin.get("salePrice"));
                        priceOrder = cb.asc(priceExpression);
                } else {
                        priceExpression = cb.max(variantJoin.get("salePrice"));
                         priceOrder = cb.desc(priceExpression);
                    }
                    query.orderBy(priceOrder, cb.asc(root.get("id")));
                }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    // Bản mở rộng cho customer: truyền kèm danh sách categoryIds (bao gồm danh mục con)
    public static Specification<Product> findByCriteria(mocviet.dto.customer.ProductCriteriaDTO criteria, List<Integer> categoryIds) {
        return (Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            Join<Product, ProductVariant> variantJoin = null;
            String sortBy = criteria.getSortBy();
            boolean needsVariantJoin = (criteria.getMinPrice() != null
                    || criteria.getMaxPrice() != null
                    || criteria.getColorId() != null
                    || (sortBy != null && sortBy.startsWith("price_")));

            if (needsVariantJoin) {
                variantJoin = root.join("variants", JoinType.INNER);
                predicates.add(cb.equal(variantJoin.get("isActive"), true));
            }

            predicates.add(cb.equal(root.get("isActive"), true));

            if (StringUtils.hasText(criteria.getName())) {
                predicates.add(cb.like(root.get("name"), "%" + criteria.getName() + "%"));
            }

            if (categoryIds != null && !categoryIds.isEmpty()) {
                predicates.add(root.get("category").get("id").in(categoryIds));
            } else if (StringUtils.hasText(criteria.getCategorySlug())) {
                predicates.add(cb.equal(root.get("category").get("slug"), criteria.getCategorySlug()));
                predicates.add(cb.equal(root.get("category").get("isActive"), true));
            }

            if (StringUtils.hasText(criteria.getCollectionSlug())) {
                Join<Product, Category> collectionJoin = root.join("collection", JoinType.LEFT);
                predicates.add(cb.equal(collectionJoin.get("slug"), criteria.getCollectionSlug()));
                predicates.add(cb.equal(collectionJoin.get("isActive"), true));
            }

            if (variantJoin != null) {
                if (criteria.getMinPrice() != null) {
                    predicates.add(cb.ge(variantJoin.get("salePrice"), criteria.getMinPrice()));
                }
                if (criteria.getMaxPrice() != null) {
                    predicates.add(cb.le(variantJoin.get("salePrice"), criteria.getMaxPrice()));
                }
                if (criteria.getColorId() != null) {
                    Join<ProductVariant, Color> colorJoin = variantJoin.join("color", JoinType.INNER);
                    predicates.add(cb.equal(colorJoin.get("id"), criteria.getColorId()));
                    predicates.add(cb.equal(colorJoin.get("isActive"), true));
                }
            }

            if (query.getResultType() != Long.class
                    && query.getResultType() != long.class
                    && needsVariantJoin
                    && sortBy != null
                    && sortBy.startsWith("price_")) {
                query.groupBy(
                        root.get("id"),
                        root.get("name"),
                        root.get("slug"),
                        root.get("description"),
                        root.get("views"),
                        root.get("soldQty"),
                        root.get("avgRating"),
                        root.get("totalReviews"),
                        root.get("isActive"),
                        root.get("createdAt"),
                        root.get("category"),
                        root.get("collection")
                );

                Expression<?> priceExpression;
                jakarta.persistence.criteria.Order priceOrder;
                if ("price_asc".equals(sortBy)) {
                    priceExpression = cb.min(variantJoin.get("salePrice"));
                    priceOrder = cb.asc(priceExpression);
                } else {
                    priceExpression = cb.max(variantJoin.get("salePrice"));
                    priceOrder = cb.desc(priceExpression);
                }
                query.orderBy(priceOrder, cb.asc(root.get("id")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    // Dùng cho luồng guest/product sử dụng DTO ở mocviet.dto, có truyền categoryIds
    public static Specification<Product> findByCriteria(mocviet.dto.ProductCriteriaDTO criteria, List<Integer> categoryIds) {
        return (Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // == Xử lý JOIN ==
            Join<Product, ProductVariant> variantJoin = null;
            String sortBy = criteria.getSortBy();
            boolean needsVariantJoin = (criteria.getMinPrice() != null
                    || criteria.getMaxPrice() != null
                    || criteria.getColorId() != null
                    || (sortBy != null && sortBy.startsWith("price_")));

            if (needsVariantJoin) {
                variantJoin = root.join("variants", JoinType.INNER);
                predicates.add(cb.equal(variantJoin.get("isActive"), true));
            }

            // == Thêm các điều kiện lọc (Predicates) ==
            predicates.add(cb.equal(root.get("isActive"), true));

            if (StringUtils.hasText(criteria.getName())) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + criteria.getName().toLowerCase() + "%"));
            }

            if (categoryIds != null && !categoryIds.isEmpty()) {
                predicates.add(root.get("category").get("id").in(categoryIds));
            }

            if (StringUtils.hasText(criteria.getCollectionSlug())) {
                Join<Product, Category> collectionJoin = root.join("collection", JoinType.LEFT);
                predicates.add(cb.equal(collectionJoin.get("slug"), criteria.getCollectionSlug()));
                predicates.add(cb.equal(collectionJoin.get("isActive"), true));
            }

            if (variantJoin != null) {
                if (criteria.getMinPrice() != null) {
                    predicates.add(cb.ge(variantJoin.get("salePrice"), criteria.getMinPrice()));
                }
                if (criteria.getMaxPrice() != null) {
                    predicates.add(cb.le(variantJoin.get("salePrice"), criteria.getMaxPrice()));
                }
                if (criteria.getColorId() != null) {
                    Join<ProductVariant, Color> colorJoin = variantJoin.join("color", JoinType.INNER);
                    predicates.add(cb.equal(colorJoin.get("id"), criteria.getColorId()));
                    predicates.add(cb.equal(colorJoin.get("isActive"), true));
                 }
            }

            // == GROUP BY và ORDER BY khi sort theo giá ==
            if (query.getResultType() != Long.class
                    && query.getResultType() != long.class
                    && needsVariantJoin
                    && sortBy != null
                    && sortBy.startsWith("price_")) {

                 query.groupBy(
                    root.get("id"),
                    root.get("name"),
                    root.get("slug"),
                    root.get("description"),
                    root.get("views"),
                    root.get("soldQty"),
                    root.get("avgRating"),
                    root.get("totalReviews"),
                    root.get("isActive"),
                    root.get("createdAt"),
                        root.get("category"),
                        root.get("collection")
                 );

                 Expression<?> priceExpression;
                 jakarta.persistence.criteria.Order priceOrder;
                if ("price_asc".equals(sortBy)) {
                    priceExpression = cb.min(variantJoin.get("salePrice"));
                    priceOrder = cb.asc(priceExpression);
                } else {
                    priceExpression = cb.max(variantJoin.get("salePrice"));
                    priceOrder = cb.desc(priceExpression);
                 }
                 query.orderBy(priceOrder, cb.asc(root.get("id")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}