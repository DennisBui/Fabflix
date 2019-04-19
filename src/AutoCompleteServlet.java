import java.io.IOException;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@WebServlet("/AutoCompleteServlet")
public class AutoCompleteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		
		try {
			Connection dbcon = dataSource.getConnection();
			
			String query = request.getParameter("query");
			
			JsonArray jsonArray = new JsonArray();
			
			if (query == null || query.trim().isEmpty() || query.length() < 3) {
				response.getWriter().write(jsonArray.toString());
				return;
			}	
			
			String autoQuery = "SELECT * FROM movies WHERE MATCH(title) AGAINST(? IN BOOLEAN MODE) limit 10";
			System.out.println(autoQuery);
			
			PreparedStatement statement = dbcon.prepareStatement(autoQuery);
			
			String movieQuery = query.trim();
			String movieSplitQuery[] = movieQuery.split(" ");
			String finalString = "";
			for(int i = 0;i < movieSplitQuery.length; i++)
			{
				finalString += "+" + movieSplitQuery[i] + "*";
			}
			
			statement.setString(1, finalString);
			
			ResultSet rs = statement.executeQuery();
						
			System.out.println(query);
			
			while(rs.next()) {
				String movie_title = rs.getString("title");
				String movie_id = rs.getString("id");
				
				
				JsonObject jsonObject = new JsonObject();
				JsonObject otherJson = new JsonObject();
				
				jsonObject.addProperty("value", movie_title);
				
				otherJson.addProperty("type", "Movies");
				otherJson.addProperty("id", movie_id);
				
				jsonObject.add("data", otherJson);
				
				jsonArray.add(jsonObject);
				
			}
			
			out.write(jsonArray.toString());
			response.setStatus(200);
			rs.close();
			statement.close();
			dbcon.close();
			return;
			
		} catch (Exception e) {
			JsonArray jsonArray = new JsonArray();
			JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            jsonArray.add(jsonObject);
            out.write(jsonArray.toString());
            
            response.setStatus(500);
		}
	}

}