package com.ecommerce.laboratoriosaga.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String home() {
        return "✅ Aplicación Spring Boot funcionando correctamente";
    }

    @GetMapping("/test")
    public String test() {
        return "Hola, Braulio 👋 desde tu microservicio";
    }
}