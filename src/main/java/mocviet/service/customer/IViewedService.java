package mocviet.service.customer;

import mocviet.dto.customer.ViewedItemDTO;

import java.util.List;

public interface IViewedService {

    void recordViewBySlug(String productSlug);

    List<ViewedItemDTO> getRecentViewedForCurrentUser(int limit);
}


