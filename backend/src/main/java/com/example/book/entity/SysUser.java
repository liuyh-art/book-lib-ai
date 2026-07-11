package com.example.book.entity;
import lombok.Data;
import java.time.LocalDateTime;
@Data
public class SysUser {
    private Long id; private String username; private String password; private String realName;
    private String email; private String phone; private Integer role; private String avatar;
    private Integer status; private Integer isDeleted; private LocalDateTime createTime; private LocalDateTime updateTime;
}
