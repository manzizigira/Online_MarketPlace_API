package com.ecommerce.Ecommerce.Web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Unauthorized {

    @GetMapping("/unauthorized")
    public String unauthorized() {
        return "unauthorizedPage";  // This should match the path where your HTML page is located
    }
}
