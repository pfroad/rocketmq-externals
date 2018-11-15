/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.rocketmq.console.aspect.admin;

import org.apache.rocketmq.console.mapper.UserMapper;
import org.apache.rocketmq.console.model.User;
import org.apache.rocketmq.console.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.util.Assert;
import tk.mybatis.mapper.entity.Example;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

public class RMQAuthenticationProvider implements AuthenticationProvider {
    private final static Logger LOGGER = LoggerFactory.getLogger(RMQAuthenticationProvider.class);

    private UserMapper userMapper;
    private UserCache userCache = new NullUserCache();
    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(UsernamePasswordAuthenticationToken.class, authentication,
                        "Only UsernamePasswordAuthenticationToken is supported");

        // Determine username
        String username = (authentication.getPrincipal() == null) ? "NONE_PROVIDED"
                : authentication.getName();

        User user = retrieveUser(username);
        Assert.notNull(user,
                "retrieveUser returned null - a violation of the interface contract");

        final String presentedPassword = authentication.getCredentials().toString();
        boolean isValid = false;
        try {
            final String encodePassword = PasswordUtil.encodePassword(presentedPassword, user.getSalt());
            isValid = encodePassword.equalsIgnoreCase(user.getPassword());
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            throw new InternalAuthenticationServiceException(e.getMessage(), e);
        }

        if (!isValid) {
            LOGGER.debug("Authentication failed: password does not match stored value");
            throw new BadCredentialsException("Bad credentials");
        }

        return new UsernamePasswordAuthenticationToken(username, authentication.getCredentials(), new ArrayList<GrantedAuthority>());
    }

    private User retrieveUser(String username) {
        User loadedUser;

        final Example example = new Example(User.class);
        example.createCriteria().andEqualTo("admin", true).andEqualTo("login", username).orEqualTo("email", username);
        try {
            loadedUser = this.userMapper.selectOneByExample(example);
        } catch (Exception repositoryProblem) {
            throw new InternalAuthenticationServiceException(
                    repositoryProblem.getMessage(), repositoryProblem);
        }

        if (loadedUser == null) {
            throw new InternalAuthenticationServiceException(
                    "UserDetailsService returned null, which is an interface contract violation");
        }
        return loadedUser;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
}
