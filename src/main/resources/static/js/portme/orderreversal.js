//start code for paralel request reversal
$(document).on("click", ".orderReversal", function() {
	// Getting data from request
	var id = $(this).data('id');
	var msisdn = $(this).data('title');
	var requestId = $(this).data('tile');
	// store data in json object
	var jsonData = {
		"id" : id,
		"msisdnUID" : [ {
			"msisdn" : msisdn,
			"requestId" : requestId
		} ]
	}
	var form = new FormData();
	form.append("orderReversal", JSON.stringify(jsonData));
	fire_ajax_submit(form);
});

function fire_ajax_submit(form){
	$.ajax({
		type : "POST",
		url : "api/orderreversal",
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
