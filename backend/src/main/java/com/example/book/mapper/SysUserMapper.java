package com.example.book.mapper;
import com.example.book.entity.SysUser;
import org.apache.ibatis.annotations.Param;
import java.util.List;
public interface SysUserMapper {
    SysUser selectById(@Param("id") Long id);
    SysUser selectByUsername(@Param("username") String username);
    List<SysUser> selectList(@Param("user") SysUser user);
    long countTotal(@Param("user") SysUser user);
    int insert(SysUser user);
    int updateById(SysUser user);
    int deleteById(@Param("id") Long id);
}
