package org.blbulyandavbulyan.blog.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserTemplatesController {
    @GetMapping
    public String showUsersAdminPage(){
        return "users";
    }
}
