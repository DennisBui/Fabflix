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
			rowHTML += "<th>" + resultData[i]["name"] + "</th>";
			if (typeof resultData[i]["birthYear"] === 'string')
				rowHTML += "<th>" + resultData[i]["birthYear"] + "</th>";
			else
				rowHTML += "<th> N/A </th>";
			
			let eachMovieTitle = resultData[i]["listMoviesTitle"].split(", ");
			let eachMovieId = resultData[i]["listMoviesId"].split(", ");
			rowHTML += "<th>";
			let j = 0;
			for (j = 0; j <= eachMovieTitle.length - 2; j++) {
					rowHTML += '<a href="single-movie.html?id=' + 
						eachMovieId[j] + '">' + eachMovieTitle[j] + '</a>, ';
			}
			rowHTML += '<a href="single-movie.html?id=' + eachMovieId[j] + '">' + eachMovieTitle[j] + '</a>';
			
			rowHTML += "</th>";
			
			

		rowHTML += "</tr>";
		
		movieTableBodyElement.append(rowHTML);
	}
}

let starId = getParameterByName('id');
console.log(starId);

jQuery.ajax({
	dataType: "json",
	method: "GET",
	url: "api/single-star?id=" + starId,
	success: (resultData) => handleResult(resultData)
});