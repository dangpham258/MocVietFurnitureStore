package mocviet.dto.admin;

import lombok.Data;

@Data
public class ShowroomResponse {
    private Integer id;
    private String name;
    private String address;
    private String city;
    private String district;
    private String email;
    private String phone;
    private String openHours;
    private String mapEmbed;
    private Boolean isActive;
    private String createdAt;
}

