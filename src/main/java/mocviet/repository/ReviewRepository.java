package mocviet.repository;

import mocviet.entity.Review;
import org.springframework.data.domain.Pageable;
// import org.springframework.data.jpa.repository.EntityGraph; // Đã xóa dòng này
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    /**
     * Lấy các đánh giá 5 sao mới nhất, mỗi sản phẩm chỉ lấy 1 đánh giá.
     * Sử dụng native query vì ROW_NUMBER() không được hỗ trợ trực tiếp trong JPQL chuẩn.
     * Lưu ý: Product và User sẽ được load LAZY theo mặc định.
     */
    // @EntityGraph(attributePaths = {"product", "user"}) // <<< ĐÃ XÓA DÒNG NÀY
    @Query(value = "WITH RankedReviews AS (" +
            "SELECT r.*, ROW_NUMBER() OVER(PARTITION BY r.product_id ORDER BY r.created_at DESC) as rn " +
            "FROM Review r WHERE r.rating = 5 AND r.is_hidden = 0" + // Chỉ lấy 5 sao và không bị ẩn
            ") " +
            "SELECT rr.* FROM RankedReviews rr " +
            "WHERE rr.rn = 1 " + // Lấy review mới nhất cho mỗi sản phẩm
            "ORDER BY rr.created_at DESC", // Sắp xếp tổng thể theo thời gian mới nhất
            nativeQuery = true)
    List<Review> findTopBestReviewsDistinctProduct(Pageable pageable);
}