package com.example.book.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class UserDTO {
    private Long id;
    @NotBlank @Size(min=3,max=50) private String username;
    private String password; private String realName; private String email; private String phone;
    private Integer role; private Integer status;
}
