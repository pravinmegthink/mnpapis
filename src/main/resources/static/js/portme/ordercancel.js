$(document).on("click", ".orderCancel", function() {
	// Getting data from request
	var requestId = $(this).data('id');
	var msisdn = $(this).data('title');
	var service = $(this).data('tile');
	// store data in json object
	var jsonData = {
		"requestId" : requestId,
		"service" : service,
		"msisdnUID" : [ {
			"msisdn" : msisdn,
			"requestId" : requestId
		} ]
	}
	// send ajax request to application
	$.ajax({
		url : 'api/ordercancel',
		type : 'POST',
		data : JSON.stringify(jsonData),
		contentType : 'application/json; charset=utf-8',
		dataType : 'json',
		async : false,
		success : function(msg) {
			alert(msg.responseMessage);
		}
	});
	$(this).closest('tr').remove();

});
