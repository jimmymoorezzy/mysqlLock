import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

class ExampleTest {
    Example example = new Example("localhost", 3306, "school", "root", "rootroot");

    ExampleTest() throws SQLException {
    }

    @Test
    void dirtyReadScenarioInReadUncommitted() throws SQLException {
        System.out.println("ISOLATION LEVEL: READ_UNCOMMITTED");
        example.dirtyRead(Connection.TRANSACTION_READ_UNCOMMITTED);
    }

    @Test
    void dirtyReadScenarioInReadCommitted() throws SQLException {
        System.out.println("ISOLATION LEVEL: READ_COMMITTED");
        example.dirtyRead(Connection.TRANSACTION_READ_COMMITTED);
    }

    @Test
    void nonRepeatableReadScenarioInReadCommitted() throws SQLException {
        System.out.println("ISOLATION LEVEL: READ_COMMITTED");
        example.nonRepeatableRead(Connection.TRANSACTION_READ_COMMITTED);
    }

    @Test
    void nonRepeatableReadScenarioInReadableRead() throws SQLException {
        System.out.println("ISOLATION LEVEL: REPEATABLE_READ");
        example.nonRepeatableRead(Connection.TRANSACTION_REPEATABLE_READ);
    }

    @Test
    void inVisibleAfterCommitScenarioInReadCommitted() throws SQLException {
        example.inVisibleAfterCommit();
    }
}