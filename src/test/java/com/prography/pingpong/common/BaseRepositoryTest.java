package com.prography.pingpong.common;

import com.prography.pingpong.config.JpaAuditingConfig;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@Import({JpaAuditingConfig.class})
@DataJpaTest
public abstract class BaseRepositoryTest {

}
