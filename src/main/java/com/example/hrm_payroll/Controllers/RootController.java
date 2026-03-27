package com.example.hrm_payroll.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {

    @GetMapping("/")
    public String redirectToIndex() {
        return "redirect:/index.html";
    }
}
