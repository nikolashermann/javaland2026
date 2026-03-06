package com.breuninger.nhermann.javaland2026.demoapiserver;

public record Article(
    String id, String title, String image, String description, Double price, Long stock) {}
