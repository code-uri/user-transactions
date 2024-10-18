package org.demo.useraccounts;

import org.springframework.boot.SpringApplication;

public class TestUserAccountsApplication {

    public static void main(String[] args) {
        SpringApplication.from(UserAccountsApplication::main).run(args);
    }

}
