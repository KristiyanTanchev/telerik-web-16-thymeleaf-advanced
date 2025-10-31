package com.company.web.springdemo.controllers.mvc;

import com.company.web.springdemo.exceptions.AuthorizationException;
import com.company.web.springdemo.exceptions.EmailDuplicateException;
import com.company.web.springdemo.exceptions.EntityDuplicateException;
import com.company.web.springdemo.exceptions.UsernameDuplicateException;
import com.company.web.springdemo.helpers.AuthenticationHelper;
import com.company.web.springdemo.helpers.UserMapper;
import com.company.web.springdemo.models.User;
import com.company.web.springdemo.models.UserDto;
import com.company.web.springdemo.models.UserLoginDto;
import com.company.web.springdemo.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthenticationMvcController {
    private final UserService userService;
    private final AuthenticationHelper authenticationHelper;
    private final UserMapper userMapper;

    @Autowired
    public AuthenticationMvcController(UserService userService, AuthenticationHelper authenticationHelper, UserMapper userMapper) {
        this.userService = userService;
        this.authenticationHelper = authenticationHelper;
        this.userMapper = userMapper;
    }

    @GetMapping("/login")
    public String showLogin(Model model, HttpSession session) {
        model.addAttribute("user", new UserLoginDto());
        return "LoginPage";
    }

    @PostMapping("/login")
    public String handleLogin(@ModelAttribute("user") @Valid UserLoginDto userDto,
                              BindingResult errors,
                              HttpSession session) {
        if (errors.hasErrors()) {
            return "LoginPage";
        }
        try {

            User user = authenticationHelper.tryGetUser(userDto);
            session.setAttribute("username", user.getUsername());
            session.setAttribute("isAdmin", user.isAdmin());
            session.setAttribute("isLogged", true);
        } catch (AuthorizationException e) {
            errors.rejectValue("password", "password", e.getMessage());
            return "LoginPage";
        }

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String handleLogout(HttpSession session) {
        session.removeAttribute("user");
        session.removeAttribute("isAdmin");
        session.setAttribute("isLogged", false);
        return "redirect:/";
    }

    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("user", new UserDto());
        return "RegisterPage";
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute("user") UserDto userDto,
                                 BindingResult errors,
                                 HttpSession session) {
        if (errors.hasErrors()) {
            return "RegisterPage";
        }
        try{
            User user = userMapper.fromDto(userDto);
            userService.create(user);
        }catch (UsernameDuplicateException e){
            errors.rejectValue("username", "username.taken", e.getMessage());
            return "RegisterPage";
        }catch (EmailDuplicateException e) {
            errors.rejectValue("email", "email.taken", e.getMessage());
            return "RegisterPage";
        }
        return "redirect:/";
    }
}
