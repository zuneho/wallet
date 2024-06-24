package com.zune.wallet;

import com.zune.wallet.config.JPAConfig;
import com.zune.wallet.config.QueryDslConfig;
import com.zune.wallet.config.auth.SecurityConfig;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;


@Profile({"test"})
@TestConfiguration
@Import({SecurityConfig.class, JPAConfig.class, QueryDslConfig.class})
public class TestConfig {
}
