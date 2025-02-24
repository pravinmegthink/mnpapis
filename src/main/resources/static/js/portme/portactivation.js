$(document).on("click", ".orderActivate", function() {
	// Getting data from request
	var requestId = $(this).data('id');
	var msisdn = $(this).data('title');
	var service = $(this).data('tile');
	// store data in json object
	var jsonData = {
		"service" : service,
		"msisdnUID" : [ {
			"msisdn" : msisdn,
			"requestId" : requestId
		} ]
	}
	var form = new FormData();
	form.append("portmeanswer", JSON.stringify(jsonData));

	$.ajax({
		type : "POST",
		url : "api/connectionanswer",
		success : function(result) {
			var response = JSON.parse(result);
			alert(response.responseMessage);
		},
		error : function(error) {
			console.log(error);
		},
		async : true,
		data : form,
		cache : false,
		contentType : false,
		processData : false,
		timeout : 60000
	});
	$(this).closest('tr').remove();
});
