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

package org.apache.rocketmq.console.config;

import org.apache.rocketmq.console.aspect.admin.RMQAuthenticationProvider;
import org.apache.rocketmq.console.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity // 1
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter { // 2

    @Autowired
    private UserMapper userMapper;

//    @Bean
//    public RMQUserDetailsService userDetailsService() {
//        RMQUserDetailsService userDetailsService = new RMQUserDetailsService();
//        userDetailsService.setUserMapper(userMapper);
//        return userDetailsService;
//    }

    @Bean
    public RMQAuthenticationProvider rmqAuthenticationProvider() {
        final RMQAuthenticationProvider rmqAuthenticationProvider = new RMQAuthenticationProvider();
        rmqAuthenticationProvider.setUserMapper(userMapper);
        return rmqAuthenticationProvider;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/user/login", "/login", "/**/*.css", "/**/*.js", "/**/*.png", "**/favicon.ico").permitAll() //4
//                .and().authorizeRequests().antMatchers()
//                .antMatchers("/user/**").authenticated()//.hasRole("USER") //5
                .anyRequest().authenticated()
                .and()                // 6
                .formLogin()
                .loginPage("/user/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/index.html")
                .and()
                .csrf().disable()
                .httpBasic();
//                .loginPage("/login")
//                .failureUrl("/login-error").permitAll();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception { // 7
        auth.authenticationProvider(rmqAuthenticationProvider());
//            .inMemoryAuthentication() //8
//            .withUser("user").password("password").roles("USER"); // 9
//                .userDetailsService(userDetailsService());
    }
}