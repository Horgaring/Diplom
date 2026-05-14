package com.horgaring.diplombackednd.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SPAController {

    @GetMapping({
            "/admin/login",
            "/admin/dashboard",
            "/admin/users",
            "/admin/users/{id}",
            "/admin/messages",
            "/admin/cities"
    })
    public String forward() {
        return "forward:/admin/index.html";
    }
}
