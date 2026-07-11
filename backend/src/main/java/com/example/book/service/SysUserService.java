package com.example.book.service;
import com.example.book.dto.LoginDTO;
import com.example.book.dto.RegisterDTO;
import com.example.book.dto.UserDTO;
import com.example.book.entity.SysUser;
import com.example.book.vo.LoginVO;
import com.example.book.vo.PageVO;
import com.example.book.vo.UserInfoVO;
public interface SysUserService {
    LoginVO login(LoginDTO dto);
    void register(RegisterDTO dto);
    UserInfoVO getCurrentUserInfo(Long userId);
    PageVO<UserInfoVO> listUsers(int pageNum, int pageSize, SysUser query);
    void addUser(UserDTO dto);
    void updateUser(UserDTO dto);
    void deleteUser(Long id);
    void updatePassword(Long userId, String oldPwd, String newPwd);
}
