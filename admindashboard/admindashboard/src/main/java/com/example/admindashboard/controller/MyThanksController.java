package com.example.admindashboard.controller;

import com.example.admindashboard.model.ThanksCartItem;
import com.example.admindashboard.model.User;
import com.example.admindashboard.repository.ThanksCartItemRepository;
import com.example.admindashboard.repository.UserRepository;
import com.example.admindashboard.service.ThanksService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/my-thanks")
public class MyThanksController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ThanksService thanksService;

    @Autowired
    private ThanksCartItemRepository thanksCartItemRepository;

    /* ---------- SESSION CHECK ---------- */
    private boolean isThanksAuthenticated(HttpSession session) {
        return Boolean.TRUE.equals(session.getAttribute("thanksAuthenticated"));
    }

    private User getAuthenticatedUser(HttpSession session, Principal principal) {
        String username = (String) session.getAttribute("thanksUser");

        if (username == null && principal != null) {
            username = principal.getName();
        }

        if (username != null) {
            return userRepository.findByUsername(username).orElse(null);
        }

        return null;
    }

    @ModelAttribute("cartSize")
    public int getCartSize(HttpSession session, Principal principal) {
        User user = getAuthenticatedUser(session, principal);
        if (user != null) {
            return thanksCartItemRepository.findByUser(user).size();
        }
        return 0;
    }

    @ModelAttribute("notificationCount")
    public int getNotificationCount(HttpSession session, Principal principal) {
        User user = getAuthenticatedUser(session, principal);
        if (user != null) {
            return thanksService.getUnreadNotificationCount(user);
        }
        return 0;
    }

    /* ---------- LOGIN ---------- */
    @GetMapping("/login")
    public String showLogin() {
        return "my-thanks/login";
    }

    /* ---------- DASHBOARD ---------- */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/my-thanks/login";
        }

        User user = getAuthenticatedUser(session, principal);

        model.addAttribute("user", user);
        model.addAttribute("wallet", thanksService.getOrCreateWallet(user));
        model.addAttribute("transactions", thanksService.getTransactionHistory(user));

        // Let layout know which menu is active
        model.addAttribute("activeMenu", "dashboard");

        return "my-thanks/dashboard";
    }

    /* ---------- STORE ---------- */
    @GetMapping("/store")
    public String store(HttpSession session, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/my-thanks/login";
        }
        User user = getAuthenticatedUser(session, principal);
        model.addAttribute("user", user);
        model.addAttribute("wallet", thanksService.getOrCreateWallet(user));
        model.addAttribute("activeMenu", "store");
        return "my-thanks/store";
    }

    /* ---------- SEND THANKS ---------- */
    @GetMapping("/send")
    public String sendThanksView(HttpSession session, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/my-thanks/login";
        }
        User user = getAuthenticatedUser(session, principal);
        model.addAttribute("user", user);
        model.addAttribute("wallet", thanksService.getOrCreateWallet(user));
        model.addAttribute("activeMenu", "send");
        model.addAttribute("users", userRepository.findAll()); // for the dropdown
        return "my-thanks/send";
    }

    @PostMapping("/send")
    public String sendThanks(
            @RequestParam String receiverUsername,
            @RequestParam Integer points,
            @RequestParam(required = false) String message,
            HttpSession session,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {

        User sender = getAuthenticatedUser(session, principal);
        Optional<User> receiver = userRepository.findByUsername(receiverUsername);

        if (sender != null && receiver.isPresent()) {
            try {
                thanksService.sendThanks(sender, receiver.get(), points, "APPRECIATION", message != null ? message : "Great work!");
                redirectAttributes.addFlashAttribute("success", "Sent successfully");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", e.getMessage());
            }
        }

        return "redirect:/my-thanks/dashboard";
    }

    /* ---------- HISTORY ---------- */
    @GetMapping("/history")
    public String history(
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate startDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate endDate,
            @RequestParam(required = false) String searchQuery,
            HttpSession session, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/my-thanks/login";
        }
        User user = getAuthenticatedUser(session, principal);
        model.addAttribute("user", user);
        model.addAttribute("wallet", thanksService.getOrCreateWallet(user));
        
        if (startDate != null || endDate != null || (searchQuery != null && !searchQuery.isEmpty())) {
            model.addAttribute("transactions", thanksService.getFilteredTransactionHistory(user, startDate, endDate, searchQuery));
        } else {
            model.addAttribute("transactions", thanksService.getTransactionHistory(user));
        }
        
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("activeMenu", "history");
        return "my-thanks/history";
    }

    /* ---------- REDEEM ---------- */
    @PostMapping("/redeem")
    @ResponseBody
    public org.springframework.http.ResponseEntity<?> redeemItem(
            @RequestParam String itemName,
            @RequestParam Integer points,
            @RequestParam String productType,
            HttpSession session,
            Principal principal) {
        
        User user = getAuthenticatedUser(session, principal);
        if (user == null) {
            return org.springframework.http.ResponseEntity.status(401).body("Unauthorized");
        }
        
        try {
            thanksService.redeemItem(user, itemName, points, productType);
            return org.springframework.http.ResponseEntity.ok().body("{\"status\":\"success\"}");
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.badRequest().body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    /* ---------- CART ---------- */
    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/my-thanks/login";
        }
        User user = getAuthenticatedUser(session, principal);
        model.addAttribute("user", user);
        model.addAttribute("wallet", thanksService.getOrCreateWallet(user));
        model.addAttribute("activeMenu", "cart");

        List<ThanksCartItem> cart = thanksCartItemRepository.findByUser(user);
        model.addAttribute("cartItems", cart);

        int totalPoints = cart.stream().mapToInt(ThanksCartItem::getPoints).sum();
        model.addAttribute("cartTotal", totalPoints);

        return "my-thanks/cart";
    }

    @PostMapping("/cart/add")
    @ResponseBody
    public org.springframework.http.ResponseEntity<?> addToCart(
            @RequestParam String itemName,
            @RequestParam Integer points,
            @RequestParam String productType,
            @RequestParam(required = false) String imageSrc,
            HttpSession session, Principal principal) {
        
        User user = getAuthenticatedUser(session, principal);
        if (user == null) {
            return org.springframework.http.ResponseEntity.status(401).body("Unauthorized");
        }
        
        ThanksCartItem cartItem = new ThanksCartItem(user, itemName, points, productType, imageSrc);
        thanksCartItemRepository.save(cartItem);

        int cartSize = thanksCartItemRepository.findByUser(user).size();
        return org.springframework.http.ResponseEntity.ok().body("{\"status\":\"success\", \"cartSize\":" + cartSize + "}");
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam Long id, HttpSession session, Principal principal) {
        User user = getAuthenticatedUser(session, principal);
        if (user != null) {
            thanksCartItemRepository.findById(id).ifPresent(item -> {
                if (item.getUser().getId().equals(user.getId())) {
                    thanksCartItemRepository.delete(item);
                }
            });
        }
        return "redirect:/my-thanks/cart";
    }

    @PostMapping("/cart/checkout")
    public String checkoutCart(HttpSession session, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/my-thanks/login";
        }
        User user = getAuthenticatedUser(session, principal);
        List<ThanksCartItem> cart = thanksCartItemRepository.findByUser(user);

        if (cart == null || cart.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Your cart is empty.");
            return "redirect:/my-thanks/cart";
        }

        int totalPoints = cart.stream().mapToInt(ThanksCartItem::getPoints).sum();
        com.example.admindashboard.model.ThanksWallet wallet = thanksService.getOrCreateWallet(user);

        if (wallet.getWalletBalance() < totalPoints) {
            redirectAttributes.addFlashAttribute("error", "Insufficient points to complete this checkout.");
            return "redirect:/my-thanks/cart";
        }

        try {
            for (ThanksCartItem item : cart) {
                thanksService.redeemItem(user, item.getItemName(), item.getPoints(), item.getProductType());
            }
            thanksCartItemRepository.deleteAll(cart);
            redirectAttributes.addFlashAttribute("success", "Checkout completed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Checkout failed: " + e.getMessage());
        }

        return "redirect:/my-thanks/cart";
    }

    /* ---------- FAQS ---------- */
    @GetMapping("/faqs")
    public String faqs(HttpSession session, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/my-thanks/login";
        }
        User user = getAuthenticatedUser(session, principal);
        model.addAttribute("user", user);
        model.addAttribute("wallet", thanksService.getOrCreateWallet(user));
        model.addAttribute("activeMenu", "faqs");
        return "my-thanks/faqs";
    }

    /* ---------- PROFILE ---------- */
    @GetMapping("/profile")
    public String viewProfile(HttpSession session, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/my-thanks/login";
        }
        User user = getAuthenticatedUser(session, principal);
        model.addAttribute("user", user);
        model.addAttribute("wallet", thanksService.getOrCreateWallet(user));
        model.addAttribute("activeMenu", "dashboard"); // keep dashboard menu active
        return "my-thanks/profile";
    }

    @PostMapping("/profile/address")
    public String updateDeliveryAddress(
            @RequestParam String addressType,
            @RequestParam String address,
            @RequestParam String pincode,
            HttpSession session,
            Principal principal,
            RedirectAttributes redirectAttributes
    ) {
        if (principal == null) {
            return "redirect:/my-thanks/login";
        }
        User user = getAuthenticatedUser(session, principal);
        if (user != null && user.getEmployeeProfile() != null) {
            user.getEmployeeProfile().setDeliveryAddressType(addressType);
            user.getEmployeeProfile().setDeliveryAddress(address);
            user.getEmployeeProfile().setDeliveryPincode(pincode);
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("success", "Delivery Address Updated Successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Unable to update profile. User or profile not found.");
        }
        return "redirect:/my-thanks/profile";
    }

    /* ---------- NOTIFICATIONS ---------- */
    @GetMapping("/notifications")
    public String notifications(HttpSession session, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/my-thanks/login";
        }
        User user = getAuthenticatedUser(session, principal);
        model.addAttribute("user", user);
        model.addAttribute("wallet", thanksService.getOrCreateWallet(user));
        model.addAttribute("notifications", thanksService.getNotifications(user));
        model.addAttribute("activeMenu", "dashboard");

        // Mark all as read when user visits the page
        thanksService.markAllNotificationsRead(user);
        return "my-thanks/notifications";
    }

    /* ---------- LOGOUT ---------- */
    @GetMapping("/logout-thanks")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/my-thanks/login";
    }
}
