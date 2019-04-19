function quickSuggest(query, callBackFin) {
    console.log("Initiating autocomplete");
	
	if(sessionStorage.getItem(query) != null){
		console.log("Checking cache for results")
		var jsonData = JSON.parse(sessionStorage.getItem(query));
		console.log(jsonData);
		callBackFin( { suggestions: jsonData } );
	}else{
		console.log("Using ajax for autocomplete");
		jQuery.ajax({
			"method": "GET",
			"url": "AutoCompleteServlet?query=" + escape(query),
			"success": function(data) {
				sessionStorage.setItem(query, data);

				var jsonData = JSON.parse(data);
				console.log(jsonData)
				callBackFin( { suggestions: jsonData } );
			},
			"error": function(errorData) {
				console.log("Error could not look up")
			}
		})
	}
}

function handleSelectSuggestion(suggestion) {
	document.location.href = "single-movie.html" + "?id=" + suggestion["data"]["id"]
}

jQuery('#autocomplete').autocomplete({
    lookup: function (query, callBackFin) {
    		quickSuggest(query, callBackFin)
    },
    onSelect: function(suggestion) {
    		handleSelectSuggestion(suggestion)
    },
    groupBy: "type",
    deferRequestBy: 300,
});

jQuery('#autocomplete').keypress(function(event){
	if(event.keyCode == 13)
		{
			document.location.href = "searchResult.html?title=" + $('#autocomplete').val();
		}
})