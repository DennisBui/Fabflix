import com.google.gson.JsonArray;
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.HashMap;
import java.util.Map;

/**
 * Servlet implementation class searchByTitle
 */

@WebServlet(name = "searchByTitle", urlPatterns = "/api/searchBy")
public class searchResult extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	
	@Resource(name ="jdbc/moviedb")
	private DataSource dataSource;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		
		String searchResultByTitle = request.getParameter("title");
		String searchResultByYear = request.getParameter("year");
		String searchResultByDirector = request.getParameter("director");
		String searchResultByStarName = request.getParameter("starName");
		String sortType = request.getParameter("sort");
		String browse = request.getParameter("browse");
		String genre = request.getParameter("genre");
		String limit = request.getParameter("limit");
		String offset = request.getParameter("offset");
		
		
//		System.out.println(offset + ", " + limit);
		
		if (browse == null)
			browse = "";
		
		
		String search = "";
		String offsetLimitQuery = "";
		
		if (offset != null && limit != null)
		{
			offsetLimitQuery += "limit " + limit;
			offsetLimitQuery += " offset " + offset;
		}
		if (browse.equals("yes"))
		{
			System.out.print("yo we browsin");
			if (searchResultByTitle != null)
			{
				search = "movies.title like '%" + searchResultByTitle + "%'";
			}
		}
		else {
			if (searchResultByTitle != null)
				search = "movies.title like '%" + searchResultByTitle + "%'";
			if (searchResultByYear != null ) {
				if (search.length() != 0)
					search += "AND cast(movies.year as char) like '%" + searchResultByYear + "%'";
				else
					search = "cast(movies.year as char) like '%" + searchResultByYear + "%'";
			}
			if (searchResultByDirector != null)
				if (search.length() != 0)
					search += "AND movies.director like '%" + searchResultByDirector + "%'";
				else
					search = "movies.director like '%" + searchResultByDirector + "%'";
			if (searchResultByStarName != null)
				if (search.length() != 0)
					search += "AND stars.name like '%" + searchResultByStarName + "%'";
				else
					search = "stars.name like '%" + searchResultByStarName + "%'";
			if (genre != null)
				search = "genres.name = '" + genre + "'";
		}
	
		
		String querySort = "";
		String querySortHeader = "";
		if (sortType != null) {
			querySortHeader = "select t.* from (";
			if (sortType.equals("titleA")) 
				querySort = ") AS t \r\n order by t.title asc";
			else if (sortType.equals("titleD"))
				querySort = ") AS t \r\n order by t.title desc";
			else if (sortType.equals("ratingA"))
				querySort = ") AS t \r\n order by t.rating asc";
			else if (sortType.equals("ratingD"))
				querySort = ") AS t \r\n order by t.rating desc";
		}
		
		System.out.println(search);
		System.out.println(querySort);
		System.out.println(offsetLimitQuery);
		
		PrintWriter out = response.getWriter();
		
		try {
			
			long startTimeTS = System.nanoTime(); 
			
			
			Context initCtx = new InitialContext();
			Context envCtx =(Context) initCtx.lookup("java:comp/env");
			
			if(envCtx == null)
			{
				System.out.println("Lookup is null");
			}
			
			DataSource dataSource = (DataSource) envCtx.lookup("jdbc/TestDB");
			
			if(dataSource == null)
			{
				System.out.println("ds is null. ");
			}
			
			Connection dbcon = dataSource.getConnection();
			
			if (dbcon == null)
			{
				System.out.println("the connection is null.");
			}
			
			String query = 
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
				     
			PreparedStatement statement = dbcon.prepareStatement(query);
			
			String movieQuery = searchResultByTitle.trim();
			String movieSplitQuery[] = movieQuery.split(" ");
			String finalString = "";
			for(int i = 0;i < movieSplitQuery.length; ++i)
			{
				finalString += "+" + movieSplitQuery[i] + "*";
			}
			
			statement.setString(1, finalString);
			
			
			long startTime2 = System.nanoTime(
					);
			ResultSet rs = statement.executeQuery();
			
			long endTime2 = System.nanoTime();
			long TJ = endTime2 - startTime2;
    		
    		System.out.println(statement.toString());

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
				String totalRows = rs.getString("total_rows");
				
				
				
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("movie_id", movie_id);
				jsonObject.addProperty("title", movie_title);
				jsonObject.addProperty("year", movie_year);
				jsonObject.addProperty("director", movie_director);
				jsonObject.addProperty("listGenres", movie_genres);
				jsonObject.addProperty("listStarsId", movieStarsId);
				jsonObject.addProperty("listStarsName", movieStarsName);
				jsonObject.addProperty("rating", movie_rating);
				jsonObject.addProperty("total_rows", totalRows);
				
				jsonArray.add(jsonObject);
			}
			
			long endTimeTS = System.nanoTime();
    		long TS = endTimeTS - startTimeTS; // elapsed time in nano seconds. Note: print the values in nano seconds
    		
    		String str1 = Long.toString(TS);
    		String str2 = Long.toString(TJ);
    		
    		
    		String contextPath = getServletContext().getRealPath("/");

    		String xmlFilePath = contextPath + "logFile.txt"  ;

    		System.out.println("xmlFilePath is " + xmlFilePath);

    		File myfile = new File(xmlFilePath);

    		BufferedWriter writer = new BufferedWriter(new FileWriter(myfile, true));
    		
    		System.out.println(str1);
    		System.out.println(str2);
    		
    		writer.append(str1);
    		writer.append(' ');
    		writer.append(str2 + "\n");
			
    		writer.close();
			out.write(jsonArray.toString());
			response.setStatus(200);
			rs.close();
			statement.close();
			dbcon.close();
		}
		catch (Exception e)
		{
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
}
