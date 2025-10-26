package mocviet.service.admin;

import mocviet.dto.admin.*;

import java.util.List;

public interface AdminShippingService {
    
    List<ZoneResponse> getAllZones();
    
    List<ProvinceResponse> getAllProvinces();
    
    List<MappingResponse> getAllMappings();
    
    ZoneResponse updateShippingFee(Integer zoneId, ShippingFeeUpdateRequest request);
    
    MappingResponse addProvinceMapping(ProvinceMappingRequest request);
    
    void removeProvinceMapping(Integer mappingId);
}

