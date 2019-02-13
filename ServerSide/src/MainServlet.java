import java.io.IOException;
import java.sql.*;

public class MainServlet extends javax.servlet.http.HttpServlet {

    public static final String WRONG_PARAMETER = "WP";
    public static final String OK = "OK";
    private User user;
    private Connection connection = null;

    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {

    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        String action = request.getParameter("action");
        try {
            switch(action) {
                case "signup":
                    String userAsString = request.getParameter("userAsString");
                    if(signup(userAsString))
                        response.getWriter().write(OK);
                    else
                        response.getWriter().write(WRONG_PARAMETER);
                    break;
                case "login":
                    String username = request.getParameter("username");
                    String password = request.getParameter("password");
                    if(!login(username,password))
                        response.getWriter().write(user.toString());
                    else
                        response.getWriter().write(WRONG_PARAMETER);
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean signup(String userAsString) throws SQLException {
        if(userAsString.isEmpty())
            return false; // User params are empty
        user = new User(userAsString);
        if(!user.isValid())
            return false; // User params are not valid
        connection = DataBase.getConn();
        if(exists(true, user.getUsername(), user.getPassword()))
            return false; // User already exists
        PreparedStatement statement = connection.prepareStatement("INSERT INTO userinfo(username,password,photoPath) VALUES (?, ?, ?)");
        statement.setString(1, user.getUsername());
        statement.setString(2, user.getPassword());
        statement.setString(3, user.getPhotoPath());
        statement.executeUpdate();
        statement.close();
        return true;
    }

    private boolean login(String usern, String passw) throws SQLException {
        System.out.println(usern + " " + passw);
        user = null;
        connection = DataBase.getConn();
        if(exists(false, usern, passw))
            return false;
        return true;
    }

    // signupOrLogin - true: sign up, false: login.
    private boolean exists(boolean signupOrLogin, String usern, String passw) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM userinfo");
        while (rs.next()) {
            String username = rs.getString("username");
            String password = rs.getString("password");
            String photoPath = rs.getString("photoPath");
            if(signupOrLogin){
                if(usern.equals(username))
                    return true;
            } else {
                if(usern.equals(username) && passw.equals(password)) {
                    user = new User(username,password,photoPath);
                    return true;
                }
            }
        }
        statement.close();
        return false;
    }

    @Override
    public void destroy() {
        super.destroy();
        DataBase.close();
    }
}
