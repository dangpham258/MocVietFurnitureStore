package mocviet.service.manager;

import lombok.RequiredArgsConstructor;
import mocviet.dto.manager.*;
import mocviet.entity.*;
import mocviet.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryAssignmentService {
    
    private final OrdersRepository ordersRepository;
    private final OrderDeliveryRepository orderDeliveryRepository;
    private final DeliveryTeamRepository deliveryTeamRepository;
    private final DeliveryTeamZoneRepository deliveryTeamZoneRepository;
    private final ProvinceZoneRepository provinceZoneRepository;
    private final ShippingZoneRepository shippingZoneRepository;
    private final DeliveryHistoryRepository deliveryHistoryRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final UserRepository userRepository;
    
    // ===== PENDING ORDERS MANAGEMENT =====
    
    @Transactional(readOnly = true)
    public Page<PendingOrderDTO> getPendingOrders(Pageable pageable) {
        // Kiểm tra xem có sort theo totalAmount không bằng cách kiểm tra Sort object
        boolean isSortByTotalAmount = pageable.getSort().stream()
            .anyMatch(order -> order.getProperty().equals("totalAmount"));
        
        Page<Orders> orders;
        
        if (isSortByTotalAmount) {
            // Sort theo tổng tiền thực tế
            boolean isDescending = pageable.getSort().stream()
                .filter(order -> order.getProperty().equals("totalAmount"))
                .anyMatch(order -> order.getDirection().isDescending());
            
            // Lấy tất cả pending orders đã sort
            List<Orders> allOrders;
            if (isDescending) {
                allOrders = ordersRepository.findPendingOrdersByTotalAmountDescNative();
            } else {
                allOrders = ordersRepository.findPendingOrdersByTotalAmountAscNative();
            }
            
            // Thực hiện pagination thủ công
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), allOrders.size());
            List<Orders> pagedOrders = allOrders.subList(start, end);
            
            // Tạo Page object
            orders = new org.springframework.data.domain.PageImpl<>(pagedOrders, pageable, allOrders.size());
        } else {
            // Sort theo các field khác
            orders = ordersRepository.findByStatusAndOrderDeliveryIsNull(
                Orders.OrderStatus.CONFIRMED, pageable);
        }
        
        return orders.map(this::mapToPendingOrderDTO);
    }
    
    @Transactional(readOnly = true)
    public Page<PendingOrderDTO> getPendingOrdersWithKeyword(String keyword, Pageable pageable) {
        // Kiểm tra xem có sort theo totalAmount không bằng cách kiểm tra Sort object
        boolean isSortByTotalAmount = pageable.getSort().stream()
            .anyMatch(order -> order.getProperty().equals("totalAmount"));
        
        Page<Orders> orders;
        
        if (isSortByTotalAmount) {
            // Sort theo tổng tiền thực tế với keyword
            boolean isDescending = pageable.getSort().stream()
                .filter(order -> order.getProperty().equals("totalAmount"))
                .anyMatch(order -> order.getDirection().isDescending());
            
            // Lấy tất cả orders với keyword đã sort
            List<Orders> allOrders;
            if (isDescending) {
                allOrders = ordersRepository.findPendingOrdersWithKeywordByTotalAmountDescNative("%" + keyword + "%");
            } else {
                allOrders = ordersRepository.findPendingOrdersWithKeywordByTotalAmountAscNative("%" + keyword + "%");
            }
            
            // Thực hiện pagination thủ công
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), allOrders.size());
            List<Orders> pagedOrders = allOrders.subList(start, end);
            
            // Tạo Page object
            orders = new org.springframework.data.domain.PageImpl<>(pagedOrders, pageable, allOrders.size());
        } else {
            // Sort theo các field khác với keyword
            orders = ordersRepository.findByStatusAndOrderDeliveryIsNullAndKeyword(
                Orders.OrderStatus.CONFIRMED, "%" + keyword + "%", pageable);
        }
        
        
        return orders.map(this::mapToPendingOrderDTO);
    }
    
    @Transactional(readOnly = true)
    public Page<PendingOrderDTO> getPendingOrdersWithZoneAndKeyword(Integer zoneId, String keyword, Pageable pageable) {
        // Kiểm tra xem có sort theo totalAmount không bằng cách kiểm tra Sort object
        boolean isSortByTotalAmount = pageable.getSort().stream()
            .anyMatch(order -> order.getProperty().equals("totalAmount"));
        
        Page<Orders> orders;
        
        if (isSortByTotalAmount) {
            // Sort theo tổng tiền thực tế với zone và keyword
            boolean isDescending = pageable.getSort().stream()
                .filter(order -> order.getProperty().equals("totalAmount"))
                .anyMatch(order -> order.getDirection().isDescending());
            
            // Lấy tất cả orders với zone và keyword đã sort
            List<Orders> allOrders;
            if (isDescending) {
                allOrders = ordersRepository.findPendingOrdersWithZoneAndKeywordByTotalAmountDescNative(zoneId, "%" + keyword + "%");
            } else {
                allOrders = ordersRepository.findPendingOrdersWithZoneAndKeywordByTotalAmountAscNative(zoneId, "%" + keyword + "%");
            }
            
            // Thực hiện pagination thủ công
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), allOrders.size());
            List<Orders> pagedOrders = allOrders.subList(start, end);
            
            // Tạo Page object
            orders = new org.springframework.data.domain.PageImpl<>(pagedOrders, pageable, allOrders.size());
        } else {
            // Sort theo các field khác với zone và keyword
            orders = ordersRepository.findByStatusAndOrderDeliveryIsNullAndZoneAndKeyword(
                Orders.OrderStatus.CONFIRMED, zoneId, "%" + keyword + "%", pageable);
        }
        
        
        return orders.map(this::mapToPendingOrderDTO);
    }
    
    @Transactional(readOnly = true)
    public List<PendingOrderDTO> getPendingOrdersByZone(Integer zoneId) {
        List<Orders> orders = ordersRepository.findByStatusAndAddressCityInZone(
            Orders.OrderStatus.CONFIRMED, zoneId);
        
        return orders.stream()
                .map(this::mapToPendingOrderDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Page<PendingOrderDTO> getAllRecentOrders(Pageable pageable) {
        // Kiểm tra xem có sort theo totalAmount không bằng cách kiểm tra Sort object
        boolean isSortByTotalAmount = pageable.getSort().stream()
            .anyMatch(order -> order.getProperty().equals("totalAmount"));
        
        Page<Orders> orders;
        
        if (isSortByTotalAmount) {
            // Sort theo tổng tiền thực tế
            boolean isDescending = pageable.getSort().stream()
                .filter(order -> order.getProperty().equals("totalAmount"))
                .anyMatch(order -> order.getDirection().isDescending());
            
            // Lấy tất cả orders đã sort
            List<Orders> allOrders;
            if (isDescending) {
                allOrders = ordersRepository.findAllOrderByTotalAmountDescNative();
            } else {
                allOrders = ordersRepository.findAllOrderByTotalAmountAscNative();
            }
            
            // Thực hiện pagination thủ công
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), allOrders.size());
            List<Orders> pagedOrders = allOrders.subList(start, end);
            
            // Tạo Page object
            orders = new org.springframework.data.domain.PageImpl<>(pagedOrders, pageable, allOrders.size());
        } else {
            // Sort theo các field khác
            orders = ordersRepository.findAll(pageable);
        }
        
        return orders.map(this::mapToPendingOrderDTO);
    }
    
    @Transactional(readOnly = true)
    public Page<PendingOrderDTO> getAllRecentOrdersWithKeyword(String keyword, Pageable pageable) {
        // Kiểm tra xem có sort theo totalAmount không bằng cách kiểm tra Sort object
        boolean isSortByTotalAmount = pageable.getSort().stream()
            .anyMatch(order -> order.getProperty().equals("totalAmount"));
        
        Page<Orders> orders;
        
        if (isSortByTotalAmount) {
            // Sort theo tổng tiền thực tế với keyword
            boolean isDescending = pageable.getSort().stream()
                .filter(order -> order.getProperty().equals("totalAmount"))
                .anyMatch(order -> order.getDirection().isDescending());
            
            // Lấy tất cả orders với keyword đã sort
            List<Orders> allOrders;
            if (isDescending) {
                allOrders = ordersRepository.findAllOrdersWithKeywordByTotalAmountDescNative("%" + keyword + "%");
            } else {
                allOrders = ordersRepository.findAllOrdersWithKeywordByTotalAmountAscNative("%" + keyword + "%");
            }
            
            // Thực hiện pagination thủ công
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), allOrders.size());
            List<Orders> pagedOrders = allOrders.subList(start, end);
            
            // Tạo Page object
            orders = new org.springframework.data.domain.PageImpl<>(pagedOrders, pageable, allOrders.size());
        } else {
            // Sort theo các field khác với keyword
            orders = ordersRepository.findByKeyword("%" + keyword + "%", pageable);
        }
        
        return orders.map(this::mapToPendingOrderDTO);
    }
    
    // ===== DELIVERY TEAM MANAGEMENT =====
    
    @Transactional(readOnly = true)
    public List<DeliveryTeamDTO> getAvailableDeliveryTeams(Integer orderId) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));
        
        // Lấy zone từ địa chỉ giao hàng
        String city = order.getAddress().getCity();
        ProvinceZone provinceZone = provinceZoneRepository.findByProvinceName(city)
                .orElseThrow(() -> new RuntimeException("Tỉnh/thành chưa được map vào zone"));
        
        // Lấy các đội giao trong zone
        List<DeliveryTeam> teams = deliveryTeamRepository.findByZoneAndActive(provinceZone.getZone().getId());
        
        return teams.stream()
                .map(team -> mapToDeliveryTeamDTO(team, provinceZone.getZone().getId()))
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<DeliveryTeamDTO> getAllDeliveryTeams() {
        List<DeliveryTeam> teams = deliveryTeamRepository.findAll();
        
        return teams.stream()
                .map(this::mapToDeliveryTeamDTO)
                .collect(Collectors.toList());
    }
    
    // ===== ASSIGNMENT OPERATIONS =====
    
    @Transactional
    public void assignDeliveryTeam(AssignDeliveryTeamRequest request, Integer managerId) {
        Orders order = ordersRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));
        
        // Validate đơn hàng
        if (order.getStatus() != Orders.OrderStatus.CONFIRMED) {
            throw new RuntimeException("Chỉ phân công đơn đã xác nhận");
        }
        
        if (order.getOrderDelivery() != null) {
            throw new RuntimeException("Đơn hàng đã được phân công");
        }
        
        DeliveryTeam team = deliveryTeamRepository.findById(request.getDeliveryTeamId())
                .orElseThrow(() -> new RuntimeException("Đội giao hàng không tồn tại"));
        
        // Validate đội giao hàng
        if (!team.getIsActive()) {
            throw new RuntimeException("Đội giao hàng không hoạt động");
        }
        
        // Validate zone
        validateDeliveryTeamZone(order, team);
        
        // Tạo OrderDelivery
        OrderDelivery orderDelivery = new OrderDelivery();
        orderDelivery.setOrder(order);
        orderDelivery.setDeliveryTeam(team);
        orderDelivery.setStatus(OrderDelivery.DeliveryStatus.IN_TRANSIT);
        orderDelivery.setNote(request.getNote());
        orderDelivery.setUpdatedAt(LocalDateTime.now());
        
        orderDeliveryRepository.save(orderDelivery);
        
        // Cập nhật trạng thái đơn hàng
        order.setStatus(Orders.OrderStatus.DISPATCHED);
        ordersRepository.save(order);
        
        // Ghi DeliveryHistory
        DeliveryHistory history = new DeliveryHistory();
        history.setOrderDelivery(orderDelivery);
        history.setStatus(OrderDelivery.DeliveryStatus.IN_TRANSIT);
        history.setNote("Phân công đội giao: " + team.getName());
        history.setChangedAt(LocalDateTime.now());
        deliveryHistoryRepository.save(history);
        
        // Ghi OrderStatusHistory
        User manager = userRepository.findById(managerId).orElse(null);
        OrderStatusHistory statusHistory = new OrderStatusHistory();
        statusHistory.setOrder(order);
        statusHistory.setStatus(Orders.OrderStatus.DISPATCHED);
        statusHistory.setNote("Phân công đội giao: " + team.getName());
        statusHistory.setChangedBy(manager);
        statusHistory.setChangedAt(LocalDateTime.now());
        orderStatusHistoryRepository.save(statusHistory);
    }
    
    @Transactional
    public void changeDeliveryTeam(ChangeDeliveryTeamRequest request, Integer managerId) {
        Orders order = ordersRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));
        
        OrderDelivery orderDelivery = order.getOrderDelivery();
        if (orderDelivery == null) {
            throw new RuntimeException("Đơn hàng chưa được phân công");
        }
        
        // Validate trạng thái
        if (orderDelivery.getStatus() != OrderDelivery.DeliveryStatus.RECEIVED) {
            throw new RuntimeException("Không thể thay đổi đội giao khi đã bắt đầu giao hàng");
        }
        
        DeliveryTeam newTeam = deliveryTeamRepository.findById(request.getNewDeliveryTeamId())
                .orElseThrow(() -> new RuntimeException("Đội giao hàng mới không tồn tại"));
        
        if (!newTeam.getIsActive()) {
            throw new RuntimeException("Đội giao hàng mới không hoạt động");
        }
        
        // Validate zone
        validateDeliveryTeamZone(order, newTeam);
        
        DeliveryTeam oldTeam = orderDelivery.getDeliveryTeam();
        
        // Cập nhật đội giao
        orderDelivery.setDeliveryTeam(newTeam);
        orderDelivery.setNote(request.getNote());
        orderDelivery.setUpdatedAt(LocalDateTime.now());
        orderDeliveryRepository.save(orderDelivery);
        
        // Ghi DeliveryHistory
        DeliveryHistory history = new DeliveryHistory();
        history.setOrderDelivery(orderDelivery);
        history.setStatus(OrderDelivery.DeliveryStatus.IN_TRANSIT);
        history.setNote("Thay đổi đội giao từ " + oldTeam.getName() + " sang " + newTeam.getName() + 
                       ". Lý do: " + request.getReason());
        history.setChangedAt(LocalDateTime.now());
        deliveryHistoryRepository.save(history);
        
        // Ghi OrderStatusHistory
        User manager = userRepository.findById(managerId).orElse(null);
        OrderStatusHistory statusHistory = new OrderStatusHistory();
        statusHistory.setOrder(order);
        statusHistory.setStatus(Orders.OrderStatus.DISPATCHED);
        statusHistory.setNote("Thay đổi đội giao từ " + oldTeam.getName() + " sang " + newTeam.getName() + 
                              ". Lý do: " + request.getReason());
        statusHistory.setChangedBy(manager);
        statusHistory.setChangedAt(LocalDateTime.now());
        orderStatusHistoryRepository.save(statusHistory);
    }
    
    // ===== ZONE MANAGEMENT =====
    
    @Transactional(readOnly = true)
    public List<ZoneDTO> getAllZones() {
        List<ShippingZone> zones = shippingZoneRepository.findAll();
        
        return zones.stream()
                .map(this::mapToZoneDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public String getOrderZone(Integer orderId) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));
        
        String city = order.getAddress().getCity();
        ProvinceZone provinceZone = provinceZoneRepository.findByProvinceName(city)
                .orElse(null);
        
        return provinceZone != null ? provinceZone.getZone().getName() : "Chưa xác định";
    }
    
    @Transactional(readOnly = true)
    public PendingOrderDTO getOrderDetails(Integer orderId) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));
        
        return mapToPendingOrderDTO(order);
    }
    
    // ===== HELPER METHODS =====
    
    private void validateDeliveryTeamZone(Orders order, DeliveryTeam team) {
        String city = order.getAddress().getCity();
        ProvinceZone provinceZone = provinceZoneRepository.findByProvinceName(city)
                .orElseThrow(() -> new RuntimeException("Địa chỉ chưa được map vào khu vực giao hàng"));
        
        boolean teamInZone = deliveryTeamZoneRepository.existsByDeliveryTeamAndZone(team, provinceZone.getZone());
        if (!teamInZone) {
            throw new RuntimeException("Đội giao không phụ trách khu vực này");
        }
    }
    
    private PendingOrderDTO mapToPendingOrderDTO(Orders order) {
        if (order == null || order.getId() == null) {
            throw new RuntimeException("Đơn hàng không hợp lệ");
        }
        
        PendingOrderDTO dto = new PendingOrderDTO();
        dto.setId(order.getId());
        dto.setCustomerName(order.getUser() != null ? order.getUser().getFullName() : "N/A");
        dto.setCustomerPhone(order.getUser() != null ? order.getUser().getPhone() : "");
        dto.setDeliveryAddress(order.getAddress() != null ? order.getAddress().getAddressLine() : "");
        dto.setCity(order.getAddress() != null ? order.getAddress().getCity() : "");
        dto.setDistrict(order.getAddress() != null ? order.getAddress().getDistrict() : "");
        dto.setCreatedAt(order.getCreatedAt());
        dto.setPaymentMethod(order.getPaymentMethod() != null ? order.getPaymentMethod().name() : "");
        dto.setPaymentStatus(order.getPaymentStatus() != null ? order.getPaymentStatus().name() : "");
        dto.setOrderStatus(order.getStatus() != null ? order.getStatus().name() : "");
        dto.setItemCount(order.getOrderItems() != null ? order.getOrderItems().size() : 0);
        
        // Tính tổng tiền đơn hàng
        BigDecimal total = BigDecimal.ZERO;
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            total = order.getOrderItems().stream()
                    .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQty())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        dto.setOrderTotal(total.add(order.getShippingFee() != null ? order.getShippingFee() : BigDecimal.ZERO));
        
        // Lấy zone
        String zoneName = getOrderZone(order.getId());
        dto.setZoneName(zoneName);
        
        return dto;
    }
    
    private DeliveryTeamDTO mapToDeliveryTeamDTO(DeliveryTeam team) {
        DeliveryTeamDTO dto = new DeliveryTeamDTO();
        dto.setId(team.getId());
        dto.setName(team.getName());
        dto.setPhone(team.getPhone());
        dto.setIsActive(team.getIsActive());
        dto.setUserName(team.getUser().getFullName());
        dto.setUserEmail(team.getUser().getEmail());
        
        // Lấy danh sách zone
        List<String> zoneNames = team.getDeliveryTeamZones().stream()
                .map(dtz -> dtz.getZone().getName())
                .collect(Collectors.toList());
        dto.setZoneNames(zoneNames);
        dto.setZoneCoverage(String.join(", ", zoneNames));
        
        // Đếm số đơn đang xử lý
        int currentOrderCount = orderDeliveryRepository.countByDeliveryTeamAndStatusIn(
                team, List.of(OrderDelivery.DeliveryStatus.RECEIVED, OrderDelivery.DeliveryStatus.IN_TRANSIT));
        dto.setCurrentOrderCount(currentOrderCount);
        
        return dto;
    }
    
    private DeliveryTeamDTO mapToDeliveryTeamDTO(DeliveryTeam team, Integer zoneId) {
        DeliveryTeamDTO dto = mapToDeliveryTeamDTO(team);
        
        // Lọc chỉ zone hiện tại
        List<String> zoneNames = team.getDeliveryTeamZones().stream()
                .filter(dtz -> dtz.getZone().getId().equals(zoneId))
                .map(dtz -> dtz.getZone().getName())
                .collect(Collectors.toList());
        dto.setZoneNames(zoneNames);
        dto.setZoneCoverage(String.join(", ", zoneNames));
        
        return dto;
    }
    
    private ZoneDTO mapToZoneDTO(ShippingZone zone) {
        ZoneDTO dto = new ZoneDTO();
        dto.setId(zone.getId());
        dto.setName(zone.getName());
        dto.setSlug(zone.getSlug());
        
        if (zone.getShippingFee() != null) {
            dto.setBaseFee(zone.getShippingFee().getBaseFee());
        }
        
        List<String> provinceNames = zone.getProvinceZones().stream()
                .map(pz -> pz.getProvinceName())
                .collect(Collectors.toList());
        dto.setProvinceNames(provinceNames);
        
        int deliveryTeamCount = deliveryTeamZoneRepository.countByZone(zone);
        dto.setDeliveryTeamCount(deliveryTeamCount);
        
        boolean hasActiveTeams = deliveryTeamZoneRepository.existsByZoneAndDeliveryTeamIsActive(zone, true);
        dto.setHasActiveTeams(hasActiveTeams);
        
        return dto;
    }
}
