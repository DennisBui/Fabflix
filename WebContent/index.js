function handleMovieResult(resultData) {
	let movieTableBodyElement = jQuery("#movie_table_body");
	
	for (let i = 0; i < 20; i++) {
		let rowHTML = "";
		rowHTML += "<tr>";
			rowHTML += "<th>" + '<a href="single-movie.html?id=' + resultData[i]["movie_id"] + '">' + resultData[i]["title"] + '</a>' + "</th>";
			rowHTML += "<th>" + resultData[i]["year"] + "</th>";
			rowHTML += "<th>" + resultData[i]["director"] + "</th>";
			rowHTML += "<th>" + resultData[i]["listGenres"] + "</th>";
			
			let eachStarId = resultData[i]["listStarsId"].split(", ");
			let eachStarName = resultData[i]["listStarsName"].split(", ");
			
			rowHTML += "<th>";
			let j = 0;
			for (j = 0; j <= eachStarName.length - 2; j++) {
					rowHTML += '<a href="single-star.html?id=' + 
						eachStarId[j] + '">' + eachStarName[j] + '</a>, ';
			}
			rowHTML += '<a href="single-star.html?id=' + eachStarId[j] + '">' + eachStarName[j] + '</a>';
			rowHTML += "</th>";
			
			rowHTML += "<th>" + resultData[i]["rating"] + "</th>";
	
		rowHTML += "</tr>";
		
		movieTableBodyElement.append(rowHTML);
	}
}

//jQuery.ajax({
//	dataType: "json",
//	method: "GET",
//	url: "api/movies",
//	success: (resultData) => handleMovieResult(resultData)
//});

function logOut(event) {
	$.get(
	        "api/indexServlet",
	        {logout : "true"},
	        function (event) {
	        	window.location.replace("index.html");
	        }
	);
}
/*
$("#search_box").keypress(function(event){
	if (event.which == 13 && $("#search_box").val() != "") {
		document.location.href = "searchResult.html?title=" + $("#search_box").val();
	}
});
*/


$("#exampleModalCenter .modal-body").keypress(function(event){
	if (event.which == 13) {
		let myArguments = "";
		
		if ($("#search_title").val())
			myArguments += "title=" + $("#search_title").val();
		if ($("#search_year").val()) {
			if (myArguments)
				myArguments += "&";
			myArguments += "year=" + $("#search_year").val();
		}
		if ($("#search_director").val()) {
			if (myArguments)
				myArguments += "&";
			myArguments += "director=" + $("#search_director").val();
		}
		if ($("#search_starName").val()){
			if (myArguments)
				myArguments += "&";
			myArguments += "starName=" + $("#search_starName").val();
		}
		
		document.location.href = "searchResult.html?" + myArguments;
	}
});


$("#buttonPressed").click(function (event){
	let myArguments = "";
	
	if ($("#search_title").val())
		myArguments += "title=" + $("#search_title").val();
	if ($("#search_year").val()) {
		if (myArguments)
			myArguments += "&";
		myArguments += "year=" + $("#search_year").val();
	}
	if ($("#search_director").val()) {
		if (myArguments)
			myArguments += "&";
		myArguments += "director=" + $("#search_director").val();
	}
	if ($("#search_starName").val()){
		if (myArguments)
			myArguments += "&";
		myArguments += "starName=" + $("#search_starName").val();
	}
	
	document.location.href = "searchResult.html?" + myArguments;
	
});


function titleClick(event){
	searchKey = event.target.textContent;
	let myArguments = "title=" + searchKey + "&browse=yes";
	//alert(myArguments);
	document.location.href = "searchResult.html?" + myArguments;
}

let alphabetString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
for (let i = 0; i < 36; i++){
	let rowHTML = "<a href=\"#\" onClick=\"titleClick(event)\">" +  alphabetString[i] + "</a>&nbsp | &nbsp";
	$("#exampleModalCenterBrowseTitle .modal-body").append(rowHTML);
}


function genreClick(event) {
	searchKey = event.target.textContent;
	let myArguments = "genre=" + searchKey;
	//alert(myArguments);
	document.location.href = "searchResult.html?" + myArguments;
}

let genreArr = ["Action", "Adult", "Adventure", "Animation", "Biography", "Comedy", "Crime", 
	"Documentary", "Drama", "Family", "Fantasy", "History", "Horror", "Music", "Musical", 
	"Mystery", "Reality-TV", "Romance", "Sci-Fi", "Sport", "Thriller", "War", "Western"];
for (let i = 0; i < 23; i++){
	let rowHTML = "<a href=\"#\" onClick=\"genreClick(event)\">" +  genreArr[i] + "</a>&nbsp | &nbsp";
	$("#exampleModalCenterBrowseGenre .modal-body").append(rowHTML);
}