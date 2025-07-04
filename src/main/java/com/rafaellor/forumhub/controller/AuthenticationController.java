package com.rafaellor.forumhub.controller;

import com.rafaellor.forumhub.dto.LoginDto;
import com.rafaellor.forumhub.dto.JwtTokenDto;
import com.rafaellor.forumhub.model.User;
import com.rafaellor.forumhub.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping
    public ResponseEntity<JwtTokenDto> login(@RequestBody @Valid LoginDto loginDto) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        var authentication = authenticationManager.authenticate(authenticationToken);

        var jwtToken = tokenService.generateToken((User) authentication.getPrincipal());

        return ResponseEntity.ok(new JwtTokenDto(jwtToken));
    }
}
