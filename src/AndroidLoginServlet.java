import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.jasypt.util.password.StrongPasswordEncryptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

@WebServlet(name = "AndroidLoginServlet", urlPatterns = "/api/android-login")
public class AndroidLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

	// Create a dataSource which registered in web.xml
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;
	
    public AndroidLoginServlet() {
        super();
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	PrintWriter out = response.getWriter();
        String username = request.getParameter("username");
        String password = request.getParameter("password");
    
    	try {
    		
    		if (dataSource == null)
                System.out.println("ds is null.");
            
    		Connection connection = dataSource.getConnection();
    		
    		PreparedStatement statement = null;
    		
    		String query = "SELECT password"
    				+ " FROM customers"
    				+ " WHERE email = ?";
    		
    		statement = connection.prepareStatement(query);
    		
    		statement.setString(1, username);
    		
    		ResultSet rs = statement.executeQuery();
    		String encryptedPassword = "";
    		boolean correctPass = false;
    	
    		if(rs.next()) {
        		encryptedPassword = rs.getString("password");
        		correctPass = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
        		
        		rs.close();
        		statement.close();
        		connection.close();
    		}
    		
            if (rs.next() && correctPass) {
   
                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "success");
                responseJsonObject.addProperty("message", "success");
                
                request.getSession().setAttribute("user", new User(username));
                out.write(responseJsonObject.toString());
                
            } else {
                JsonObject responseJsonObject = new JsonObject();
                responseJsonObject.addProperty("status", "fail");
                if (!rs.next()) {
                    responseJsonObject.addProperty("message", "User does not exist");
                } else if (!correctPass) {
                    responseJsonObject.addProperty("message", "Incorrect password");
                }
                out.write(responseJsonObject.toString());
            }
            

        }catch(Exception e){
        	JsonObject responseJsonObject = new JsonObject();
            responseJsonObject.addProperty("message", e.getMessage());
            
            out.write(responseJsonObject.toString());
            
    		
        }
    }

}