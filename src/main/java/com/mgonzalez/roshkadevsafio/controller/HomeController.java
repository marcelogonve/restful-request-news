package com.mgonzalez.roshkadevsafio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.mgonzalez.roshkadevsafio.interfaces.HomeControllerInterface;

@Controller
public class HomeController implements HomeControllerInterface {
    
    @Override
    @GetMapping("/")
    public String home() {
        return "html/index.html";
    }
}
