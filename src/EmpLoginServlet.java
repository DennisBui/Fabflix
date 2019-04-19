
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.jasypt.util.password.StrongPasswordEncryptor;
import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.HashMap;
import java.util.Map;
/**
 * Servlet implementation class LoginServlet
 */
@WebServlet(name = "EmpLoginServlet", urlPatterns = "/api/emp-login")
public class EmpLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
//    public LoginServlet() {
//        super();
//        // TODO Auto-generated constructor stub
//    }
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
	    String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
		PrintWriter out = response.getWriter();
		System.out.println(username);
		System.out.println(password);
		System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);
        
		
		  try {
	            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
	        } catch (Exception e) {
	            out.println("<html>");
	            out.println("<head><title>Error</title></head>");
	            out.println("<body>");
	            out.println("<p>recaptcha verification error</p>");
	            out.println("<p>" + e.getMessage() + "</p>");
	            out.println("</body>");
	            out.println("</html>");
	            
	            out.close();
	            return;
	        }
	     
		try {
			Connection dbcon = dataSource.getConnection();
			String query = "SELECT password"
						+ " from employees"
						+ " where email = ?";
			
			PreparedStatement statement = dbcon.prepareStatement(query);
			statement.setString(1, username);
			
			ResultSet rs = statement.executeQuery();
			boolean success = false;
			String encryptedPassword = "";
			// credentials entered by the user does not match with credentials in the database
			if (!rs.next()) {
				JsonObject responseJsonObject = new JsonObject();
				responseJsonObject.addProperty("status", "fail");
				responseJsonObject.addProperty("message", "Either the username or password doesn't exist");
				response.getWriter().write(responseJsonObject.toString());
			}
			else
			{
	            String sessionId = ((HttpServletRequest) request).getSession().getId();
	            Long lastAccessTime = ((HttpServletRequest) request).getSession().getLastAccessedTime();
	            request.getSession().setAttribute("employee", new User(username));
	            
	            encryptedPassword = rs.getString("password");
	            System.out.println(password);
	            System.out.println(encryptedPassword);
				
				// use the same encryptor to compare the user input password with encrypted password stored in DB
				success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
	            
	            JsonObject responseJsonObject = new JsonObject();
	            responseJsonObject.addProperty("status", "success");
	            responseJsonObject.addProperty("message", "success");
	            response.getWriter().write(responseJsonObject.toString());
			}
		}
		catch (Exception e) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("errorMessage", e.getMessage());
		out.write(jsonObject.toString());

		response.setStatus(500);
		}
		
	}

}
