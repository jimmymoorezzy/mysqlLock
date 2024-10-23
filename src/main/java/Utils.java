import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class Utils {
    static final String formElementFormat = "%10s\t";

    static void printResultSet(String title, ResultSet resultSet) throws SQLException {
        System.out.println(formatTitle(title, "-"));

        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnsNumber = metaData.getColumnCount();
        for (int i = 1; i <= columnsNumber; i++) {
            System.out.printf(formElementFormat, metaData.getColumnName(i));
        }
        System.out.println();

        while (resultSet.next()) {
            for (int i = 1; i <= columnsNumber; i++) {
                String columnValue = resultSet.getString(i);
                System.out.printf(formElementFormat, columnValue);
            }
            System.out.println();
        }
        System.out.println();
    }

    static String formatTitle(String title, String padding) {
        int length = 60;
        int leftPadding = (length - title.length()) / 2;
        int rightPadding = length - title.length() - leftPadding;
        return padding.repeat(Math.max(0, leftPadding)) +
                title +
                padding.repeat(Math.max(0, rightPadding));
    }

    static void printAction(String title, String content) {
        System.out.printf("%s\n%s\n\n", formatTitle(title, "#"), content);
    }
}
