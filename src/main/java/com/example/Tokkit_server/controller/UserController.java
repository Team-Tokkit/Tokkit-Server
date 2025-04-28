// package com.example.Tokkit_server.controller;
//
// import java.util.List;
//
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;
//
// import com.example.Tokkit_server.apiPayload.ApiResponse;
// import com.example.Tokkit_server.domain.user.User;
// import com.example.Tokkit_server.service.UserService;
//
// import io.swagger.v3.oas.annotations.Operation;
// import lombok.RequiredArgsConstructor;
//
// @RestController
// @RequestMapping("/api/users")
// @RequiredArgsConstructor
// public class UserController {
//
//     private final UserService userService;
//
//     @Operation(summary = "유저 등록", description = "name, phoneNumber, email을 입력받아 유저 생성")
//     @PostMapping
//     public ApiResponse createUser(@RequestParam String name, @RequestParam String phoneNumber, @RequestParam String email) {
//         User createdUser =  userService.saveUser(name, phoneNumber, email);
//
//         return ApiResponse.onSuccess(createdUser);
//     }
//
//     @GetMapping
//     public List<User> getUsers() {
//         return userService.getAllUsers();
//     }
// }
