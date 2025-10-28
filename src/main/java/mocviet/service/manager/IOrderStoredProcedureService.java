package mocviet.service.manager;

import java.util.Map;

public interface IOrderStoredProcedureService {
    void confirmOrder(Integer orderId, Integer actorUserId, String note);
    void cancelOrder(Integer orderId, Integer actorUserId, String reason);
    void approveReturn(Integer orderId, Integer managerId, String note, Integer deliveryTeamId);
    void rejectReturn(Integer orderId, Integer managerId, String note);
    void requestReturn(Integer orderId, Integer customerId, String reason);
    void processReturn(Integer orderId, Integer actorUserId, String reason, String refundMethod);
    void markDispatched(Integer orderId, Integer deliveryTeamId, Integer actorUserId, String note);
    void markDelivered(Integer orderId, String proofImageUrl, Integer actorUserId, String note);
    void handlePaymentWebhook(Integer orderId, String paymentMethod, Boolean isSuccess, String gatewayTxnCode);
    void autoCancelUnpaidOnline(Integer expireMinutes);
    Map<String, Object> getOrderDetails(Integer orderId);
}


