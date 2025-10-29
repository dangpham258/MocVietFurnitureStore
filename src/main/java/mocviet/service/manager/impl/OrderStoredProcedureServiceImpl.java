package mocviet.service.manager.impl;

import lombok.RequiredArgsConstructor;
import mocviet.service.manager.IOrderStoredProcedureService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderStoredProcedureServiceImpl implements IOrderStoredProcedureService {

    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void confirmOrder(Integer orderId, Integer actorUserId, String note) {
        String sql = "EXEC sp_ConfirmOrder @order_id = ?, @actor_user_id = ?, @note = ?";
        jdbcTemplate.update(sql, orderId, actorUserId, note);
    }

    @Override
    @Transactional
    public void cancelOrder(Integer orderId, Integer actorUserId, String reason) {
        String sql = "EXEC sp_CancelOrder @order_id = ?, @actor_user_id = ?, @reason = ?";
        jdbcTemplate.update(sql, orderId, actorUserId, reason);
    }

    @Override
    @Transactional
    public void approveReturn(Integer orderId, Integer managerId, String note, Integer deliveryTeamId) {
        String sql = "EXEC sp_ApproveReturn @order_id = ?, @manager_id = ?, @note = ?, @delivery_team_id = ?";
        jdbcTemplate.update(sql, orderId, managerId, note, deliveryTeamId);
    }

    @Override
    @Transactional
    public void rejectReturn(Integer orderId, Integer managerId, String note) {
        String sql = "EXEC sp_RejectReturn @order_id = ?, @manager_id = ?, @note = ?";
        jdbcTemplate.update(sql, orderId, managerId, note);
    }

    @Override
    @Transactional
    public void requestReturn(Integer orderId, Integer customerId, String reason) {
        String sql = "EXEC sp_RequestReturn @order_id = ?, @customer_id = ?, @reason = ?";
        jdbcTemplate.update(sql, orderId, customerId, reason);
    }

    @Override
    @Transactional
    public void processReturn(Integer orderId, Integer actorUserId, String reason, String refundMethod) {
        String sql = "EXEC sp_ReturnOrder @order_id = ?, @actor_user_id = ?, @reason = ?, @refund_method = ?";
        jdbcTemplate.update(sql, orderId, actorUserId, reason, refundMethod);
    }

    @Override
    @Transactional
    public void markDispatched(Integer orderId, Integer deliveryTeamId, Integer actorUserId, String note) {
        String sql = "EXEC sp_MarkDispatched @order_id = ?, @delivery_team_id = ?, @actor_user_id = ?, @note = ?";
        jdbcTemplate.update(sql, orderId, deliveryTeamId, actorUserId, note);
    }

    @Override
    @Transactional
    public void markDelivered(Integer orderId, String proofImageUrl, Integer actorUserId, String note) {
        String sql = "EXEC sp_MarkDelivered @order_id = ?, @proof_image_url = ?, @actor_user_id = ?, @note = ?";
        jdbcTemplate.update(sql, orderId, proofImageUrl, actorUserId, note);
    }

    @Override
    @Transactional
    public void handlePaymentWebhook(Integer orderId, String paymentMethod, Boolean isSuccess, String gatewayTxnCode) {
        String sql = "EXEC sp_HandlePaymentWebhook @order_id = ?, @payment_method = ?, @is_success = ?, @gateway_txn_code = ?";
        jdbcTemplate.update(sql, orderId, paymentMethod, isSuccess, gatewayTxnCode);
    }

    @Override
    @Transactional
    public void autoCancelUnpaidOnline(Integer expireMinutes) {
        String sql = "EXEC sp_AutoCancelUnpaidOnline @expire_minutes = ?";
        jdbcTemplate.update(sql, expireMinutes);
    }

    @Override
    public Map<String, Object> getOrderDetails(Integer orderId) {
        String sql = "SELECT * FROM Orders WHERE id = ?";
        return jdbcTemplate.queryForMap(sql, orderId);
    }
}


