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

/**
 * Servlet implementation class SingleStarServlet
 */
@WebServlet(name = "SingleStarServlet", urlPatterns = "/api/single-star")
public class SingleStarServlet extends HttpServlet {
	private static final long serialVersionUID = 3L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
//    public SingleStarServlet() {
//        super();
//        // TODO Auto-generated constructor stub
//    }

	@Resource(name ="jdbc/moviedb")
	private DataSource dataSource;
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("application/json");
		
		String id = request.getParameter("id");
		PrintWriter out = response.getWriter();
		
		try {
			Connection dbcon = dataSource.getConnection();
			String query = "SELECT stars.name, stars.birthYear, \r\n" + 
					"group_concat(distinct movies.title SEPARATOR ', ') AS listMoviesTitle,\r\n" + 
					"group_concat(distinct movies.id ORDER BY movies.title SEPARATOR ', ') AS listMoviesId\r\n" + 
					"FROM stars\r\n" + 
					"INNER JOIN stars_in_movies ON stars_in_movies.starId = stars.id\r\n" + 
					"INNER JOIN movies ON stars_in_movies.movieId = movies.id\r\n" + 
					"WHERE stars.id = ?\r\n" + 
					"group by stars.id;";
			PreparedStatement statement = dbcon.prepareStatement(query);
			statement.setString(1, id);
			
			ResultSet rs = statement.executeQuery();
			JsonArray jsonArray = new JsonArray();
			
			while (rs.next()) {
				String starName = rs.getString("name");
				String starBirthYear = rs.getString("birthYear");
				String movieListTitle = rs.getString("listMoviesTitle");
				String movieListId = rs.getString("listMoviesId");
				
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("name", starName);
				jsonObject.addProperty("birthYear", starBirthYear);
				jsonObject.addProperty("listMoviesTitle", movieListTitle);
				jsonObject.addProperty("listMoviesId", movieListId);
				
				jsonArray.add(jsonObject);
			}
			out.write(jsonArray.toString());
			response.setStatus(200);
			rs.close();
			statement.close();
			dbcon.close();
		} catch (Exception e) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);
		}
//		// TODO Auto-generated method stub
//		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
