package mocviet.service;

import jakarta.persistence.criteria.*;
import mocviet.dto.ProductCriteriaDTO;
import mocviet.entity.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> findByCriteria(ProductCriteriaDTO criteria, List<Integer> categoryIds) {
        return (Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // == Xử lý JOIN ==
            Join<Product, ProductVariant> variantJoin = null;
            boolean needsVariantJoin = (criteria.getMinPrice() != null
                                        || criteria.getMaxPrice() != null
                                        || criteria.getColorId() != null
                                        || (criteria.getSortBy() != null && criteria.getSortBy().startsWith("price_")));

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
                 // Không cần add(cb.equal(root.get("category").get("isActive"), true));
                 // vì hàm getCategoryIdsIncludingDescendants đã lọc rồi.
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


            // == Xử lý GROUP BY và ORDER BY ==
            // Chỉ group by khi sắp xếp theo giá và không phải query count
            if (query.getResultType() != Long.class && query.getResultType() != long.class && needsVariantJoin && criteria.getSortBy() != null && criteria.getSortBy().startsWith("price_")) {

                 // **** SỬA PHẦN NÀY: GROUP BY TẤT CẢ CÁC CỘT CỦA PRODUCT ****
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
                    root.get("category"), // Group by category (hoặc category.id)
                    root.get("collection") // Group by collection (hoặc collection.id)
                    // Thêm các trường khác của Product nếu có
                 );
                 // **********************************************************


                 // Xử lý ORDER BY giá
                 Expression<?> priceExpression;
                 jakarta.persistence.criteria.Order priceOrder;
                 if (criteria.getSortBy().equals("price_asc")) {
                    priceExpression = cb.min(variantJoin.get("salePrice"));
                    priceOrder = cb.asc(priceExpression);
                 } else { // price_desc
                    priceExpression = cb.max(variantJoin.get("salePrice"));
                    priceOrder = cb.desc(priceExpression);
                 }
                 // Sắp xếp theo giá MIN/MAX, sau đó theo ID để ổn định
                 query.orderBy(priceOrder, cb.asc(root.get("id")));
            }
            // Các trường hợp sort khác (không theo giá) sẽ do Pageable xử lý bình thường

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}