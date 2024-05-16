package com.myproject.emailverifierrestservice.controller;

import com.myproject.emailverifierrestservice.dto.UserResponseDto;
import com.myproject.emailverifierrestservice.entity.AppUser;
import com.myproject.emailverifierrestservice.service.abstraction.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("${api-prefix}/user")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
@Tag(name = "User")
public class UserController {

    private static final ModelMapper modelMapper = new ModelMapper();

    private final UserService userService;


    @Operation(summary = "Get current user", description = "Retrieves information about the currently authenticated user")
    @GetMapping("/current-user")
    public ResponseEntity<UserResponseDto> getCurrentUser(Authentication authentication) {

        AppUser appUser = userService.getByEmail(authentication.getName());

        return ResponseEntity.ok(modelMapper.map(appUser, UserResponseDto.class));
    }

    @Operation(summary = "Get user page", description = "Retrieves a paginated list of users")
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Page<UserResponseDto>> getUserPage(@RequestParam(defaultValue = "0", name = "page-number") Integer pageNumber,
                                                             @RequestParam(defaultValue = "10", name = "page-size") Integer pageSize) {

        Page<AppUser> userPage = userService.getAll(PageRequest.of(pageNumber, pageSize));

        return ResponseEntity.ok(userPage.map(user -> modelMapper.map(user, UserResponseDto.class)));
    }

}
