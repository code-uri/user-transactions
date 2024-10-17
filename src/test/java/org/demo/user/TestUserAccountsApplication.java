package org.demo.user;

import org.demo.testcontainer.MySQLContainersConfiguration;
import org.springframework.boot.SpringApplication;

public class TestUserAccountsApplication {

    public static void main(String[] args) {
        SpringApplication.from(UserAccountsApplication::main).with(MySQLContainersConfiguration.class).run(args);
    }

}
