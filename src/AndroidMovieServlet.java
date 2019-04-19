import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

/**
 * Servlet implementation class AndroidMovieListServlet
 */
@WebServlet(name = "/AndroidMovieServlet", urlPatterns = "/api/android-view")
public class AndroidMovieServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	// Create a dataSource which registered in web.xml
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AndroidMovieServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String query = request.getParameter("query");
		JsonArray jsonArray = new JsonArray();
		PrintWriter out = response.getWriter();
        
		try {

            if (dataSource == null)
                System.out.println("ds is null.");
            
			Connection dbcon = dataSource.getConnection();
    		
    		String sql = 
					"	select distinct count(*)  AS total_rows, movies.id as movie_id, movies.title, movies.year, movies.director, rating, \r\n" + 
					"	group_concat(distinct genres.name SEPARATOR ', ') AS listGenres, \r\n" + 
					"	group_concat(distinct stars.id ORDER BY stars.name separator ', ') AS listStarsId,\r\n" + 
					"	group_concat(distinct stars.name SEPARATOR ', ') as listStarsName\r\n" + 
					"	from ratings, movies\r\n" + 
					"	inner join genres_in_movies on movies.id = genres_in_movies.movieId\r\n" + 
					"	inner join genres on genres.id = genres_in_movies.genreId\r\n" + 
					"	inner join stars_in_movies on movies.id = stars_in_movies.movieId \r\n" + 
					"	inner join stars on stars.id = stars_in_movies.starId\r\n" + 
					"	where ratings.movieId = movies.id AND MATCH (title) AGAINST (? IN BOOLEAN MODE)" + "\r\n" + 
					"	group by movies.id limit 50 offset 0; ";
    		
    		PreparedStatement statement = dbcon.prepareStatement(sql);
    		
    		String movieQuery = query.trim();
			String movieSplitQuery[] = movieQuery.split(" ");
			String finalString = "";
			for(int i = 0;i < movieSplitQuery.length; i++)
			{
				finalString += "+" + movieSplitQuery[i] + "*";
			}
    		
    		statement.setString(1, finalString);
    		
    		ResultSet rs = statement.executeQuery();
    		
    		while(rs.next()) {
    			String movie_id = rs.getString("movie_id");
				String movie_title = rs.getString("title");
				String movie_year = rs.getString("year");
				String movie_director = rs.getString("director");
				String movie_genres = rs.getString("listGenres");
				String movie_stars = rs.getString("listStarsName");
				String totalRows = rs.getString("total_rows");
				
				JsonObject responseJsonObject = new JsonObject();
				responseJsonObject.addProperty("status", "success");
				responseJsonObject.addProperty("movie_id", movie_id);
				responseJsonObject.addProperty("title", movie_title);
				responseJsonObject.addProperty("year", movie_year);
				responseJsonObject.addProperty("director", movie_director);
				responseJsonObject.addProperty("listGenres", movie_genres);
				responseJsonObject.addProperty("listStarsName", movie_stars);
				responseJsonObject.addProperty("total_rows", totalRows);
				
				jsonArray.add(responseJsonObject);
				
				out.write(responseJsonObject.toString());
				
				rs.close();
				statement.close();
				dbcon.close();
    		}
			
		}catch(Exception e) {
			// write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("status", "fail");
			jsonObject.addProperty("message", e.getMessage());

			jsonArray.add(jsonObject);
            response.getWriter().write(jsonArray.toString());
		}
	}


}