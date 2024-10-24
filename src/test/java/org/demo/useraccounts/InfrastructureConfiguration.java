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
class InfrastructureConfiguration {}