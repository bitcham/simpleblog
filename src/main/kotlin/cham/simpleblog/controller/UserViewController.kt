package cham.simpleblog.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class UserViewController {
    @GetMapping("/login")
    fun login(): String{
        return "login"
    }

    @GetMapping("/signup")
    fun signUp(): String {
        return "signup"
    }
}