package mocviet.dto.admin;

import java.util.List;

import lombok.Data;

@Data
public class DeliveryTeamResponse {
    private Integer id;
    private String name;
    private String phone;
    private Boolean isActive;
    private Integer userId;
    private String userName;
    private String userEmail;
    private List<ZoneInfo> zones;

    @Data
    public static class ZoneInfo {
        private Integer id;
        private String name;
        private Integer mappingId;
    }
}

