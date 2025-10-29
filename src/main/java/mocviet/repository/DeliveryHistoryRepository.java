package mocviet.repository;

import mocviet.entity.DeliveryHistory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryHistoryRepository extends JpaRepository<DeliveryHistory, Integer> {
	/**
     * Tìm lịch sử theo OrderDelivery ID, sắp xếp theo thời gian mới nhất
     */
    List<DeliveryHistory> findByOrderDeliveryIdOrderByChangedAtDesc(Integer orderDeliveryId);
}
