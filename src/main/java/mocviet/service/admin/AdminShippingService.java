package mocviet.service.admin;

import java.util.List;

import mocviet.dto.admin.MappingResponse;
import mocviet.dto.admin.ProvinceMappingRequest;
import mocviet.dto.admin.ProvinceResponse;
import mocviet.dto.admin.ShippingFeeUpdateRequest;
import mocviet.dto.admin.ZoneResponse;

public interface AdminShippingService {

    List<ZoneResponse> getAllZones();

    List<ProvinceResponse> getAllProvinces();

    List<MappingResponse> getAllMappings();

    ZoneResponse updateShippingFee(Integer zoneId, ShippingFeeUpdateRequest request);

    MappingResponse addProvinceMapping(ProvinceMappingRequest request);

    void removeProvinceMapping(Integer mappingId);
}

