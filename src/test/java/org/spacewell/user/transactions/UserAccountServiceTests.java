package org.spacewell.user.transactions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserAccountServiceTests extends BaseIntegrationTests {

    @Test
    public void test_select_user() {
        assertDoesNotThrow(() -> {
            Connection connection = getUserTransactionsConnection();
            ResultSet resultSet = performQuery(connection,"select * from Users where username = 'john_doe'");
            int count = resultSet.getFetchSize();
            System.out.println(count);
        });
    }


    @Test
    void test_container_db_user_access() {
        Assertions.assertDoesNotThrow(() -> {
            Connection connection = getUserTransactionsConnection();
            connection.createStatement().execute("SELECT 1");

            Assertions.assertThrows(SQLException.class, () -> {
                connection.createStatement().execute("set sql_log_bin=0");
            });
        });
    }


    private Connection getUserTransactionsConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:" + getContainer().getMappedPort(3306) + "/user_transactions",
                "db-user",
                "Password1");
        return connection;
    }
}
