package com.example.demo.controllers;

import com.example.demo.data.LoginForm;
import com.example.demo.data.UserEntity;
import com.example.demo.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // Kullanıcıları listele
    @GetMapping
    public String listUsers(HttpSession session, Model model) {
        UserEntity currentUser = (UserEntity) session.getAttribute("user");
        if (currentUser == null || currentUser.getRole() != UserEntity.Role.ROLE_ADMIN) {
            return "redirect:/access-denied";
        }

        model.addAttribute("users", userService.getAllUsers());
        return "listUser";
    }

    // Yeni kullanıcı formu
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("user", new UserEntity());
        return "addUser";
    }

    // Yeni kullanıcı ekle
    @PostMapping("/add")
    public String addUser(@ModelAttribute("user") @Valid UserEntity user, BindingResult result) {
        if (result.hasErrors()) {
            return "addUser";
        }
        userService.createUser(user);
        return "redirect:/users";
    }

    // Kullanıcı düzenleme formu
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        UserEntity user = userService.getUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID:" + id));
        model.addAttribute("user", user);
        return "editUser";
    }

    // Kullanıcıyı güncelle
    @PostMapping("/edit/{id}")
    public String updateUser(@PathVariable Integer id,
                             @ModelAttribute("user") @Valid UserEntity user,
                             BindingResult result) {
        if (result.hasErrors()) {
            return "editUser";
        }
        user.setId(id);
        userService.createUser(user);
        return "redirect:/users";
    }

    // Kullanıcı sil
    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return "redirect:/users";
    }

    // Rol değiştirme
    @PostMapping("/changeRole")
    public String changeUserRole(@RequestParam int userId,
                                 @RequestParam String role) {
        userService.changeUserRole(userId, role);
        return "redirect:/users";
    }

    // Kayıt formu
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new UserEntity());
        return "register";
    }

    // Kayıt işlemi
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") @Valid UserEntity user,
                               BindingResult result) {
        if (result.hasErrors()) {
            return "register";
        }
        user.setRole(UserEntity.Role.ROLE_LIMITED_USER);
        userService.createUser(user);
        return "redirect:/users/login";
    }

    // Tek login metodu (POST)
    @PostMapping("/login")
    public String login(@RequestParam String login,
                        @RequestParam String password,
                        @RequestParam(required = false) String rememberMe,
                        HttpSession session,
                        HttpServletRequest request,
                        HttpServletResponse response,
                        Model model) {
        Optional<UserEntity> userOpt = userService.findByLogin(login);
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            if (user.getPassword().equals(password)) {
                session.setAttribute("user", user); // Oturum başlat

                if ("on".equals(rememberMe)) {
                    Cookie cookie = new Cookie("rememberMeUser", login);
                    cookie.setMaxAge(7 * 24 * 60 * 60);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                } else {
                    Cookie[] cookies = request.getCookies();
                    if (cookies != null) {
                        for (Cookie c : cookies) {
                            if ("rememberMeUser".equals(c.getName())) {
                                c.setMaxAge(0);
                                c.setPath("/");
                                response.addCookie(c);
                            }
                        }
                    }
                }
                return "redirect:/information";
            }
        }
        model.addAttribute("loginError", true);
        return "login";
    }

    @GetMapping("/login")
    public String showLoginForm(HttpServletRequest request, Model model) {
        if (!model.containsAttribute("loginForm")) {
            model.addAttribute("loginForm", new LoginForm());
        }

        String rememberedLogin = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("rememberMeUser".equals(cookie.getName())) {
                    rememberedLogin = cookie.getValue();
                    break;
                }
            }
        }
        model.addAttribute("rememberedLogin", rememberedLogin);
        return "login";
    }
    @PostMapping("/logout")
    public String logout(HttpSession session, HttpServletResponse response) {
        Object userObj = session.getAttribute("user");
        if (userObj instanceof UserEntity user) {
            userService.saveUserSortPreference(user, user.getSortPreference());
        }

        session.invalidate();

        Cookie cookie = new Cookie("rememberMeUser", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
        return "redirect:/users/login";
    }
    @PostMapping("/updateSortPreference")
    public String updateSortPreference(@RequestParam String sortPreference, HttpSession session) {
        Object userObj = session.getAttribute("user");
        if (userObj instanceof UserEntity user) {
            user.setSortPreference(sortPreference);
            session.setAttribute("user", user);


            String[] parts = sortPreference.split(":");
            String sortField = parts.length > 0 ? parts[0] : "date";
            String sortDir = parts.length > 1 ? parts[1] : "asc";

            return "redirect:/information?sortField=" + sortField + "&sortDir=" + sortDir;
        }
        return "redirect:/users/login";
    }

}
