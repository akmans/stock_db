package com.akmans.trade.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.akmans.trade.core.springdata.jpa.config.DataSourceConfig;
import com.akmans.trade.core.springdata.jpa.config.JpaConfig;
import com.akmans.trade.core.springdata.jpa.config.RepositoryConfig;

@Configuration
@Import({DataSourceConfig.class, JpaConfig.class, RepositoryConfig.class})
public class TradeCoreConfig {
}
