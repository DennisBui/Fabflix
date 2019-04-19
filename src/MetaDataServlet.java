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
 * Servlet implementation class MetaDataServlet
 */
@WebServlet(name = "/MetaDataServlet", urlPatterns = "/api/metadata")
public class MetaDataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	   /**
     * @see HttpServlet#HttpServlet()
     */

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		
		PrintWriter out = response.getWriter();
		
		try 
		{
			Connection dbcon = dataSource.getConnection();
			String query = "SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE " +
							"FROM INFORMATION_SCHEMA.COLUMNS " +
							"WHERE table_schema = 'moviedb'";
			
			System.out.println(query);
			
			PreparedStatement statement = dbcon.prepareStatement(query);
			
			ResultSet rs = statement.executeQuery();
			JsonArray jsonArray = new JsonArray();
			String table_name = "";
			String column_name = "";
			String data_type = "";
			JsonObject jsonObject = new JsonObject();
			
			while(rs.next())
			{
				jsonObject.addProperty("status","success");
				
				if (table_name.equalsIgnoreCase("")){
    				
    				table_name = rs.getString("table_name");
        			column_name = rs.getString("column_name");
        			data_type = rs.getString("data_type");
        			
    				
    			}
				else if(table_name.equalsIgnoreCase(rs.getString("table_name"))) 
				{

        			column_name += "<br>" + rs.getString("column_name");
        			data_type += "<br>" + rs.getString("data_type");

        		}
				else if(!table_name.equalsIgnoreCase(rs.getString("table_name")))
				{
        			
        			jsonObject.addProperty("table_name", table_name);
    				jsonObject.addProperty("column_name", column_name);
    				jsonObject.addProperty("data_type", data_type);
    				jsonArray.add(jsonObject);
    				
        			jsonObject = new JsonObject();
        			table_name = rs.getString("table_name");
        			column_name = rs.getString("column_name");
        			data_type = rs.getString("data_type");    				
    				
        		}
				
			}
			out.write(jsonArray.toString());
			response.setStatus(200);
    		rs.close();
    		statement.close();
    		dbcon.close();
		}
		catch(Exception e)
		{
			JsonArray jsonArray = new JsonArray();
			JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            jsonArray.add(jsonObject);
            out.write(jsonArray.toString());
            
            response.setStatus(500);
		}
	}
	
}

