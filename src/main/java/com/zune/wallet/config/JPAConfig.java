package com.zune.wallet.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.Entity;
import org.h2.tools.Server;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.sql.DataSource;
import java.sql.SQLException;

@ComponentScan(
        basePackages = {"com.zune.wallet.domain"},
        includeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Entity.class)}
)
@EnableJpaAuditing
@Configuration
public class JPAConfig {

    @Profile({"local", "test"})
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.hikari")
    public DataSource dataSource() throws SQLException {
        Server.createTcpServer("-tcp",
                "-tcpPort",
                "9092",
                "-tcpAllowOthers",
                "-ifNotExists"
        ).start();
        return new HikariDataSource();
    }
}
