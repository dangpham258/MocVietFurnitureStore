package mocviet.service.customer.impl;

import lombok.RequiredArgsConstructor;
import mocviet.entity.ProvinceZone;
import mocviet.repository.ProvinceZoneRepository;
import mocviet.service.customer.ICustomerService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements ICustomerService {
    
    private final ProvinceZoneRepository provinceZoneRepository;
    
    @Override
    public List<ProvinceZone> getAllProvinces() {
        return provinceZoneRepository.findAllByOrderByProvinceNameAsc();
    }
    
}
