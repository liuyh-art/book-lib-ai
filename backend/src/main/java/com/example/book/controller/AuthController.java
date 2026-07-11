package com.example.book.controller;
import com.example.book.common.JwtInterceptor;
import com.example.book.common.Result;
import com.example.book.dto.LoginDTO;
import com.example.book.dto.RegisterDTO;
import com.example.book.service.SysUserService;
import com.example.book.vo.LoginVO;
import com.example.book.vo.UserInfoVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/auth")
public class AuthController {
    @Autowired private SysUserService sysUserService;
    @PostMapping("/login") public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto) { return Result.success(sysUserService.login(dto)); }
    @PostMapping("/register") public Result<Void> register(@Valid @RequestBody RegisterDTO dto) { sysUserService.register(dto); return Result.success("注册成功", null); }
    @GetMapping("/me") public Result<UserInfoVO> getCurrentUser() {
        JwtInterceptor.UserContext user = JwtInterceptor.getCurrentUser();
        return Result.success(sysUserService.getCurrentUserInfo(user.getUserId()));
    }
}
