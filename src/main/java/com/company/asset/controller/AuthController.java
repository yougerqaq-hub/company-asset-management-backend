package com.company.asset.controller;

import com.company.asset.common.*;
import com.company.asset.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 登录注册接口；正式项目可将这里的临时 token 替换为 JWT。
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    @PersistenceContext
    private EntityManager em;
    private final com.company.asset.service.CrudService crud;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 用户注册，用户名不可重复。
     */
    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        if (em.createQuery("select count(u) from User u where u.username=:n", Long.class).setParameter("n", user.getUsername()).getSingleResult() > 0)
            throw new BusinessException("用户名已存在");
        user.setPassword(encoder.encode(user.getPassword()));
        return Result.ok("注册成功", crud.save(user));
    }

    /**
     * 用户登录，返回用户资料与临时 accessToken。
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest req) {
        List<User> users = em.createQuery("from User u where u.username=:n", User.class).setParameter("n", req.username).getResultList();
        if (users.isEmpty() || !encoder.matches(req.password, users.get(0).getPassword()))
            throw new BusinessException("用户名或密码错误");
        User u = users.get(0);
        if (u.getStatus() != 1) throw new BusinessException("账号已停用");
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("accessToken", UUID.randomUUID().toString());
        data.put("user", u);
        return Result.ok("登录成功", data);
    }

    @Data
    static class LoginRequest {
        private String username;
        private String password;
    }
}
