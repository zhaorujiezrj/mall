package cn.zrj.mall.auth.security;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author zhaorujie
 * @date 2022/8/22
 */
public class NoOpPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return rawPassword.toString().equals(encodedPassword);
    }
}
