import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase {

    private static Connection conn;

    private DataBase(){
    }

    public static Connection getConn() {
        if(conn == null){
            String connString = "jdbc:mysql://localhost:3306/users?useSSL=false&characterEncoding=utf8";
            String username = "root";
            String password = "huckgyhv1";
            try {
                DriverManager.registerDriver(new com.mysql.jdbc.Driver());
                conn = DriverManager.getConnection(connString, username, password);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return conn;
    }

    public static void close(){
        if (conn != null) {
            try {
                conn.close();
                conn = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}