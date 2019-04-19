import com.google.gson.JsonObject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Servlet implementation class IndexServlet
 */
@WebServlet(name = "IndexServlet", urlPatterns = "/api/indexServlet")
public class IndexServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String itemName = request.getParameter("itemName");
        String logout = request.getParameter("logout");
        String quantity = request.getParameter("quantity");
        String deleteItemName = request.getParameter("deleteItemName");
        
        System.out.println("itemName=" + itemName);
        System.out.println("logout=" + logout);
        System.out.println("quantity=" + quantity);
        System.out.println("deleteItemName=" + deleteItemName);
        
        HttpSession session = request.getSession();
        
        if (logout != null) {  
            session.invalidate();
            response.getWriter().write("getout");
            return; // <--- Here.
        }
        
        // get the previous items in a ArrayList
		ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");
        if (previousItems == null) {
            previousItems = new ArrayList<>();
        	if (itemName != null) {
        		boolean repeat = false;
        		for (int i = 0; i < previousItems.size(); i++) {
        			if (previousItems.get(i).split("-")[0].contentEquals(itemName)) {
        				repeat = true;
        				System.out.println(previousItems.get(i).split("-")[0]);
        				previousItems.set(i, itemName+'-'+quantity);
        			}
        		}
        		if (!repeat)
        			previousItems.add(itemName+'-'+quantity);
        	}
        	if (deleteItemName != null) {
        		boolean removeItem = false;
        		for (int i = 0; i < previousItems.size(); i++) {
        			if (previousItems.get(i).split("-")[0].contentEquals(deleteItemName)) {
        				removeItem = true;
        				System.out.println("remove " + previousItems.get(i).split("-")[0]);
        				previousItems.remove(i);
        			}
        		}
        		if (!removeItem)
        			previousItems.add(itemName+'-'+quantity);
        	}
            session.setAttribute("previousItems", previousItems);
        } else {
            // prevent corrupted states through sharing under multi-threads
            // will only be executed by one thread at a time
            synchronized (previousItems) {
            	if (itemName != null) {
            		boolean repeat = false;
            		for (int i = 0; i < previousItems.size(); i++) {
            			if (previousItems.get(i).split("-")[0].contentEquals(itemName)) {
            				repeat = true;
            				System.out.println(previousItems.get(i).split("-")[0]);
            				previousItems.set(i, itemName+'-'+quantity);
            			}
            		}
            		if (!repeat)
            			previousItems.add(itemName+'-'+quantity);
            	}
            	if (deleteItemName != null) {
            		boolean removeItem = false;
            		for (int i = 0; i < previousItems.size(); i++) {
            			if (previousItems.get(i).split("-")[0].contentEquals(deleteItemName)) {
            				removeItem = true;
            				System.out.println("remove " + previousItems.get(i).split("-")[0]);
            				previousItems.remove(i);
            			}
            		}
            		if (!removeItem)
            			previousItems.add(itemName+'-'+quantity);
            	}
            }
        }
        
        System.out.print("{ ");
        for (String i : previousItems) {
        	System.out.print(i + ", ");
        }
        System.out.println(" }");
        System.out.println("String.join(\",\", previousItems)=" + String.join(",", previousItems));
        
        response.getWriter().write(String.join(",", previousItems));
        
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        Long lastAccessTime = session.getLastAccessedTime();

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("sessionID", sessionId);
        responseJsonObject.addProperty("lastAccessTime", new Date(lastAccessTime).toString());

        // write all the data into the jsonObject
        response.getWriter().write(responseJsonObject.toString());
	}

}
