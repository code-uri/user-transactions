package org.spacewell.user.transactions;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MySQLContainer;

import javax.sql.DataSource;
import java.sql.*;


@Import(TestcontainersConfiguration.class)
@SpringBootTest
class BaseIntegrationTests {

    @Autowired
    private MySQLContainer container;

    @Autowired
    private DataSource dataSource;

    @Test
    void test_container_check_root_access() {

        Assertions.assertDoesNotThrow(() -> {
            Connection connection = DriverManager.getConnection(
                    container.getJdbcUrl(),
                    "root",
                    "secret");
            connection.createStatement().execute("set sql_log_bin=0");
        });

    }





    protected ResultSet performQuery(Connection connection, String sql) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(sql);
        ResultSet resultSet = statement.getResultSet();
        resultSet.next();
        return resultSet;
    }

    protected ResultSet performQuery(String sql) throws SQLException {
        Statement statement = dataSource.getConnection().createStatement();
        statement.execute(sql);
        ResultSet resultSet = statement.getResultSet();
        resultSet.next();
        return resultSet;
    }


    public MySQLContainer getContainer() {
        return container;
    }

    public void setContainer(MySQLContainer container) {
        this.container = container;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}