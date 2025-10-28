package mocviet.service;

import jakarta.persistence.criteria.*;
import mocviet.dto.customer.ProductCriteriaDTO;
import mocviet.entity.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> findByCriteria(ProductCriteriaDTO criteria) {
        return (Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // == Xử lý JOIN ==
            Join<Product, ProductVariant> variantJoin = null;
            boolean needsVariantJoin = (criteria.getMinPrice() != null
                                        || criteria.getMaxPrice() != null
                                        || criteria.getColorId() != null
                                        || criteria.getSortBy().startsWith("price_"));

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
                    predicates.add(cb.equal(variantJoin.get("color").get("id"), criteria.getColorId()));
                    predicates.add(cb.equal(variantJoin.get("color").get("isActive"), true));
                }
            }

            // == Xử lý GROUP BY và ORDER BY ==
            // Chỉ group by khi đã join variant và không phải query count
            if (query.getResultType() != Long.class && query.getResultType() != long.class && needsVariantJoin) {
                 // **** SỬA PHẦN NÀY ****
                 // Group by tất cả các cột của Product đang được SELECT
                 // (Cần đảm bảo khớp với câu SELECT mà Hibernate tạo ra)
                 query.groupBy(
                    root.get("id"),
                    root.get("avgRating"), // Thêm các cột vào GROUP BY
                    root.get("category"), // Group by entity join hoặc ID
                    root.get("collection"), // Group by entity join hoặc ID
                    root.get("createdAt"),
                    root.get("description"),
                    root.get("isActive"),
                    root.get("name"),
                    root.get("slug"),
                    root.get("soldQty"),
                    root.get("totalReviews"),
                    root.get("views")
                    // Thêm các trường khác của Product nếu Hibernate select chúng
                 );
                 // **********************

                 // Xử lý ORDER BY giá bằng hàm tổng hợp
                if (criteria.getSortBy().startsWith("price_")) {
                    Expression<?> priceExpression;
                    jakarta.persistence.criteria.Order priceOrder; // Sử dụng tên đầy đủ
                    if (criteria.getSortBy().equals("price_asc")) {
                        priceExpression = cb.min(variantJoin.get("salePrice"));
                        priceOrder = cb.asc(priceExpression);
                    } else { // price_desc
                        priceExpression = cb.max(variantJoin.get("salePrice"));
                         priceOrder = cb.desc(priceExpression);
                    }
                     // Sắp xếp theo giá (từ aggregate) và ID
                    query.orderBy(priceOrder, cb.asc(root.get("id")));
                }
                 // Lưu ý: Các trường hợp sort khác (không theo giá) sẽ do Pageable xử lý
                 // và không cần group by phức tạp như vậy.
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}