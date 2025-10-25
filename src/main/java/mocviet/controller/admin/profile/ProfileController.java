package mocviet.controller.admin.profile;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import mocviet.dto.admin.PasswordChangeRequest;
import mocviet.dto.admin.ProfileResponse;
import mocviet.dto.admin.ProfileUpdateRequest;
import mocviet.entity.User;
import mocviet.service.admin.AdminProfileService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/profile")
@RequiredArgsConstructor
public class ProfileController {
    
    private final AdminProfileService profileService;
    
    @GetMapping({"", "/"})
    @PreAuthorize("hasRole('ADMIN')")
    public String profile(Model model, HttpServletRequest request, Authentication authentication) {
        model.addAttribute("pageTitle", "Thông tin cá nhân");
        model.addAttribute("activeMenu", "profile");
        
        // Lấy thông tin user hiện tại
        User currentUser = (User) authentication.getPrincipal();
        ProfileResponse userProfile = profileService.getCurrentProfile(currentUser);
        model.addAttribute("user", userProfile);
        
        // Thêm các form objects với dữ liệu hiện tại
        ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest();
        profileUpdateRequest.setEmail(userProfile.getEmail());
        profileUpdateRequest.setFullName(userProfile.getFullName());
        profileUpdateRequest.setPhone(userProfile.getPhone());
        profileUpdateRequest.setGender(userProfile.getGender());
        profileUpdateRequest.setDob(userProfile.getDob());
        
        // Format DOB for HTML5 date input (yyyy-MM-dd)
        if (userProfile.getDob() != null) {
            model.addAttribute("formattedDob", userProfile.getDob().toString());
        } else {
            model.addAttribute("formattedDob", "");
        }
        
        model.addAttribute("profileUpdateRequest", profileUpdateRequest);
        model.addAttribute("passwordChangeRequest", new PasswordChangeRequest());
        
        // Check if it's an AJAX request
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return "admin/profile/admin_profile ::content";
        }
        
        return "admin/profile/admin_profile";
    }
}