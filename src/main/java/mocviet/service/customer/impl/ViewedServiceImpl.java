package mocviet.service.customer.impl;

import lombok.RequiredArgsConstructor;
import mocviet.dto.customer.ViewedItemDTO;
import mocviet.entity.Product;
import mocviet.entity.User;
import mocviet.entity.Viewed;
import mocviet.repository.ProductImageRepository;
import mocviet.repository.ProductRepository;
import mocviet.repository.ViewedRepository;
import mocviet.service.UserDetailsServiceImpl;
import mocviet.service.customer.IViewedService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ViewedServiceImpl implements IViewedService {

    private final ViewedRepository viewedRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository imageRepository;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    @Transactional
    public void recordViewBySlug(String productSlug) {
        User currentUser = userDetailsService.getCurrentUser();
        if (currentUser == null) return; // chỉ lưu cho user đăng nhập

        Optional<Product> productOpt = productRepository.findBySlug(productSlug);
        if (productOpt.isEmpty()) return;
        Product product = productOpt.get();

        // Nếu đã có bản ghi, chỉ cập nhật thời gian để đẩy lên đầu
        Optional<Viewed> existed = viewedRepository.findByUserAndProduct(currentUser, product);
        if (existed.isPresent()) {
            Viewed v = existed.get();
            v.setViewedAt(java.time.LocalDateTime.now());
            viewedRepository.save(v);
        } else {
            Viewed v = new Viewed();
            v.setUser(currentUser);
            v.setProduct(product);
            v.setViewedAt(java.time.LocalDateTime.now());
            viewedRepository.save(v);
        }

        // Giới hạn còn 20: nếu tổng > 20 thì xóa các bản ghi cũ hơn (offset >= 20)
        long total = viewedRepository.countByUser(currentUser);
        if (total > 20) {
            int toDelete = (int)(total - 20);
            var page = viewedRepository.findByUserOrderByViewedAtDesc(currentUser, PageRequest.of(1, toDelete));
            if (!page.isEmpty()) {
                viewedRepository.deleteAll(page.getContent());
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewedItemDTO> getRecentViewedForCurrentUser(int limit) {
        User currentUser = userDetailsService.getCurrentUser();
        if (currentUser == null) return List.of();

        return viewedRepository.findByUserOrderByViewedAtDesc(currentUser, PageRequest.of(0, limit))
                .getContent()
                .stream()
                .map(v -> {
                    ViewedItemDTO dto = new ViewedItemDTO();
                    dto.setProductId(v.getProduct().getId());
                    dto.setProductName(v.getProduct().getName());
                    dto.setProductSlug(v.getProduct().getSlug());
                    dto.setViewedAt(v.getViewedAt());

                    // Lấy thumbnail thực tế: ưu tiên ảnh có tên chứa "/00_" nếu có, fallback placeholder tĩnh
                    var images = imageRepository.findByProductId(v.getProduct().getId());
                    String thumb = images.stream()
                            .sorted(Comparator.comparing(img -> img.getUrl().contains("/00_") ? 0 : 1))
                            .map(img -> img.getUrl())
                            .findFirst()
                            .orElse("/images/products/placeholder.jpg");
                    dto.setThumbnailUrl(thumb);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}


