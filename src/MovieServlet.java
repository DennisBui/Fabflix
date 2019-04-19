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
import java.sql.ResultSet;
import java.sql.Statement;


/**
 * Servlet implementation class MovieServlet
 */
@WebServlet(name = "MovieServlet", urlPatterns = "/api/movies")
public class MovieServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;
    /**
     * @see HttpServlet#HttpServlet()
     */
//    public MovieServlet() {
//        super();
//        // TODO Auto-generated constructor stub
//    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		
		try {
			Connection dbcon = dataSource.getConnection();
			Statement statement = dbcon.createStatement();
			String query = "select distinct movies.id as movie_id, movies.title, movies.year, movies.director, rating, \r\n" + 
					"group_concat(distinct genres.name SEPARATOR ', ') AS listGenres, \r\n" + 
					"group_concat(distinct stars.id ORDER BY stars.name separator ', ') AS listStarsId,\r\n" + 
					"group_concat(distinct stars.name SEPARATOR ', ') as listStarsName\r\n" + 
					"from ratings, movies\r\n" + 
					"inner join genres_in_movies on movies.id = genres_in_movies.movieId\r\n" + 
					"inner join genres on genres.id = genres_in_movies.genreId\r\n" + 
					"inner join stars_in_movies on movies.id = stars_in_movies.movieId \r\n" + 
					"inner join stars on stars.id = stars_in_movies.starId\r\n" + 
					"where ratings.movieId = movies.id\r\n" + 
					"group by movies.id\r\n" + 
					"order by rating desc\r\n" + 
					"limit 20;";
			
			ResultSet rs = statement.executeQuery(query);
			JsonArray jsonArray = new JsonArray();
			
			while (rs.next()) {
				String movie_id = rs.getString("movie_id");
				String movie_title = rs.getString("title");
				String movie_year = rs.getString("year");
				String movie_director = rs.getString("director");
				String movie_genres = rs.getString("listGenres");
				String movieStarsId = rs.getString("listStarsId");
				String movieStarsName = rs.getString("listStarsName");
				String movie_rating = rs.getString("rating");
				
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("movie_id", movie_id);
				jsonObject.addProperty("title", movie_title);
				jsonObject.addProperty("year", movie_year);
				jsonObject.addProperty("director", movie_director);
				jsonObject.addProperty("listGenres", movie_genres);
				jsonObject.addProperty("listStarsId", movieStarsId);
				jsonObject.addProperty("listStarsName", movieStarsName);
				jsonObject.addProperty("rating", movie_rating);
				
				jsonArray.add(jsonObject);
			}
			out.write(jsonArray.toString());
			response.setStatus(200);
		}
		catch (Exception e) {
			
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("errorMessage", e.getMessage());
		out.write(jsonObject.toString());

		response.setStatus(500);
		}
		
		// TODO Auto-generated method stub
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
