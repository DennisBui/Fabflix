import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mysql.jdbc.CallableStatement;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

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
 * Servlet implementation class DashboardServlet
 */
@WebServlet(name = "DashboardServlet", urlPatterns = "/api/_dashboard")
public class DashboardServlet extends HttpServlet
{
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
	
	private void addStar(PrintWriter out, String new_star_name, String new_star_birth)
	{
		try 
		{
			Connection dbcon = dataSource.getConnection();
			String new_max_id = "";
			String updateQuery = "";
			
			// Find the number of stars currently in the database
			String star_id_query = "SELECT max(id) FROM stars";
			
			System.out.println(star_id_query);
			
			PreparedStatement aStatement = dbcon.prepareStatement(star_id_query);
			ResultSet rs = aStatement.executeQuery();
			JsonArray jsonArray = new JsonArray();
			
			// ResultSet will have the highest id number of stars starting with nm + integer
			while(rs.next())
			{
			new_max_id = rs.getString("max(id)");
			}
			
			// Must parse the string by beginning the index after nbm
			String tempString = new_max_id.substring(2);
			Integer result = Integer.valueOf(tempString);
			result += 1;
			new_max_id = "nm" + result.toString();
			
			System.out.printf("The new added id is %s", new_max_id);
			
			PreparedStatement bStatement = null;
			if(new_star_birth.isEmpty() == true)
			{
				updateQuery = "INSERT INTO stars (id, name, birthYear) VALUES (?, ?, NULL)";
				
				bStatement = dbcon.prepareStatement(updateQuery);
				
				// Inserting mandatory values into query
				bStatement.setString(1, new_max_id);
				bStatement.setString(2, new_star_name); 
				
				// Because executeQuery only will work for SELECT statements
				bStatement.executeUpdate();
			}
			else
			{
				updateQuery = "INSERT INTO stars (id, name, birthYear) VALUES (?, ?, ?)";
				
				bStatement = dbcon.prepareStatement(updateQuery);
				
				// Inserting all values into query
				bStatement.setString(1, new_max_id);
				bStatement.setString(2, new_star_name);
				
				Integer tempInt = Integer.valueOf(new_star_birth);
				bStatement.setInt(3, tempInt);
				
				// Because executeQuery only will work for SELECT statements
				bStatement.executeUpdate();
			}
			
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("status", "success");
			jsonObject.addProperty("message", "success");
			jsonArray.add(jsonObject);
			out.write(jsonArray.toString());
			
			rs.close();
			aStatement.close();
			bStatement.close();
			dbcon.close();
			
		}
		catch(Exception e)
		{
			JsonObject jsonObject = new JsonObject();
			JsonArray jsonArray = new JsonArray();
			jsonObject.addProperty("errorMessage", e.getMessage());
			jsonArray.add(jsonObject);
			out.write(jsonArray.toString());
		}
		
	}
	
	private void addMovie(PrintWriter out, String new_movie_title, String new_release_year, String new_director,
			String new_genre, String new_star_name, String new_star_birth)
	{
		ArrayList<String> genreList = new ArrayList<String>();
		
		String [] genreArr = {"Action", "Adult", "Adventure", "Animation", "Biography", "Comedy", "Crime", 
		            	"Documentary", "Drama", "Family", "Fantasy", "History", "Horror", "Music", "Musical", 
		            	"Mystery", "Reality-TV", "Romance", "Sci-Fi", "Sport", "Thriller", "War", "Western"};
		
		for(int i = 0; i < 23; i++)
		{
			genreList.add(genreArr[i]);
		}
		try
		{			
			Connection dbcon = dataSource.getConnection();
			String new_max_movie = "";
			String new_max_id = "";
			String aupdateQuery = "";
			String bupdateQuery = "";
			String cupdateQuery = "";
			
			// Find the number of stars and movies currently in the database
			
			String dataMax1 = "SELECT max(movie.id) FROM movies movie";
			String dataMax2 = "SELECT max(star.id) FROM stars star";
			
			System.out.println(dataMax1);
			
			
			PreparedStatement aStatement = dbcon.prepareStatement(dataMax1);
			PreparedStatement testStatement = dbcon.prepareStatement(dataMax2);
			
			PreparedStatement bStatement = null;
			PreparedStatement cStatement = null;
			PreparedStatement dStatement = null;
			
			/*
			Statement xcStatement = null;
			Statement xdStatement = null;
			*/
			
			// Searching for current maximum IDs
			ResultSet rs = aStatement.executeQuery();
			ResultSet rsTest = testStatement.executeQuery();
			JsonArray jsonArray = new JsonArray();
			
			while(rs.next())
			{
				System.out.println("Check movie max ID");
				new_max_movie = rs.getString("max(movie.id)");
			}
			
			while(rsTest.next())
			{
				System.out.println("Check star max ID");
				new_max_id = rsTest.getString("max(star.id)");
			}
			
			//Updating movie ID first
			String tempMovieString = new_max_movie.substring(2);
			Integer movieRes = Integer.valueOf(tempMovieString);
			movieRes += 1;
			new_max_movie = "tt" + movieRes.toString();
			
			//Updating star ID next
			String tempStarString = new_max_id.substring(2);
			Integer starRes = Integer.valueOf(tempStarString);
			movieRes += 1;
			new_max_id = "nm" + starRes.toString();
			
			if(new_genre.isEmpty() == false && new_star_name.isEmpty() == false)
			{	
				aupdateQuery = "INSERT INTO movies(id, title, year, director) VALUES (?, ?, ?, ?)";
				bStatement = dbcon.prepareStatement(aupdateQuery);
				
				bStatement.setString(1, new_max_movie);
				bStatement.setString(2, new_movie_title);
				bStatement.setString(3, new_release_year);
				bStatement.setString(4, new_director);
				
				String queryCheck = "SELECT title, year, director FROM movies WHERE title = ? AND year = ? AND director = ?";
				PreparedStatement checker = dbcon.prepareStatement(queryCheck);
				
				checker.setString(1, new_movie_title);
				checker.setString(2, new_release_year);
				checker.setString(3, new_director);
				ResultSet resCheck = checker.executeQuery();
				
				if(resCheck.next())
				{
					System.out.println("The movie is already in the database");
					JsonObject checkJson = new JsonObject();
					String invString = "The movie is already in the database";
					checkJson.addProperty("message", invString);
					out.write(checkJson.toString());
					return;
				}
				else
				{
				bupdateQuery = "INSERT INTO stars_in_movies(starId, movieId) VALUES (?, ?)";
				cStatement = dbcon.prepareStatement(bupdateQuery);
				
				cStatement.setString(1, new_max_id);
				cStatement.setString(2, new_max_movie);
				
				Integer conGenre = genreList.indexOf(new_genre) + 1;
				System.out.println("The genre id is" + conGenre);
				cupdateQuery = "INSERT INTO genres_in_movies(genreId, movieId) VALUES (?, ?)";
				dStatement = dbcon.prepareStatement(cupdateQuery);
				dStatement.setInt(1, conGenre);
				dStatement.setString(2, new_max_movie);
				
				// bStatement.executeUpdate();
				
				JsonObject BcheckJson = new JsonObject();
				String vString = "Movie has been added";
				BcheckJson.addProperty("status", "success");
				BcheckJson.addProperty("message", vString);
				out.write(BcheckJson.toString());
				
				// cStatement.executeUpdate();
				// dStatement.executeUpdate();
				
				
				/*
				String bprocQuery = "CREATE PROCEDURE add-movie (IN new_movie_title VARCHAR(10), IN new_release_year INT(11), IN new_director VARCHAR(100), IN new_genre INT(11), IN new_star_id VARCHAR(10), IN new_movie_id VARCHAR(10))"
						+ "BEGIN INSERT INTO movies(id, title, year, director) VALUES (new_movie_id, new_movie_title, new_release_year, new_director);"
						+ "INSERT INTO stars_in_movies(starId, movieId) VALUES (new_star_id, new_movie_id);"
						+ "INSERT INTO genres_in_movies(genreId, movieId) VALUES (new_genre, new_movie_id);"
						+ "END";
				
				System.out.println("Checkuh1");
				Statement xbStatement = dbcon.createStatement();
				System.out.println("Checkuh2");
				ResultSet rsStore = xbStatement.executeQuery("SELECT name FROM mysql.proc WHERE name = 'add_movie'");
				System.out.println("Checkuh3");
				
				if(!rsStore.next())
				{
					System.out.println("Checkuh4");
					xbStatement.execute(bprocQuery);
					System.out.println("Checkuh");
					
					CallableStatement cs;
					
					cs = (CallableStatement) dbcon.prepareCall("{CALL add_movie(?,?,?,?,?,?}");
					cs.setString("new_movie_title", new_movie_title);
					cs.setString("new_release_year", new_release_year);
					cs.setString("new_director", new_director);
					cs.setInt("new_genre", conGenre);
					cs.setString("new_star_id", new_max_id);
					cs.setString("new_movie_id", new_max_movie);
					cs.execute();
				}
				*/
					
				cStatement.close();
				dStatement.close();
				
				//xcStatement.close();
				//xdStatement.close();
				
				}
				bStatement.close();
				//xbStatement.close();
			
				
			}
			else if(new_genre.isEmpty() == false && new_star_name.isEmpty() == true)
			{
				aupdateQuery = "INSERT INTO movies(id, title, year, director) VALUES (?, ?, ?, ?)";
				bStatement = dbcon.prepareStatement(aupdateQuery);
				
				bStatement.setString(1, new_max_movie);
				bStatement.setString(2, new_movie_title);
				bStatement.setString(3, new_release_year);
				bStatement.setString(4, new_director);
				
				String queryCheck = "SELECT title, year, director FROM movies WHERE title = ? AND year = ? AND director = ?";
				PreparedStatement checker = dbcon.prepareStatement(queryCheck);
				
				checker.setString(1, new_movie_title);
				checker.setString(2, new_release_year);
				checker.setString(3, new_director);
				ResultSet resCheck = checker.executeQuery();
				
				if(resCheck.next())
				{
					System.out.println("The movie is already in the database");
					JsonObject checkJson = new JsonObject();
					String invString = "The movie is already in the database";
					checkJson.addProperty("message", invString);
					out.write(checkJson.toString());
					return;
				}
				else
				{
					bStatement.executeUpdate();
					
					JsonObject BcheckJson = new JsonObject();
					String vString = "Movie has been added";
					BcheckJson.addProperty("status", "success");
					BcheckJson.addProperty("message", vString);
					out.write(BcheckJson.toString());
	
				Integer conGenre = genreList.indexOf(new_genre) + 1;
				System.out.println("The genre id is" + conGenre);
				cupdateQuery = "INSERT INTO genres_in_movies(genreId, movieId) VALUES (?, ?)";
				dStatement = dbcon.prepareStatement(cupdateQuery);
				dStatement.setInt(1, conGenre);
				dStatement.setString(2, new_max_movie);
				
				dStatement.executeUpdate();
				
				dStatement.close();
				}
				
				bStatement.close();
			}
			else if(new_genre.isEmpty() == true && new_star_name.isEmpty() == false)
			{
				aupdateQuery = "INSERT INTO movies(id, title, year, director) VALUES (?, ?, ?, ?)";
				bStatement = dbcon.prepareStatement(aupdateQuery);
				
				bStatement.setString(1, new_max_movie);
				bStatement.setString(2, new_movie_title);
				bStatement.setString(3, new_release_year);
				bStatement.setString(4, new_director);
				
				String queryCheck = "SELECT title, year, director FROM movies WHERE title = ? AND year = ? AND director = ?";
				PreparedStatement checker = dbcon.prepareStatement(queryCheck);
				
				checker.setString(1, new_movie_title);
				checker.setString(2, new_release_year);
				checker.setString(3, new_director);
				ResultSet resCheck = checker.executeQuery();
				
				if(resCheck.next())
				{
					System.out.println("The movie is already in the database");
					JsonObject checkJson = new JsonObject();
					String invString = "The movie is already in the database";
					checkJson.addProperty("message", invString);
					out.write(checkJson.toString());
					return;
				}
				else
				{
					bStatement.executeUpdate();
					
					JsonObject BcheckJson = new JsonObject();
					String vString = "Movie has been added";
					BcheckJson.addProperty("status", "success");
					BcheckJson.addProperty("message", vString);
					out.write(BcheckJson.toString());
				
				
				bupdateQuery = "INSERT INTO stars_in_movies(starId, movieId) VALUES (?, ?)";
				cStatement = dbcon.prepareStatement(bupdateQuery);
				
				cStatement.setString(1, new_max_id);
				cStatement.setString(2, new_max_movie);
				
				cStatement.executeUpdate();
				
				cStatement.close();
				}
				
				bStatement.close();	
			}
			else if(new_genre.isEmpty() == true && new_star_name.isEmpty() == true)
			{
				aupdateQuery = "INSERT INTO movies(id, title, year, director) VALUES (?, ?, ?, ?)";
				bStatement = dbcon.prepareStatement(aupdateQuery);
				
				bStatement.setString(1, new_max_movie);
				bStatement.setString(2, new_movie_title);
				bStatement.setString(3, new_release_year);
				bStatement.setString(4, new_director);
				
				String queryCheck = "SELECT title, year, director FROM movies WHERE title = ? AND year = ? AND director = ?";
				PreparedStatement checker = dbcon.prepareStatement(queryCheck);
				
				checker.setString(1, new_movie_title);
				checker.setString(2, new_release_year);
				checker.setString(3, new_director);
				ResultSet resCheck = checker.executeQuery();
				
				if(resCheck.next())
				{
					System.out.println("The movie is already in the database");
					JsonObject checkJson = new JsonObject();
					String invString = "The movie is already in the database";
					checkJson.addProperty("message", invString);
					out.write(checkJson.toString());
					return;
				}
				else
				{
					bStatement.executeUpdate();
					
					JsonObject BcheckJson = new JsonObject();
					String vString = "Movie has been added";
					BcheckJson.addProperty("status", "success");
					BcheckJson.addProperty("message", vString);
					out.write(BcheckJson.toString());
				
				}
				bStatement.close();
				
			}
			aStatement.close();
			dbcon.close();
			
		}
		catch(Exception e)
		{
			JsonObject jsonObject = new JsonObject();
			JsonArray jsonArray = new JsonArray();
			jsonObject.addProperty("errorMessage", e.getMessage());
			jsonArray.add(jsonObject);
			out.write(jsonArray.toString());
		}
	}
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		
		String new_movie_title = request.getParameter("new_movie_title");
		String new_release_year = request.getParameter("new_release_year");
		String new_director = request.getParameter("new_director");
		String new_genre = request.getParameter("new_genre");
		String new_star_name = request.getParameter("new_star_name");
		String new_star_birth = request.getParameter("new_star_birth");
		
		PrintWriter out = response.getWriter();
		
		// If only a star is added
		if(new_movie_title.isEmpty() == true && new_release_year.isEmpty() == true && new_director.isEmpty() == true && new_genre.isEmpty() == true && new_star_name.isEmpty() == false)
		{
			System.out.println("please1");
			addStar(out, new_star_name, new_star_birth);
		}
		else if(new_movie_title.isEmpty() == false && new_release_year.isEmpty() == false && new_director.isEmpty() == false && new_star_name.isEmpty() == true)
		{
			System.out.println("please2");
			addMovie(out, new_movie_title, new_release_year, new_director, new_genre, new_star_name, new_star_birth);
		}
		else if(new_movie_title.isEmpty() == false && new_release_year.isEmpty() == false && new_director.isEmpty() == false && new_star_name.isEmpty() == false)
		{
			System.out.println("please3");
			addMovie(out, new_movie_title, new_release_year, new_director, new_genre, new_star_name, new_star_birth);
			addStar(out, new_star_name, new_star_birth);
		}
		else
		{
			JsonObject jsonObject = new JsonObject();
			String vString = "Some required fields are not filled";
			jsonObject.addProperty("status", "fail");
			jsonObject.addProperty("message", vString);
			out.write(jsonObject.toString());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);
		}	
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		doGet(request, response);
	}
}