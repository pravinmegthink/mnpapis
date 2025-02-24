//start code for terminate msisdn
function terminateMSISDN() {
	var msisdn = document.getElementById("msisdn").value;
	if (msisdn.trim() == "") {
		alert("Please input msisdn");
	} else {
		var form = new FormData();
		form.append("msisdn", msisdn);
		fire_ajax_submit(form);
	}
};

function fire_ajax_submit(form) {
	$.ajax({
		type : "POST",
		url : "api/terminatesim",
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

$(document).on("click", ".nrhConfirm", function() {
	// Getting data from request
	var requestId = $(this).data('id');
	var msisdn = $(this).data('title');
	// store data in json object
	var jsonData = {
		"requestId" : requestId,
		"msisdn" : msisdn
	}
	var form = new FormData();
	form.append("nrh_confirmation", JSON.stringify(jsonData));
	// send ajax request to application
	$.ajax({
		type : "POST",
		url : "api/nrhconfirmation",
		success : function(result) {
			var response = JSON.parse(result);
			alert(response.responseMessage);
			if (response.responseCode == 200) {
				$("#btnSubmit").prop("disabled", true);
			}
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
});

