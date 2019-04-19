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
 * Servlet implementation class SingleMovieServlet
 */
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
	private static final long serialVersionUID = 2L;
	
	@Resource(name ="jdbc/moviedb")
	private DataSource dataSource;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
//    public SingleMovieServlet() {
//        super();
//        // TODO Auto-generated constructor stub
//    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		
		String id = request.getParameter("id");
		PrintWriter out = response.getWriter();
		
		try {
			Connection dbcon = dataSource.getConnection();
			String query = "SELECT movies.title, movies.year, movies.director,\r\n" + 
					"group_concat(distinct stars.id  ORDER BY stars.name separator ', ') AS  listStarsId,\r\n" + 
					"group_concat(distinct genres.name SEPARATOR ', ') AS listGenres, \r\n" + 
					"group_concat(distinct stars.name SEPARATOR ', ') AS listStars, ratings.rating \r\n" + 
					"FROM ratings, movies\r\n" + 
					"inner join genres_in_movies on genres_in_movies.movieId = movies.id\r\n" + 
					"inner join genres on genres.id = genres_in_movies.genreId\r\n" + 
					"inner join stars_in_movies on movies.id = stars_in_movies.movieId \r\n" + 
					"inner join stars on stars.id = stars_in_movies.starId\r\n" + 
					"WHERE ratings.movieId = movies.id AND movies.id = ?\r\n" + 
					"group by movies.id;";
			PreparedStatement statement = dbcon.prepareStatement(query);
			statement.setString(1, id);
			
			ResultSet rs = statement.executeQuery();
			JsonArray jsonArray = new JsonArray();
			
			while (rs.next()) {
				String movieTitle = rs.getString("title");
				String movieYear = rs.getString("year");
				String movieDirector = rs.getString("director");
				String listStarsId = rs.getString("listStarsId");
				String movie_genres = rs.getString("listGenres");
				String movie_stars = rs.getString("listStars");
				String movieRating = rs.getString("rating");
				
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("title", movieTitle);
				jsonObject.addProperty("year", movieYear);
				jsonObject.addProperty("director", movieDirector);
				jsonObject.addProperty("listStarsId", listStarsId);
				jsonObject.addProperty("listGenres", movie_genres);
				jsonObject.addProperty("listStars", movie_stars);
				jsonObject.addProperty("rating", movieRating);
				
				jsonArray.add(jsonObject);
			}
			out.write(jsonArray.toString());
			response.setStatus(200);
			rs.close();
			statement.close();
			dbcon.close();
		}
		catch (Exception e) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);
		}
		
		// TODO Auto-generated method stub
		// response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
