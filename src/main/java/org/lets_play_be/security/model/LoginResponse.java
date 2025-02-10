package org.lets_play_be.security.model;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponse(@Schema(description = "jwt token", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsYWJyYXJ5LnRlc3RAZ21haWwuY29tIiwidXNlclJvbGVzIjpbIlJPTEVfQURNSU4iLCJST0xFX1VTRVIiXSwiZXhwIjoxNzM4OTUwMDYwLCJpYXQiOjE3Mzg5NDkxNjB9.yJE1cj-J4KVqZHcgLBtYCBBRHINoVjoHnoqwhqO7reQ")
        String accessToken) {
}
