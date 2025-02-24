//start code for parallel request disconnection
$("#datatablesSimple").on("click", ".portDisconnect", function() {
	// Getting data from request
	var requestId = $(this).data('id');
	var msisdn = $(this).data('title');
	// store data in json object
	var jsonData = {
		"msisdnUID" : [ {
			"msisdn" : msisdn,
			"requestId" : requestId
		} ]
	}
	var form = new FormData();
	form.append("portDisconnection", JSON.stringify(jsonData));
	fire_ajax_submit(form);
	$(this).closest('tr').remove();
});

// start code for whole request disconnection
$("#datatablesSimple").on("click", ".portAllDiconnect", function() {
	// Getting data from request
	var requestId = $(this).data('id');
	// store data in json object
	var jsonData = {
			"msisdnUID" : [ {
				"msisdn" : null,
				"requestId" : requestId
			} ]
	}
	var form = new FormData();
	form.append("portDisconnection", JSON.stringify(jsonData));
	fire_ajax_submit(form);
	$(this).closest('tr').remove();
});


function fire_ajax_submit(form){
	$.ajax({
		type : "POST",
		url : "api/disconnectionanswer",
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

};
