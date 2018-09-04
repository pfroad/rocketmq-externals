package org.apache.rocketmq.console;

import org.springframework.security.crypto.password.PasswordEncoder;

public class RMQPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence charSequence) {
        return null;
    }

    @Override
    public boolean matches(CharSequence charSequence, String s) {
        return false;
    }
}
