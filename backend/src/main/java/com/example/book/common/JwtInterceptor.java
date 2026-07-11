package com.example.book.common;

import com.example.book.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtUtil jwtUtil;
    private static final String AUTH_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final ThreadLocal<UserContext> USER_HOLDER = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;
        String header = request.getHeader(AUTH_HEADER);
        if (!StringUtils.hasText(header) || !header.startsWith(TOKEN_PREFIX)) throw new BusinessException(ErrorCode.UNAUTHORIZED, "请先登录");
        String token = header.substring(TOKEN_PREFIX.length());
        if (!StringUtils.hasText(token)) throw new BusinessException(ErrorCode.UNAUTHORIZED, "token不能为空");
        try {
            Long userId = jwtUtil.getUserId(token);
            String username = jwtUtil.getUsername(token);
            Integer role = jwtUtil.getRole(token);
            USER_HOLDER.set(new UserContext(userId, username, role));
            return true;
        } catch (ExpiredJwtException e) { throw new BusinessException(ErrorCode.TOKEN_EXPIRED); }
        catch (Exception e) { throw new BusinessException(ErrorCode.TOKEN_INVALID); }
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) { USER_HOLDER.remove(); }
    public static UserContext getCurrentUser() { return USER_HOLDER.get(); }
    @Data @AllArgsConstructor
    public static class UserContext { private Long userId; private String username; private Integer role; }
}
