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

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    @Value("${rmq.console.datasource.url}")
    private String jdbcUrl;
    @Value("${rmq.console.datasource.username}")
    private String username;
    @Value("${rmq.console.datasource.password}")
    private String password;
    @Value("${rmq.console.datasource.driver-class-name}")
    private String driverClassName;
    @Value("${rmq.console.datasource.maxPoolSize}")
    private Integer maxPoolSize;
    @Value("${rmq.console.datasource.minIdle}")
    private Integer minIdle;
    @Value("${rmq.console.datasource.idleTimeout}")
    private Long idleTimeout;
    @Value("${rmq.console.datasource.connectTimeout}")
    private Long connectTimeout;
    @Value("${rmq.console.datasource.readOnly}")
    private Boolean readOnly = true;

    @Bean(name = "dataSource", destroyMethod = "close")
    public DataSource dataSource() {
        if (StringUtils.isEmpty(jdbcUrl) ||
                StringUtils.isEmpty(username) ||
                StringUtils.isEmpty(password) ||
                StringUtils.isEmpty(driverClassName))
            throw new NullPointerException("DataSource config cannot be null!");

        HikariConfig config = new HikariConfig();
        config.setDriverClassName(driverClassName);
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minIdle);
        config.setIdleTimeout(idleTimeout);
        config.setConnectionTimeout(connectTimeout);
        config.setReadOnly(readOnly);

        return new HikariDataSource(config);
    }

    @Bean(name = "transactionManager")
    public DataSourceTransactionManager dataSourceTransactionManager(@Qualifier("dataSource") DataSource ds) {
        return new DataSourceTransactionManager(ds);
    }
}
