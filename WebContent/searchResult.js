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


let defaultLimit = 10;
var numberOfPages = 1;
function processPagination(resultData) {
	console.log(resultData);
	
	let movieTableBodyElement = jQuery("#movie_table_body");
	numberOfPages = (resultData[0]['total_rows'] / defaultLimit);
	console.log("numberOfPages = " + numberOfPages);
	

	for (let i = Math.ceil(numberOfPages); i > 1 ; i--){
		
		let newHTML = "";

		newHTML += "<li class=\"page-item\"><a class=\"page-link\" href=\"#\" onclick=\"toggleActiveDisable(event)\" id=\""+ i + "\">" + i + "</a></li>";
		$("#pagination_ul li:nth-child(2)").after(newHTML);
	}
	
	for (let i = 0; i < resultData.length; i++) {
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
			rowHTML += "<th><button style=\"margin-left:6px;\" type=\"button\" class=\"btn btn-primary\"  id=\"" + resultData[i]["title"] + "\" onClick=\"addToCart(event)\">Add to cart</button></th>";
	
		rowHTML += "</tr>";
		
		movieTableBodyElement.append(rowHTML);
	}
	
}

function handleResult(resultData) {
	
	let movieTableBodyElement = jQuery("#movie_table_body");
//	console.log("parseInt(resultData[0]['total_rows']) = " + resultData[0]['total_rows']);
	
	console.log("resultData.length = " + resultData.length);
	if (resultData.length === 0){
		jQuery("#removeTable").remove();
		jQuery("#remove").remove();
		jQuery("#check").append("<h2 style=\"text-align: center;\">Sorry, result(s) not found!</h2>");
	}
	else if ((parseInt(resultData[0]['total_rows'])) > 9) 
			processPagination(resultData);
	else {
		for (let i = 0; i < resultData.length; i++) {
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
				rowHTML += "<th><button style=\"margin-left:6px;\" type=\"button\" class=\"btn btn-primary\"  id=\"" + resultData[i]["title"] + "\" onClick=\"addToCart(event)\">Add to cart</button></th>";
		
			rowHTML += "</tr>";
			
			movieTableBodyElement.append(rowHTML);
		}
	}
}

function addToCart(event){
	let itemName = event.target.id;

	window.location.href = "shoppingCart.html?&itemName=" + itemName;
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

let movieTitle = getParameterByName('title');
let movieYear = getParameterByName('year');
let movieDirector = getParameterByName('director');
let movieStarName = getParameterByName('starName');
let browse = getParameterByName('browse');
let genre = getParameterByName('genre');
let sortParam = getParameterByName('sort');
let limit = getParameterByName('limit');
let offset = getParameterByName('offset');
let updateUrlPagination = getParameterByName('updateUrlPagination');

console.log("limit=" + limit);
console.log("offset=" + offset);

let limitOffset = "";

if (limit == null){
	limit = 10;
	limitOffset += "&limit=50";
}
if (offset == null)
	limitOffset += "&offset=0";


if (updateUrlPagination != null)
	limitOffset += "&updateUrlPagination=true"
if (offset != null)
	limitOffset += "&offset=" + offset;
if (limit != null){
	defaultLimit = limit;
	limitOffset += "&limit=" + limit;
}


let urlString = "";

if (browse == "yes"){
	if (movieTitle != null)
		urlString += "title=" + movieTitle + "&browse=yes";
}
else {
	if (movieTitle != null){
		urlString = "title=" + movieTitle;
	}
	if (movieYear != null){
		if (urlString.length != 0)
			urlString += "&year=" + movieYear;
		else 
			urlString = "year=" + movieYear;
	}
	if (movieDirector != null){
		if (urlString.length != 0)
			urlString += "&director=" + movieDirector;
		else 
			urlString = "director=" + movieDirector;
	}
	if (movieStarName != null){
		if (urlString.length != 0)
			urlString += "&starName=" + movieStarName;
		else
			urlString = "starName=" + movieStarName;
	}
	if (genre != null)
		urlString = "genre=" + genre;
}

if (sortParam != null){
	urlString += "&sort=" + sortParam;
}

jQuery.ajax({
	dataType: "json",
	method: "GET",
	url: "api/searchBy?" + urlString + limitOffset,
	success: (resultData) => handleResult(resultData)
});


$("#search_box").keypress(function(event){

	if (event.which == 13 && $("#search_box").val() != "") {
			document.location.href = "searchResult.html?title=" + $("#search_box").val();
	}
});

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

$("#titleSort").click(function(event) {
	
	let urlStringSort = "";
	if (sortParam == null){
		urlStringSort = window.location.href.slice(window.location.href.indexOf('?') + 1);
		urlStringSort += "&sort=titleA";
		
		
//		$("#ratingIcon").attr('class', '');
//		$("#titleIcon").attr('class', 'fas fa-sort-up');
	}
	else if (sortParam == "titleA"){
		urlStringSort = window.location.href.slice(window.location.href.indexOf('?') + 1);
		urlStringSort = updateQueryStringParameter(urlStringSort, "sort", "titleD");
		
		
//		$("#ratingIcon").attr('class', '');
//		$("#titleIcon").attr('class', 'fas fa-sort-down');
	}
	else if (sortParam == "titleD"){
		urlStringSort = window.location.href.slice(window.location.href.indexOf('?') + 1);
		urlStringSort = updateQueryStringParameter(urlStringSort, "sort", "titleA");
		
	
//		$("#ratingIcon").attr('class', '');
//		$("#titleIcon").attr('class', 'fas fa-sort-up');
	}
	else if (sortParam == "ratingA"){
		urlStringSort = window.location.href.slice(window.location.href.indexOf('?') + 1);
		urlStringSort = updateQueryStringParameter(urlStringSort, "sort", "titleA");
		
	
//		$("#ratingIcon").attr('class', '');
//		$("#titleIcon").attr('class', 'fas fa-sort-up');
	}
	else if (sortParam == "ratingD"){
		urlStringSort = window.location.href.slice(window.location.href.indexOf('?') + 1);
		urlStringSort = updateQueryStringParameter(urlStringSort, "sort", "titleA");
		
	
//		$("#ratingIcon").attr('class', '');
//		$("#titleIcon").attr('class', 'fas fa-sort-up');
	}
	
//	alert(urlStringSort);
	document.location.href = "searchResult.html?" + urlStringSort;

});

$("#ratingSort").click(function(event){
	
	let urlStringSort = "";
		if (sortParam == null){
			urlStringSort = window.location.href.slice(window.location.href.indexOf('?') + 1);
			urlStringSort += "&sort=ratingA";
			
//			$("#titleIcon").attr('class', '');
//			$("#ratingIcon").attr('class', 'fas fa-sort-up');
		}
		else if (sortParam == "ratingA"){
			urlStringSort = window.location.href.slice(window.location.href.indexOf('?') + 1);
			urlStringSort = updateQueryStringParameter(urlStringSort, "sort", "ratingD");
			
//			$("#titleIcon").attr('class', '');
//			$("#ratingIcon").attr('class', 'fas fa-sort-down');
		}
		else if (sortParam == "ratingD"){
			urlStringSort = window.location.href.slice(window.location.href.indexOf('?') + 1);
			urlStringSort = updateQueryStringParameter(urlStringSort, "sort", "ratingA");
			
//			$("#titleIcon").attr('class', '');
//			$("#ratingIcon").attr('class', 'fas fa-sort-up');
		}
		else if (sortParam == "titleA"){
			urlStringSort = window.location.href.slice(window.location.href.indexOf('?') + 1);
			urlStringSort = updateQueryStringParameter(urlStringSort, "sort", "ratingA");
			
//			$("#titleIcon").attr('class', '');
//			$("#ratingIcon").attr('class', 'fas fa-sort-down');
		}
		else if (sortParam == "titleD"){
			urlStringSort = window.location.href.slice(window.location.href.indexOf('?') + 1);
			urlStringSort = updateQueryStringParameter(urlStringSort, "sort", "ratingA");
			
//			$("#titleIcon").attr('class', '');
//			$("#ratingIcon").attr('class', 'fas fa-sort-up');
		}
		
//		alert(urlStringSort);
		
		document.location.href = "searchResult.html?" + urlStringSort;
		
});

function handleSortResult(resultData, urlStringSort) {
	$("#movie_table_body").empty();
	handleResult(resultData);
}

$("#movie_table_body").click(function(event){
	window.history.pushState({}, null, "searchResult.html?" + urlString);
});

function titleClick(event){
	searchKey = event.target.textContent;
	let myArguments = "title=" + searchKey + "&browse=yes";
//	alert(myArguments);
	document.location.href = "searchResult.html?" + myArguments;
}

let alphabetString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
for (let i = 0; i < 36; i++){
	if (alphabetString[i] === 'A')
		$("#exampleModalCenterBrowseTitle .modal-body").append("<br>");
	let rowHTML = "<a href=\"#\" onClick=\"titleClick(event)\">" +  alphabetString[i] + "</a>&nbsp";
	$("#exampleModalCenterBrowseTitle .modal-body").append(rowHTML);
}


function genreClick(event) {
	searchKey = event.target.textContent;
	let myArguments = "genre=" + searchKey;
//	alert(myArguments);
	document.location.href = "searchResult.html?" + myArguments;
}

let genreArr = ["Action", "Adult", "Adventure", "Animation", "Biography", "Comedy", "Crime", 
	"Documentary", "Drama", "Family", "Fantasy", "History", "Horror", "Music", "Musical", 
	"Mystery", "Reality-TV", "Romance", "Sci-Fi", "Sport", "Thriller", "War", "Western"];
for (let i = 0; i < 23; i++){
	if (genreArr[i] === "Documentary")
		$("#exampleModalCenterBrowseGenre .modal-body").append("<br>");
	else if (genreArr[i] === "Mystery")
		$("#exampleModalCenterBrowseGenre .modal-body").append("<br>");
	let rowHTML = "<a href=\"#\" onClick=\"genreClick(event)\">" +  genreArr[i] + "</a>&nbsp";
	$("#exampleModalCenterBrowseGenre .modal-body").append(rowHTML);
}


if (updateUrlPagination == null)
	updateUrlPagination = "false";
function setLimOff(event) {
	
	let oldUrl = "";
	let limitSet = event.target.textContent.split(' ')[1];
	
	
	if (updateUrlPagination == "false"){
		console.log("hi i'm in first if dropdown menu");
		oldUrl = "searchResult.html?" + window.location.href.slice(window.location.href.indexOf('?') + 1) + 
		"&updateUrlPagination=true&offset=0&limit=" + limitSet;
	}
	else if (updateUrlPagination == "true")
	{
		console.log("i'm in else dropdown menu");
		oldUrl = "searchResult.html?" + window.location.href.slice(window.location.href.indexOf('?') + 1);
		oldUrl= updateQueryStringParameter(oldUrl, "offset", "0");
		oldUrl= updateQueryStringParameter(oldUrl, "limit", limitSet);
	}
	
	
	oldUrl = removeURLParameter(oldUrl, 'sort');
	
//	alert(oldUrl);
	document.location.href = oldUrl;
}

let newOffset;

if (offset == null)
	newOffset = 0;
else if (offset != null)
	newOffset = offset;

$("#pagination_ul").click(function(event){
	
	let offsetClick;
	let current = (newOffset / limit);
	console.log("offset=" + newOffset);
	console.log("current=" + current);
	
	let limitClick = "";
	let oldUrl = "";
	
	if (limit == null){
		limitClick = defaultLimit;
		
	}
	else {
		limitClick = limit;
	}
	
	if (event.target.textContent == "Previous"){
		if (current > 0){
			current = current - 1;
			offsetClick = current * (Number.parseInt(limit, 10));
		}
		else
		{
			offsetClick = current * (Number.parseInt(limit, 10));
		}
	}
	else if (event.target.textContent == "Next"){
		if (current < (Math.ceil(numberOfPages) - 1)) {
			current = current + 1;
			offsetClick = current * (Number.parseInt(limit, 10));
		}
		else
		{
			offsetClick = current * (Number.parseInt(limit, 10));
		}
	}
	else
		offsetClick = (Number.parseInt(event.target.textContent, 10) - 1) *  (Number.parseInt(limit, 10));

	
	if (updateUrlPagination == "false"){
		console.log("hi i'm in first if pagination_click");
		oldUrl = "searchResult.html?" + window.location.href.slice(window.location.href.indexOf('?') + 1) + "&updateUrlPagination=true"
		+ "&offset=" + offsetClick + "&limit=" + limitClick;
	}
	else if (updateUrlPagination == "true")
	{
		console.log("i'm in else pagination_click");
//		oldUrl = document.referrer
		oldUrl = "searchResult.html?" + window.location.href.slice(window.location.href.indexOf('?') + 1);
		oldUrl = updateQueryStringParameter(oldUrl, "limit", limitClick);
		oldUrl = updateQueryStringParameter(oldUrl, "offset", offsetClick);
	}
	
	oldUrl = removeURLParameter(oldUrl, 'sort');
//	alert(oldUrl);
	
	document.location.href = oldUrl;
});


