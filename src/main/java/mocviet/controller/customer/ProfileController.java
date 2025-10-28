package mocviet.controller.customer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mocviet.dto.AddressRequest;
import mocviet.dto.PasswordChangeRequest;
import mocviet.dto.ProfileUpdateRequest;
import mocviet.entity.Address;
import mocviet.entity.User;
import mocviet.repository.AddressRepository;
import mocviet.service.customer.IProfileService;
import mocviet.service.customer.ICustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/customer/profile")
@RequiredArgsConstructor
public class ProfileController {
    
    private final IProfileService profileService;
    private final ICustomerService customerService;
    private final AddressRepository addressRepository;
    
    @GetMapping
    public String profilePage(@RequestParam(value = "error", required = false) String error,
                             Model model) {
        User user = profileService.getCurrentUserProfile();
        List<Address> addresses = profileService.getUserAddresses();
        
        // Populate ProfileUpdateRequest with current user data
        ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest();
        profileUpdateRequest.setFullName(user.getFullName());
        profileUpdateRequest.setEmail(user.getEmail());
        profileUpdateRequest.setGender(user.getGender());
        profileUpdateRequest.setDob(user.getDob());
        profileUpdateRequest.setPhone(user.getPhone());
        
        // Handle error message
        if (error != null) {
            switch (error) {
                case "no_address":
                    model.addAttribute("error", "Bạn chưa có địa chỉ nhận hàng. Vui lòng thêm địa chỉ để tiếp tục thanh toán.");
                    break;
                default:
                    model.addAttribute("error", "Có lỗi xảy ra");
            }
        }
        
        model.addAttribute("user", user);
        model.addAttribute("addresses", addresses);
        model.addAttribute("profileUpdateRequest", profileUpdateRequest);
        model.addAttribute("passwordChangeRequest", new PasswordChangeRequest());
        model.addAttribute("addressRequest", new AddressRequest());
        model.addAttribute("provinces", customerService.getAllProvinces());
        
        return "customer/profile";
    }
    
    @PostMapping("/update")
    public String updateProfile(@Valid @ModelAttribute ProfileUpdateRequest request,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               Model model,
                               @RequestParam(value = "dob", required = false) String dobString) {
        
        // Convert dob string to LocalDate if provided
        if (dobString != null && !dobString.trim().isEmpty()) {
            try {
                request.setDob(java.time.LocalDate.parse(dobString));
            } catch (Exception e) {
                bindingResult.rejectValue("dob", "dob.invalid", "Ngày sinh không hợp lệ");
            }
        }
        
        if (bindingResult.hasErrors()) {
            // Giữ lại dữ liệu và hiển thị lỗi validation
            User user = profileService.getCurrentUserProfile();
            List<Address> addresses = profileService.getUserAddresses();
            
            model.addAttribute("user", user);
            model.addAttribute("addresses", addresses);
            model.addAttribute("profileUpdateRequest", request);
            model.addAttribute("passwordChangeRequest", new PasswordChangeRequest());
            model.addAttribute("addressRequest", new AddressRequest());
            
            return "customer/profile";
        }
        
        try {
            profileService.updateProfile(request);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/customer/profile";
    }
    
    @PostMapping("/change-password")
    public String changePassword(@Valid @ModelAttribute PasswordChangeRequest request,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng nhập đầy đủ thông tin");
            return "redirect:/customer/profile";
        }
        
        try {
            profileService.changePassword(request);
            redirectAttributes.addFlashAttribute("success", "Thay đổi mật khẩu thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/customer/profile";
    }
    
    // AJAX endpoint cho thay đổi mật khẩu
    @PostMapping("/change-password-ajax")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> changePasswordAjax(@RequestBody PasswordChangeRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            profileService.changePassword(request);
            response.put("success", true);
            response.put("message", "Thay đổi mật khẩu thành công");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            response.put("field", "currentPassword"); // Chỉ định field nào có lỗi
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Có lỗi xảy ra. Vui lòng thử lại sau");
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @PostMapping("/address/add")
    public String addAddress(@Valid @ModelAttribute AddressRequest request,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng nhập đầy đủ thông tin địa chỉ");
            return "redirect:/customer/profile";
        }
        
        try {
            profileService.addAddress(request);
            redirectAttributes.addFlashAttribute("success", "Thêm địa chỉ thành công");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể thêm địa chỉ lúc này. Vui lòng thử lại sau");
        }
        
        return "redirect:/customer/profile";
    }
    
    @PostMapping("/address/{id}/update")
    public String updateAddress(@PathVariable Integer id,
                                @Valid @ModelAttribute AddressRequest request,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng nhập đầy đủ thông tin địa chỉ");
            return "redirect:/customer/profile";
        }
        
        try {
            profileService.updateAddress(id, request);
            redirectAttributes.addFlashAttribute("success", "Cập nhật địa chỉ thành công");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể sửa địa chỉ lúc này. Vui lòng thử lại sau");
        }
        
        return "redirect:/customer/profile";
    }
    
    @PostMapping("/address/{id}/delete")
    public String deleteAddress(@PathVariable Integer id,
                                RedirectAttributes redirectAttributes) {
        try {
            profileService.deleteAddress(id);
            redirectAttributes.addFlashAttribute("success", "Xóa địa chỉ thành công");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa địa chỉ lúc này. Vui lòng thử lại sau");
        }
        
        return "redirect:/customer/profile";
    }
    
    @PostMapping("/address/{id}/set-default")
    public String setDefaultAddress(@PathVariable Integer id,
                                   RedirectAttributes redirectAttributes) {
        try {
            profileService.setDefaultAddress(id);
            redirectAttributes.addFlashAttribute("success", "Đặt địa chỉ mặc định thành công");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/customer/profile";
    }
    
    // AJAX endpoint để lấy thông tin địa chỉ cho edit modal
    @GetMapping("/address/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAddress(@PathVariable Integer id) {
        try {
            User currentUser = profileService.getCurrentUserProfile();
            
            // Sử dụng repository để kiểm tra ownership và lấy address
            Address address = addressRepository.findByIdAndUserId(id, currentUser.getId())
                    .orElse(null);
            
            if (address == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Tạo Map để tránh vấn đề lazy loading và circular reference
            Map<String, Object> addressData = new HashMap<>();
            addressData.put("id", address.getId());
            addressData.put("receiverName", address.getReceiverName());
            addressData.put("phone", address.getPhone());
            addressData.put("addressLine", address.getAddressLine());
            addressData.put("city", address.getCity());
            addressData.put("district", address.getDistrict());
            addressData.put("isDefault", address.getIsDefault());
            
            return ResponseEntity.ok(addressData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
