package cham.simpleblog.controller

import cham.simpleblog.dto.AddUserRequest
import cham.simpleblog.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping

@Controller
class UserApiController (
    val userService: UserService
){
    @PostMapping("/user")
    fun signUp(addUserRequest: AddUserRequest): String {
        userService.save(addUserRequest)
        return "redirect:/login"
    }

    @GetMapping("/logout")
    fun logout(request: HttpServletRequest, response: HttpServletResponse): String {
        SecurityContextLogoutHandler()
            .logout(request, response, SecurityContextHolder.getContext().getAuthentication())
        return "redirect:/login"
    }


}