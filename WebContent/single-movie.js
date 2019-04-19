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

function handleResult(resultData) {
	console.log("handleResult: populating movie tablefrom resultData");
	console.log(resultData);
	
	let movieTableBodyElement = jQuery("#movie_table_body");
	
	for (let i = 0; i < Math.min(10, resultData.length); i++) {
		let rowHTML = "";
		rowHTML += "<tr>";
			rowHTML += "<th>" + resultData[i]["title"] + "</th>";
			rowHTML += "<th>" + resultData[i]["year"] + "</th>";
			rowHTML += "<th>" + resultData[i]["director"] + "</th>";
			rowHTML += "<th>" + resultData[i]["listGenres"] + "</th>";
			
			let eachStar = resultData[i]["listStars"].split(", ");
			let eachStarId = resultData[i]["listStarsId"].split(", ");
			
			rowHTML += "<th>";
			let j = 0;
			for (j = 0; j <= eachStar.length - 2; j++) {
					rowHTML += '<a href="single-star.html?id=' + 
						eachStarId[j] + '">' + eachStar[j] + '</a>, ';
			}
			rowHTML += '<a href="single-star.html?id=' + eachStarId[j] + '">' + eachStar[j] + '</a>';
			rowHTML += "<th><button style=\"margin-left:6px;\" type=\"button\" class=\"btn btn-primary\"  id=\"" + resultData[i]["title"] + "\" onClick=\"addToCart(event)\">Add to cart</button></th>";
			rowHTML += "</th>";
			
		rowHTML += "</tr>";
		
		movieTableBodyElement.append(rowHTML);
	}
}

let movieID = getParameterByName('id');
console.log(movieID);

jQuery.ajax({
	dataType: "json",
	method: "GET",
	url: "api/single-movie?id=" + movieID,
	success: (resultData) => handleResult(resultData)
});

function addToCart(event){
	let itemName = event.target.id;

	window.location.href = "shoppingCart.html?&itemName=" + itemName;
}