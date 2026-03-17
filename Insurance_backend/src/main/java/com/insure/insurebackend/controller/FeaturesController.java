package com.insure.insurebackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/features")
@CrossOrigin(origins = "http://localhost:5173")
public class FeaturesController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> getFeatures() {

        Map<String, Object> body = Map.of(
                "heroTitle", "Our Services",
                "heroSubtitle", "Explore our wide range of insurance products designed to protect every aspect of your life.",
                "topServices", List.of(
                        Map.of(
                                "id", 1,
                                "title", "Online Insurance",
                                "description", "Comprehensive coverage for your peace of mind with instant online processing.",
                                "icon", "shield"
                        ),
                        Map.of(
                                "id", 2,
                                "title", "Health Protection",
                                "description", "Secure your family's health with our extensive network of hospitals.",
                                "icon", "heart"
                        ),
                        Map.of(
                                "id", 3,
                                "title", "Vehicle Safety",
                                "description", "Protect your vehicle against accidents, theft, and third‑party liabilities.",
                                "icon", "car"
                        )
                ),
                "midServices", List.of(
                        Map.of(
                                "id", 4,
                                "title", "Home Security",
                                "description", "Safeguard your dream home from natural calamities and burglaries.",
                                "icon", "home"
                        ),
                        Map.of(
                                "id", 5,
                                "title", "Life Insurance",
                                "description", "Ensure your family's financial stability even in your absence.",
                                "icon", "umbrella"
                        ),
                        Map.of(
                                "id", 6,
                                "title", "Business Cover",
                                "description", "Protect your business assets and liabilities with customized plans.",
                                "icon", "briefcase"
                        )
                ),
                "footer", Map.of(
                        "aboutTitle", "Online Insurance",
                        "aboutText", "Providing reliable insurance solutions for your family and assets 24/7.",
                        "quickLinks", List.of("About Us", "Our Service", "Contact"),
                        "tags", List.of("Insurance", "Policy", "Safety", "Family"),
                        "contact", Map.of(
                                "address", "123 Insurance Ave, City, State",
                                "phone", "+1 234 567 8900",
                                "email", "support@onlineinsurance.com"
                        )
                )
        );

        return ResponseEntity.ok(body);
    }
}

