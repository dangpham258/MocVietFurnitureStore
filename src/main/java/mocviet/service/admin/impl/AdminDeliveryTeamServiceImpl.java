package mocviet.service.admin.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mocviet.dto.admin.AvailableUserResponse;
import mocviet.dto.admin.DeliveryTeamCreateRequest;
import mocviet.dto.admin.DeliveryTeamResponse;
import mocviet.dto.admin.DeliveryTeamUpdateRequest;
import mocviet.dto.admin.ZoneInfoResponse;
import mocviet.dto.admin.ZoneMappingRequest;
import mocviet.entity.DeliveryTeam;
import mocviet.entity.DeliveryTeamZone;
import mocviet.entity.ShippingZone;
import mocviet.entity.User;
import mocviet.repository.DeliveryTeamRepository;
import mocviet.repository.DeliveryTeamZoneRepository;
import mocviet.repository.RoleRepository;
import mocviet.repository.ShippingZoneRepository;
import mocviet.repository.UserRepository;
import mocviet.service.admin.AdminDeliveryTeamService;

@Service
@RequiredArgsConstructor
public class AdminDeliveryTeamServiceImpl implements AdminDeliveryTeamService {

    private final DeliveryTeamRepository teamRepository;
    private final DeliveryTeamZoneRepository mappingRepository;
    private final ShippingZoneRepository zoneRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryTeamResponse> getAllTeams() {
        List<DeliveryTeam> teams = teamRepository.findAll();
        List<DeliveryTeamResponse> responses = new ArrayList<>();

        for (DeliveryTeam team : teams) {
            DeliveryTeamResponse response = new DeliveryTeamResponse();
            response.setId(team.getId());
            response.setName(team.getName());
            response.setPhone(team.getPhone());
            response.setIsActive(team.getIsActive());

            // Lấy thông tin người dùng
            if (team.getUser() != null) {
                response.setUserId(team.getUser().getId());
                response.setUserName(team.getUser().getFullName());
                response.setUserEmail(team.getUser().getEmail());
            }

            // Lấy khu vực
            List<DeliveryTeamResponse.ZoneInfo> zones = new ArrayList<>();
            if (team.getDeliveryTeamZones() != null) {
                for (DeliveryTeamZone mapping : team.getDeliveryTeamZones()) {
                    DeliveryTeamResponse.ZoneInfo zoneInfo = new DeliveryTeamResponse.ZoneInfo();
                    zoneInfo.setMappingId(mapping.getId());
                    if (mapping.getZone() != null) {
                        zoneInfo.setId(mapping.getZone().getId());
                        zoneInfo.setName(mapping.getZone().getName());
                    }
                    zones.add(zoneInfo);
                }
            }
            response.setZones(zones);

            responses.add(response);
        }

        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ZoneInfoResponse> getAllZones() {
        List<ShippingZone> zones = zoneRepository.findAll();
        List<ZoneInfoResponse> responses = new ArrayList<>();

        for (ShippingZone zone : zones) {
            ZoneInfoResponse response = new ZoneInfoResponse();
            response.setId(zone.getId());
            response.setName(zone.getName());
            response.setSlug(zone.getSlug());
            responses.add(response);
        }

        return responses;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvailableUserResponse> getAvailableUsers() {
        // Lấy tất cả người dùng có vai trò DELIVERY và chưa được gán vào đội
        var deliveryRole = roleRepository.findByName("DELIVERY").orElse(null);
        if (deliveryRole == null) {
            return new ArrayList<>();
        }

        List<User> allUsers = userRepository.findAll();
        List<Integer> assignedUserIds = teamRepository.findAll().stream()
                .filter(team -> team.getUser() != null)
                .map(team -> team.getUser().getId())
                .toList();

        List<AvailableUserResponse> responses = new ArrayList<>();
        for (User user : allUsers) {
            if (user.getRole() != null && user.getRole().getId().equals(deliveryRole.getId()) &&
                !assignedUserIds.contains(user.getId())) {
                AvailableUserResponse response = new AvailableUserResponse();
                response.setId(user.getId());
                response.setName(user.getFullName());
                response.setEmail(user.getEmail());
                responses.add(response);
            }
        }

        return responses;
    }

    @Override
    @Transactional
    public DeliveryTeamResponse createTeam(DeliveryTeamCreateRequest request) {
        // Kiểm tra xem người dùng có tồn tại và có vai trò DELIVERY
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == null || !"DELIVERY".equals(user.getRole().getName())) {
            throw new RuntimeException("User phải có vai trò DELIVERY");
        }

        // Kiểm tra xem người dùng đã được gán vào đội
        if (teamRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new RuntimeException("User đã được gán vào đội khác");
        }

        DeliveryTeam team = new DeliveryTeam();
        team.setName(request.getName());
        team.setPhone(request.getPhone());
        team.setIsActive(request.getIsActive());
        team.setUser(user);

        team = teamRepository.save(team);

        return convertToResponse(team);
    }

    @Override
    @Transactional
    public DeliveryTeamResponse updateTeam(Integer teamId, DeliveryTeamUpdateRequest request) {
        DeliveryTeam team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        // Cập nhật thông tin cơ bản
        team.setName(request.getName());
        team.setPhone(request.getPhone());
        team.setIsActive(request.getIsActive());

        // Kiểm tra xem người dùng được thay đổi
        if (team.getUser() == null || !team.getUser().getId().equals(request.getUserId())) {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (user.getRole() == null || !"DELIVERY".equals(user.getRole().getName())) {
                throw new RuntimeException("User phải có vai trò DELIVERY");
            }

            // Kiểm tra xem người dùng mới đã được gán vào đội khác
            teamRepository.findByUserId(request.getUserId()).ifPresent(otherTeam -> {
                if (!otherTeam.getId().equals(teamId)) {
                    throw new RuntimeException("User đã được gán vào đội khác");
                }
            });

            team.setUser(user);
        }

        team = teamRepository.save(team);

        return convertToResponse(team);
    }

    @Override
    @Transactional
    public void toggleTeamStatus(Integer teamId) {
        DeliveryTeam team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        team.setIsActive(!team.getIsActive());
        teamRepository.save(team);
    }

    @Override
    @Transactional
    public void addZoneToTeam(Integer teamId, ZoneMappingRequest request) {
        DeliveryTeam team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        ShippingZone zone = zoneRepository.findById(request.getZoneId())
                .orElseThrow(() -> new RuntimeException("Zone not found"));

        // Kiểm tra xem mapping đã tồn tại
        if (mappingRepository.findByDeliveryTeamIdAndZoneId(teamId, request.getZoneId()).isPresent()) {
            throw new RuntimeException("Đội đã phục vụ khu vực này");
        }

        DeliveryTeamZone mapping = new DeliveryTeamZone();
        mapping.setDeliveryTeam(team);
        mapping.setZone(zone);

        mappingRepository.save(mapping);
    }

    @Override
    @Transactional
    public void removeZoneFromTeam(Integer mappingId) {
        DeliveryTeamZone mapping = mappingRepository.findById(mappingId)
                .orElseThrow(() -> new RuntimeException("Mapping not found"));

        mappingRepository.delete(mapping);
    }

    private DeliveryTeamResponse convertToResponse(DeliveryTeam team) {
        DeliveryTeamResponse response = new DeliveryTeamResponse();
        response.setId(team.getId());
        response.setName(team.getName());
        response.setPhone(team.getPhone());
        response.setIsActive(team.getIsActive());

        if (team.getUser() != null) {
            response.setUserId(team.getUser().getId());
            response.setUserName(team.getUser().getFullName());
            response.setUserEmail(team.getUser().getEmail());
        }

        List<DeliveryTeamResponse.ZoneInfo> zones = new ArrayList<>();
        if (team.getDeliveryTeamZones() != null) {
            for (DeliveryTeamZone mapping : team.getDeliveryTeamZones()) {
                DeliveryTeamResponse.ZoneInfo zoneInfo = new DeliveryTeamResponse.ZoneInfo();
                zoneInfo.setMappingId(mapping.getId());
                if (mapping.getZone() != null) {
                    zoneInfo.setId(mapping.getZone().getId());
                    zoneInfo.setName(mapping.getZone().getName());
                }
                zones.add(zoneInfo);
            }
        }
        response.setZones(zones);

        return response;
    }
}

