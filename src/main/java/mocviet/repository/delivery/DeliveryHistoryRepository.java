package mocviet.repository.delivery;

import mocviet.entity.DeliveryHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryHistoryRepository extends JpaRepository<DeliveryHistory, Integer> {

    /**
     * Tìm lịch sử theo OrderDelivery ID, sắp xếp theo thời gian mới nhất
     */
    List<DeliveryHistory> findByOrderDeliveryIdOrderByChangedAtDesc(Integer orderDeliveryId);
}