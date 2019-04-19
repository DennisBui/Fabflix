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

function updateQueryStringParameter(uri, key, value) {
	  var re = new RegExp("([?&])" + key + "=.*?(&|$)", "i");
	  var separator = uri.indexOf('?') !== -1 ? "&" : "?";
	  if (uri.match(re)) {
	    return uri.replace(re, '$1' + key + "=" + value + '$2');
	  }
	  else {
	    return uri + separator + key + "=" + value;
	  }
}

function removeURLParameter(url, parameter) {
    //prefer to use l.search if you have a location/link object
    var urlparts = url.split('?');   
    if (urlparts.length >= 2) {

        var prefix = encodeURIComponent(parameter) + '=';
        var pars = urlparts[1].split(/[&;]/g);

        //reverse iteration as may be destructive
        for (var i = pars.length; i-- > 0;) {    
            //idiom for string.startsWith
            if (pars[i].lastIndexOf(prefix, 0) !== -1) {  
                pars.splice(i, 1);
            }
        }

        return urlparts[0] + (pars.length > 0 ? '?' + pars.join('&') : '');
    }
    return url;
}

let itemName = getParameterByName('itemName');
let quantity = getParameterByName('quantity');
let newQuantity = getParameterByName('newQuantity');
let deleteItemName = getParameterByName('deleteItemName');

let quantityParam = "";
let itemNameParam = "";

if (itemName != null){
	itemNameParam = "itemName=" + itemName;
	
	if (quantity == null)
		quantityParam = "&quantity=1";
	else if (quantity != null)
		quantityParam = "&quantity=" + quantity;
}


//alert("api/indexServlet?" + itemNameParam + quantityParam);
if (deleteItemName == null){
	console.log("i'm in deleteItemName == null");
	jQuery.ajax({
		method: "GET",
		url: "api/indexServlet?" + itemNameParam + quantityParam,
		success: (resultDataString) => handleCartArray(resultDataString)
	});
}
else if (deleteItemName != null) {
	console.log("i'm in deleteItemName != null");
	console.log("api/indexServlet?" + "deleteItemName=" + deleteItemName);
	jQuery.ajax({
		method: "GET",
		url: "api/indexServlet?" + "deleteItemName=" + deleteItemName,
		success: function(resultDataString) {
			handleCartArray(resultDataString)
			}
	});
}

function logOut(event) {
	$.get(
	        "api/indexServlet",
	        {logout : "true"},
	        function (event) {
	        	window.location.replace("index.html");
	        }
	);
}



function handleCartArray(resultDataString) {
	let resultArray = resultDataString.split(",");
	console.log(resultArray);
	
	let cardBody = jQuery(".card-body");
	
	if (resultArray[0] == "" && resultArray.length == 1){
		cardBody.append("<p>Your shopping cart is empty.</p>");
	}
	else{
		let itemName = "";
		let itemId = "";
		for (let i = 0; i < resultArray.length; i++){
			
			itemName = resultArray[i].split('-')[0];
			itemId = resultArray[i].split('-')[1];
			
			let newHtml = "";
			let checkoutHtml = "";
			newHtml += "<div class=\"row\">";
			newHtml += "<div class=\"col-12 col-sm-12 col-md-2 text-center\">";
			newHtml += "<img class=\"img-responsive\" src=\"http://placehold.it/120x80\" alt=\"prewiew\" width=\"120\" height=\"80\">";
			newHtml += "</div>";
			newHtml += "<div class=\"col-12 text-sm-center col-sm-12 text-md-left col-md-6\">";
			newHtml += "<h4 class=\"product-name\"><strong>" + itemName + "</strong></h4>";
			newHtml += "</div>";
			newHtml += "<div class=\"col-12 col-sm-12 text-sm-center col-md-4 text-md-right row\">";
			newHtml += "<div class=\"col-4 col-sm-4 col-md-4\">";
			newHtml += "<div class=\"quantity\">";
			newHtml += "<input onkeydown=\"return false;\" id=\"" + itemName + "\"  onClick=\"chooseQuantity(event)\" style=\"color: transparent;text-shadow: 0 0 0 blue;\" type=\"number\" step=\"1\" max=\"999\" min=\"0\" value=\"" + itemId + "\" title=\"Qty\" class=\"qty\" size=\"4\">";
			newHtml += "</div>";
			newHtml += "</div>";
			newHtml += "<div class=\"col-2 col-sm-2 col-md-2 text-right\">";
			newHtml += "<button id=\"" + itemName + "\" onClick=\"deleteItem(event)\" type=\"button\" class=\"btn btn-outline-danger btn-xs\">Delete";
			newHtml += "</button>";
			newHtml += "</div>";
			newHtml += "</div>";
			newHtml += "</div>";
			newHtml += "<hr>";
			
			cardBody.append(newHtml);

		}
	}
}

function chooseQuantity(event){
	let oldUrl = window.location.href.slice(window.location.href.indexOf('?') + 1);
	
//	alert(oldUrl);
	
	let newUrl;
	if (deleteItemName == null)
		newUrl = "shoppingCart.html?" + oldUrl;
	else
		newUrl = "shoppingCart.html?";
//	alert(itemName + "-> " + event.target.id);
//	alert(itemName == null);
//	alert(itemName != event.target.id);
	if ($(event.target).val() == 0)
	{
		console.log("i'm in $(event.target).val() == 0");
		newUrl = newUrl.split('?')[0];
		newUrl += "?deleteItemName=" + event.target.id;
//		alert(newUrl);
		window.location.href = newUrl;
	}
	else{
		if (itemName == null)
			newUrl += "itemName=" + event.target.id;
		else if (itemName != event.target.id)
			newUrl = updateQueryStringParameter(newUrl, "itemName", event.target.id);
		if (newQuantity == null)
			newUrl += "&newQuantity=false&quantity=" + $(event.target).val();
		else
			newUrl = updateQueryStringParameter(newUrl, "quantity", $(event.target).val());
	
//		alert(newUrl);
		window.location.href = newUrl;
	}
}

function deleteItem(event) {
	let oldUrl = window.location.href.slice(window.location.href.indexOf('?') + 1);
	let newUrl = "shoppingCart.html?" + oldUrl;
	
//	alert("itemName=" + itemName);
//	alert("event.target.id=" + event.target.id);
//	alert("(itemName == null)->" + (itemName == null));

	newUrl = newUrl.split('?')[0];
	newUrl += "?deleteItemName=" + event.target.id;

//	if (newQuantity == null)
//		newUrl += "&newQuantity=false&quantity=" + $(event.target).val(); 
//	else
//		newUrl = updateQueryStringParameter(newUrl, "quantity", $(event.target).val());
	
	// alert(newUrl);
	window.location.href = newUrl;
}


