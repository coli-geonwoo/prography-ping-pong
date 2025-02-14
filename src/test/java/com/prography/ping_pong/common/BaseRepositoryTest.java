package com.prography.ping_pong.common;

import com.prography.ping_pong.config.JpaAuditingConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Import({JpaAuditingConfig.class})
@DataJpaTest
public abstract class BaseRepositoryTest {

}
