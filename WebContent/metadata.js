function handleMetaData(resultData)
{
	console.log(resultData[0]["status"]);
	
	let metaDataElement = jQuery("#metadata_table");
	if(resultData[0]["status"] === "success")
		{
		console.log("Metadata import was successful");
		for (let i = 0; i < resultData.length; i++)
			{
			let rowHTML = "";
			rowHTML += "<tr>";
			rowHTML += "<th>" + resultData[i]["table_name"] + "</th>";
			rowHTML += "<th>" + resultData[i]["column_name"] + "</th>";
			rowHTML += "<th>" + resultData[i]["data_type"] + "</th>";
			
			rowHTML += "</tr>";
			metaDataElement.append(rowHTML);
			}
		}
}

jQuery.ajax({
	dataType: "json",
	method: "GET",
	url: "api/metadata",
	success: (resultData) => handleMetaData(resultData)
});