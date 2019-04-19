function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);

    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

$("#checkoutForm").submit(function(event) {
	submitLoginForm(event)
});

function submitLoginForm(formSubmitEvent) {
    console.log("submit login form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();
    
    $.post(
        "api/checkout",
        // Serialize the login form to the data sent by POST request
        $("#checkoutForm").serialize(),
        function(confirmation) {
        	handleConfirmationResult(confirmation)
        });
}


function handleConfirmationResult(confirmation) {
	
	confirmationJson = JSON.parse(confirmation);
	
    
    if (confirmationJson["status"] == "success"){
    	
    	window.location.href = "checkout.html?status=" + confirmationJson["status"] + "&message=" + confirmationJson["message"];
    }
    else if (confirmationJson["status"] == "fail") {
    	window.location.href = "checkout.html?status=" + confirmationJson["status"] + "&message=" + confirmationJson["message"];
    }

}


let status = getParameterByName('status');
let message = getParameterByName('message');

function handleConfirmationMovieResult(itemDetail){
	console.log(typeof itemDetail);
	if (itemDetail != ""){
		$("#hideMe").show();
	    let itemArr = itemDetail.split(',');
	    let obj = {};
	    
	    for (let i = 0; i < itemArr.length; i++){
	        let itemName = "";
	        let itemQty = "";
	        itemName = itemArr[i].split('-')[0];
	        itemQty = itemArr[i].split('-')[1];
	        
	        obj[itemName] = itemQty;
	    }
	    
		let orderDetails = jQuery("#orderDetails");
		
		Object.entries(obj).forEach(([key, val]) => {
		    console.log(key);          
		    console.log(val);
		    
		    let newHtml = "";
		    newHtml += "<h5 class=\"card-title\">" + key + "</h5>";
		    newHtml += "<p class=\"card-text\">Quantity: " + val + "</p>";
		    newHtml += "<hr>";
		    
		    orderDetails.append(newHtml);
		    
		});
	}
}

if (status == "success"){
	$("#removeMe").remove();
	jQuery.ajax({
		method: "GET",
		url: "api/indexServlet?",
		success: function(itemDetail) {
			handleConfirmationMovieResult(itemDetail)
			}
	}); 
	$("#bigText").text("Thank you for your order.");
	$("#smallText").text("Your order ID is ");
}
else if (status == "fail"){
	$("#removeMe").remove();
	$("#bigText").text("Something went wrong.");
	$("#smallText").text("Your information is incorrect or missing. Please check your credit card information.");
}

$("#hideMe").hide();




