/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleLoginResult(resultDataJson) {
    //resultDataJson = JSON.parse(resultDataString);

    console.log("handle login response");
    console.log(resultDataJson[0]["status"]);
    
    // If login success, redirect to index.html page
    if (resultDataJson[0]["status"] === "success") {
    	console.log("Successfully inserted into database"); 
    }
    else {
        console.log("show error message");
        console.log(resultDataJson["message"]);
        $("#form_error_message").text(resultDataJson["message"]);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitLoginForm(formSubmitEvent) {
    console.log("submit employee login form");

    formSubmitEvent.preventDefault();

    jQuery.post(
        "api/_dashboard",
        // Serialize the login form to the data sent by POST request
        jQuery("#new_movie_form").serialize(),
        (resultDataJson) => handleLoginResult(resultDataJson));

}

// Bind the submit action of the form to a handler function
jQuery("#new_movie_form").submit((event) => submitLoginForm(event));


