import java.sql.*;

import static java.lang.System.in;
import static java.lang.System.out;

// create schema `school` default charset utf8mb4 default collate utf8mb4_general_ci
// id student_name student_number
/*
create table `student` (
	`id` bigint auto_increment not null,
    `student_name` varchar(64) not null default '',
    `student_number` bigint not null default 0,
    primary key (`id`),
    key `student_name` (`student_name`),
    unique key `student_number` (`student_number`)
)
需求
以不同的隔离级别运行同一套代码
初始化，获取连接部分的逻辑应该加以复用。
直接一个类4个函数，每个函数一个prepare好了。
拢共也就200行代码，写4个类太弱智了
 */

public class Example {
    private final String jdbcUrl;


    public Example(String host, int port, String databaseName, String username, String password) throws SQLException {
        jdbcUrl = String.format("jdbc:mysql://%s:%s/%s?user=%s&password=%s",
                host, port, databaseName, username, password);
    }

    private void resetContext() throws SQLException {
        Connection connection = getConnection(Connection.TRANSACTION_READ_COMMITTED);
        PreparedStatement preparedStatement = connection.prepareStatement("TRUNCATE table `student`");
        preparedStatement.execute();
        connection.commit();
    }

    public Connection getConnection(int isolationLevel) throws SQLException {
        Connection connection = DriverManager.getConnection(jdbcUrl);
        connection.setTransactionIsolation(isolationLevel);
        connection.setAutoCommit(false);
        return connection;
    }

    void dirtyRead(int isolationLevel) throws SQLException {
        resetContext();
        Connection connA = getConnection(isolationLevel);
        Connection connB = getConnection(isolationLevel);


        //CONNECTION_A_INSERT
        String insertSql = "INSERT INTO `student` (`student_name`, `student_number`) VALUES ('alice', 1)";
        connA.prepareStatement(insertSql).execute();
        Utils.printAction("CONNECTION_A_INSERT", insertSql);

        //CONNECTION_B_READ
        PreparedStatement ConnBSelectAllQuery = connB.prepareStatement("SELECT * FROM `student`");
        Utils.printResultSet("CONNECTION_B_READ", ConnBSelectAllQuery.executeQuery());

        //CONNECTION_A_ROLLBACK
        connA.rollback();
        Utils.printAction("CONNECTION_A_ROLLBACK", "");

        //CONNECTION_B_READ_AGAIN
        Utils.printResultSet("CONNECTION_B_READ_AGAIN", ConnBSelectAllQuery.executeQuery());
    }

    void inVisibleAfterCommit() throws SQLException {
        resetContext();
        Connection connA = getConnection(Connection.TRANSACTION_READ_COMMITTED);
        Connection connB = getConnection(Connection.TRANSACTION_READ_COMMITTED);
        Connection observer = getConnection(Connection.TRANSACTION_READ_COMMITTED);

        String insertSql = "INSERT INTO `student` (`student_name`, `student_number`) VALUES ('alice', 1)";
        observer.prepareStatement(insertSql).execute();
        observer.commit();
        Utils.printAction("INSERT_A_ROW", insertSql);

        String bModifySql = "UPDATE `student` SET `student_name` = 'modified_by_b' WHERE `student_number` = 1";
        connB.prepareStatement(bModifySql).execute();
        Utils.printAction("MODIFIED_BY_B", bModifySql);

        String aModifySql = "UPDATE `student` SET `student_name` = 'modified_by_a' WHERE `student_number` = 1";
        connA.prepareStatement(aModifySql).execute();
        Utils.printAction("MODIFIED_BY_A", aModifySql);

        connA.commit();
        connB.commit();

        String observerSql = "SELECT * FROM `student`";
        Utils.printResultSet("SELECT RESULT", observer.prepareStatement(observerSql).executeQuery());
    }

    void nonRepeatableRead(int isolationLevel) throws SQLException {
        resetContext();
        Connection connA = getConnection(isolationLevel);
        Connection connB = getConnection(isolationLevel);

        //CONNECTION_B_READ
        PreparedStatement ConnBSelectAllQuery = connB.prepareStatement("SELECT * FROM `student`");
        Utils.printResultSet("CONNECTION_B_READ", ConnBSelectAllQuery.executeQuery());

        //CONNECTION_A_INSERT
        String insertSql = "INSERT INTO `student` (`student_name`, `student_number`) VALUES ('alice', 1)";
        connA.prepareStatement(insertSql).execute();
        Utils.printAction("CONNECTION_A_INSERT", insertSql);

        //CONNECTION_A_COMMIT
        connA.commit();
        Utils.printAction("CONNECTION_A_COMMIT", "");

        //CONNECTION_B_READ_AGAIN
        Utils.printResultSet("CONNECTION_B_READ_AGAIN", ConnBSelectAllQuery.executeQuery());
    }

//    void lockingRead(int isolationLevel) throws SQLException {
//        Connection connA = getConnection(isolationLevel);
//        Connection connB = getConnection(isolationLevel);
//
//        // CONNECTION_A SELECT FOR UPDATE
//        String selectForUpdateSql = "SELECT * FROM `student` where student_number > 1 FOR UPDATE";
//        connA.prepareStatement(selectForUpdateSql).execute();
//        Utils.printAction("CONNECTION_A_SELECT_FOR_UPDATE");
//    }
}

