package org.demo.useraccounts;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.data.r2dbc.AutoConfigureDataR2dbc;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.test.context.TestPropertySource;


@DataR2dbcTest()
@AutoConfigureDataR2dbc
@TestPropertySource( properties = {
        "spring.r2dbc.url=r2dbc:tc:mysql:///databasename?TC_IMAGE_TAG=8.0.36",
        "spring.r2dbc.username=user",
        "spring.r2dbc.password=password"
})
public class UserTransactionServiceTests {

    @Test
    void loadContext() {

    }


}
