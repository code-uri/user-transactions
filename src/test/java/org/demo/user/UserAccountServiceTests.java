package org.demo.user;


import org.demo.user.model.Customer;
import org.demo.user.repository.CustomerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;


@Testcontainers(disabledWithoutDocker = true)
@DataR2dbcTest()
@TestPropertySource( properties = {
        "spring.r2dbc.url=r2dbc:tc:mysql:///databasename?TC_IMAGE_TAG=8.0.36",
        "spring.r2dbc.username=user",
        "spring.r2dbc.password=password"
})
public class UserAccountServiceTests {


    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void test_find_by_lastname() {

        Customer customer = customerRepository.findByLastName("koduri").blockFirst();

        Assertions.assertEquals("koduri", customer.getLastName());

    }


}
