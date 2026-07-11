package com.example.book.service.impl;
import com.example.book.common.BusinessException;
import com.example.book.common.ErrorCode;
import com.example.book.dto.LoginDTO;
import com.example.book.dto.RegisterDTO;
import com.example.book.dto.UserDTO;
import com.example.book.entity.SysUser;
import com.example.book.mapper.SysUserMapper;
import com.example.book.service.SysUserService;
import com.example.book.util.JwtUtil;
import com.example.book.vo.LoginVO;
import com.example.book.vo.PageVO;
import com.example.book.vo.UserInfoVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class SysUserServiceImpl implements SysUserService {
    @Autowired private SysUserMapper sysUserMapper;
    @Autowired private JwtUtil jwtUtil;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    @Override
    public LoginVO login(LoginDTO dto) {
        SysUser user = sysUserMapper.selectByUsername(dto.getUsername());
        if (user == null) throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        if (user.getStatus() == 0) throw new BusinessException(ErrorCode.USER_DISABLED);
        if (!encoder.matches(dto.getPassword(), user.getPassword())) throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        return new LoginVO(token, convertToUserInfo(user));
    }
    @Override
    public void register(RegisterDTO dto) {
        if (sysUserMapper.selectByUsername(dto.getUsername()) != null) throw new BusinessException(ErrorCode.USER_EXISTS);
        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setRole(2);
        sysUserMapper.insert(user);
    }
    @Override
    public UserInfoVO getCurrentUserInfo(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        return convertToUserInfo(user);
    }
    @Override
    public PageVO<UserInfoVO> listUsers(int pageNum, int pageSize, SysUser query) {
        PageHelper.startPage(pageNum, pageSize);
        List<SysUser> list = sysUserMapper.selectList(query);
        PageInfo<SysUser> pageInfo = new PageInfo<>(list);
        List<UserInfoVO> voList = list.stream().map(this::convertToUserInfo).collect(Collectors.toList());
        return new PageVO<>(voList, pageInfo.getTotal(), pageNum, pageSize, pageInfo.getPages());
    }
    @Override @Transactional
    public void addUser(UserDTO dto) {
        if (sysUserMapper.selectByUsername(dto.getUsername()) != null) throw new BusinessException(ErrorCode.USER_EXISTS);
        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(encoder.encode(dto.getPassword() != null && !dto.getPassword().isEmpty() ? dto.getPassword() : "123456"));
        sysUserMapper.insert(user);
    }
    @Override @Transactional
    public void updateUser(UserDTO dto) {
        SysUser user = new SysUser();
        BeanUtils.copyProperties(dto, user);
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) user.setPassword(encoder.encode(dto.getPassword()));
        sysUserMapper.updateById(user);
    }
    @Override @Transactional
    public void deleteUser(Long id) { sysUserMapper.deleteById(id); }
    @Override @Transactional
    public void updatePassword(Long userId, String oldPwd, String newPwd) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        if (!encoder.matches(oldPwd, user.getPassword())) throw new BusinessException(ErrorCode.OLD_PASSWORD_ERROR);
        SysUser update = new SysUser();
        update.setId(userId);
        update.setPassword(encoder.encode(newPwd));
        sysUserMapper.updateById(update);
    }
    private UserInfoVO convertToUserInfo(SysUser user) {
        UserInfoVO vo = new UserInfoVO();
        BeanUtils.copyProperties(user, vo);
        vo.setCreateTime(user.getCreateTime() != null ? user.getCreateTime().toString().replace("T", " ") : null);
        return vo;
    }
}
