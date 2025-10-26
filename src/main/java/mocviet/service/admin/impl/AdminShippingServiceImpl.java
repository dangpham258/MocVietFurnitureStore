package mocviet.service.admin.impl;

import lombok.RequiredArgsConstructor;
import mocviet.dto.admin.*;
import mocviet.entity.ProvinceZone;
import mocviet.entity.ShippingFee;
import mocviet.entity.ShippingZone;
import mocviet.repository.ProvinceZoneRepository;
import mocviet.repository.ShippingFeeRepository;
import mocviet.repository.ShippingZoneRepository;
import mocviet.service.admin.AdminShippingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminShippingServiceImpl implements AdminShippingService {
    
    private final ShippingZoneRepository zoneRepository;
    private final ShippingFeeRepository feeRepository;
    private final ProvinceZoneRepository mappingRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<ZoneResponse> getAllZones() {
        List<ShippingZone> zones = zoneRepository.findAll();
        List<ZoneResponse> responses = new ArrayList<>();
        
        for (ShippingZone zone : zones) {
            ZoneResponse response = new ZoneResponse();
            response.setId(zone.getId());
            response.setName(zone.getName());
            response.setSlug(zone.getSlug());
            
            // Get shipping fee for this zone
            if (zone.getShippingFee() != null) {
                response.setBaseFee(zone.getShippingFee().getBaseFee());
            } else {
                response.setBaseFee(java.math.BigDecimal.ZERO);
            }
            
            responses.add(response);
        }
        
        return responses;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ProvinceResponse> getAllProvinces() {
        // Get all unique province names from ProvinceZone
        List<String> provinceNames = mappingRepository.findAll().stream()
                .map(ProvinceZone::getProvinceName)
                .distinct()
                .collect(Collectors.toList());
        
        List<ProvinceResponse> responses = new ArrayList<>();
        for (int i = 0; i < provinceNames.size(); i++) {
            ProvinceResponse response = new ProvinceResponse();
            response.setId(i + 1);
            response.setName(provinceNames.get(i));
            responses.add(response);
        }
        
        return responses;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MappingResponse> getAllMappings() {
        List<ProvinceZone> mappings = mappingRepository.findAll();
        List<MappingResponse> responses = new ArrayList<>();
        
        for (ProvinceZone mapping : mappings) {
            MappingResponse response = new MappingResponse();
            response.setId(mapping.getId());
            response.setZoneId(mapping.getZone().getId());
            
            // Get province ID from name (simple indexing for now)
            String provinceName = mapping.getProvinceName();
            List<ProvinceResponse> allProvinces = getAllProvinces();
            ProvinceResponse province = allProvinces.stream()
                    .filter(p -> p.getName().equals(provinceName))
                    .findFirst()
                    .orElse(null);
            
            response.setProvinceId(province != null ? province.getId() : null);
            
            responses.add(response);
        }
        
        return responses;
    }
    
    @Override
    @Transactional
    public ZoneResponse updateShippingFee(Integer zoneId, ShippingFeeUpdateRequest request) {
        ShippingZone zone = zoneRepository.findById(zoneId)
                .orElseThrow(() -> new RuntimeException("Zone not found"));
        
        // Check if shipping fee exists for this zone
        ShippingFee fee = feeRepository.findByZoneId(zoneId).orElse(null);
        
        if (fee == null) {
            // Create new shipping fee
            fee = new ShippingFee();
            fee.setZone(zone);
            fee.setBaseFee(request.getBaseFee());
        } else {
            // Update existing shipping fee
            fee.setBaseFee(request.getBaseFee());
        }
        
        fee = feeRepository.save(fee);
        
        // Return updated zone response
        ZoneResponse response = new ZoneResponse();
        response.setId(zone.getId());
        response.setName(zone.getName());
        response.setSlug(zone.getSlug());
        response.setBaseFee(fee.getBaseFee());
        
        return response;
    }
    
    @Override
    @Transactional
    public MappingResponse addProvinceMapping(ProvinceMappingRequest request) {
        ShippingZone zone = zoneRepository.findById(request.getZoneId())
                .orElseThrow(() -> new RuntimeException("Zone not found"));
        
        // Check if province already exists
        if (mappingRepository.findByProvinceName(request.getProvinceName()).isPresent()) {
            throw new RuntimeException("Tỉnh/thành đã được map vào miền khác");
        }
        
        ProvinceZone mapping = new ProvinceZone();
        mapping.setProvinceName(request.getProvinceName());
        mapping.setZone(zone);
        
        mapping = mappingRepository.save(mapping);
        
        MappingResponse response = new MappingResponse();
        response.setId(mapping.getId());
        response.setZoneId(mapping.getZone().getId());
        response.setProvinceId(null); // Will be set on load
        
        return response;
    }
    
    @Override
    @Transactional
    public void removeProvinceMapping(Integer mappingId) {
        ProvinceZone mapping = mappingRepository.findById(mappingId)
                .orElseThrow(() -> new RuntimeException("Mapping not found"));
        
        mappingRepository.delete(mapping);
    }
}

