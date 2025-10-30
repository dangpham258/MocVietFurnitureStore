package mocviet.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import lombok.RequiredArgsConstructor;
import mocviet.entity.SocialLink;
import mocviet.repository.SocialLinkRepository;
import mocviet.service.customer.ICartService;
import mocviet.service.customer.IWishlistService;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {

    private final SocialLinkRepository socialLinkRepository;
    private final ICartService cartService;
    private final IWishlistService wishlistService;

    @ModelAttribute
    public void addGlobalSocialLinks(Model model) {
        List<SocialLink> all = socialLinkRepository.findAll();
        Map<String, String> links = new HashMap<>();
        for (SocialLink link : all) {
            if (link.getPlatform() == null || link.getUrl() == null || link.getUrl().isBlank()) continue;
            String key = link.getPlatform().toLowerCase();
            String url = normalizeUrl(link.getUrl());
            // Lấy các link facebook, zalo, youtube
            if (key.equals("facebook") || key.equals("zalo") || key.equals("youtube")) {
                links.put(key, url);
            }
        }
        model.addAttribute("socialLinks", links);
    }

    @ModelAttribute
    public void addGlobalCounts(Model model) {
        try {
            int cartCount = cartService.getCartItemCount();
            model.addAttribute("globalCartCount", cartCount);
        } catch (Exception e) {
            model.addAttribute("globalCartCount", 0);
        }

        try {
            int wishlistCount = wishlistService.getWishlistCount();
            model.addAttribute("globalWishlistCount", wishlistCount);
        } catch (Exception e) {
            model.addAttribute("globalWishlistCount", 0);
        }
    }

    private String normalizeUrl(String url) {
        String trimmed = url.trim();
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return trimmed;
        }
        return "http://" + trimmed;
    }
}


