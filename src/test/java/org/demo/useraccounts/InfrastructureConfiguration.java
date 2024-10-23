package org.demo.useraccounts;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Oliver Gierke
 * @author Mark Paluch
 */
@TestConfiguration
@EnableTransactionManagement
@TestPropertySource( properties = {
        "spring.r2dbc.url=r2dbc:tc:mysql:///databasename?TC_IMAGE_TAG=8.0.36",
        "spring.r2dbc.username=user",
        "spring.r2dbc.password=password",
        "spring.sql.init.mode=ALWAYS",
        "logging.level.org.springframework.transaction=TRACE"
})
class InfrastructureConfiguration {}