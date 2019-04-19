import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
 * Servlet implementation class checkout
 */
@WebServlet(name = "checkout", urlPatterns = "/api/checkout")
public class checkout extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
//    public checkout() {
//        super();
//        // TODO Auto-generated constructor stub
//    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String cardNum = request.getParameter("cardNum");
		String firstName = request.getParameter("firstName");
		String lastName = request.getParameter("lastName");
		String date = request.getParameter("date");
		
		PrintWriter out = response.getWriter();
		
		System.out.println("cardNum=" + cardNum);
		System.out.println("firstName=" + firstName);
		System.out.println("lastName=" + lastName);
		System.out.println("date=" + date);
		
		try {
			Connection dbcon = dataSource.getConnection();
			String query = "select * from creditcards "
					+ "where creditcards.id = \"" + cardNum + "\"\r\n" + 
					"AND creditcards.firstName = \"" + firstName + "\"\r\n" + 
					"AND creditcards.lastName = \"" + lastName + "\"\r\n" + 
					"AND creditcards.expiration = \"" + date + "\";";
			
			System.out.println(query);
			
			PreparedStatement statement = dbcon.prepareStatement(query);
			ResultSet rs = statement.executeQuery();
			
			if (!rs.next()) {
				JsonObject responseJsonObject = new JsonObject();
				responseJsonObject.addProperty("status", "fail");
				responseJsonObject.addProperty("message", "Your information is not existed in our database.");
				response.getWriter().write(responseJsonObject.toString());
			}
			else 
			{
				JsonObject responseJsonObject = new JsonObject();
				responseJsonObject.addProperty("status", "success");
				responseJsonObject.addProperty("message", "Thank you for your order.");
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
