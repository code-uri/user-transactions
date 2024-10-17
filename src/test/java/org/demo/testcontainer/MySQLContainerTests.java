package org.demo.testcontainer;


import jakarta.annotation.Nonnull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;

import java.sql.*;


public class MySQLContainerTests extends MySQLContainersConfiguration {

    static MySQLContainer<?> container = mySQLContainer();

    @BeforeAll
    static void startContainers() {
        container.start();
    }

    @AfterAll
    static void stopContainers() {
        container.stop();
    }

    private static ResultSet performQueryWithConnection(Connection connection, String sql) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(sql);
        return statement.getResultSet();
    }

    private static Connection getConnection(@Nonnull String user, @Nonnull String password)
            throws SQLException {
        return DriverManager.getConnection(container.getJdbcUrl(), user, password);
    }

    @Test
    void test_container_needs_root_access() {
        Assertions.assertDoesNotThrow(() -> {
            Connection connection = getConnection("root", "secret");
            performQueryWithConnection(connection, "set sql_log_bin=0");
        });
    }

    @Test
    void test_container_dbuser_access() {
        Assertions.assertDoesNotThrow(() -> {
            Connection connection = getConnection("db-user", "Password1");

            ResultSet resultSet = performQueryWithConnection(connection, "SELECT count(*) from Users");
            resultSet.next();
            int resultSetInt = resultSet.getInt(1);
            Assertions.assertEquals(3, resultSetInt);

            Assertions.assertThrows(SQLException.class, () -> {
                performQueryWithConnection(connection, "set sql_log_bin=0");
            });
        });
    }

}