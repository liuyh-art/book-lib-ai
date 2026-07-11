package com.example.book.controller;
import com.example.book.common.ErrorCode;
import com.example.book.common.JwtInterceptor;
import com.example.book.common.Result;
import com.example.book.dto.UserDTO;
import com.example.book.entity.SysUser;
import com.example.book.service.SysUserService;
import com.example.book.vo.PageVO;
import com.example.book.vo.UserInfoVO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController @RequestMapping("/api/user")
public class UserController {
    @Autowired private SysUserService sysUserService;
    @GetMapping("/page")
    public Result<PageVO<UserInfoVO>> listUsers(
            @RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String username, @RequestParam(required = false) String realName,
            @RequestParam(required = false) Integer role, @RequestParam(required = false) Integer status) {
        SysUser query = new SysUser(); query.setUsername(username); query.setRealName(realName); query.setRole(role); query.setStatus(status);
        return Result.success(sysUserService.listUsers(pageNum, pageSize, query));
    }
    @PostMapping public Result<Void> addUser(@Valid @RequestBody UserDTO dto) { sysUserService.addUser(dto); return Result.success("新增成功", null); }
    @PutMapping public Result<Void> updateUser(@Valid @RequestBody UserDTO dto) { sysUserService.updateUser(dto); return Result.success("更新成功", null); }
    @DeleteMapping("/{id}") public Result<Void> deleteUser(@PathVariable Long id) { sysUserService.deleteUser(id); return Result.success("删除成功", null); }
    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestBody Map<String, String> params) {
        JwtInterceptor.UserContext user = JwtInterceptor.getCurrentUser();
        String oldPwd = params.get("oldPassword"); String newPwd = params.get("newPassword");
        if (oldPwd == null || newPwd == null) return Result.error(ErrorCode.PARAM_ERROR, "原密码和新密码不能为空");
        sysUserService.updatePassword(user.getUserId(), oldPwd, newPwd);
        return Result.success("密码修改成功", null);
    }
}
