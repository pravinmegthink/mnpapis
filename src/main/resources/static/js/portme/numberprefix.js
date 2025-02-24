//$('#op_id').change(function(event) {
//	var op_id = $("select#op_id").val();
//	$.getJSON('getoperatordetails', {
//		op_id : op_id
//	}, function(data) {
//		// console.log(data);
//		// $("#start_range").val(data.start_range);
//		// $("#end_range").val(data.end_range);
//		// $("#technology").val(data.technology);
//		// $("#type").val(data.type);
//		// $("#area").val(data.area);
//		// $("#routing_info").val(data.routing_info);
//		// $("#slno").val(data.slno);
//	});
//});

$(document).ready(function() {
	submits();
});

function submits() {

	var button = $('#Submit')

	button.on('click', function() {

		event.preventDefault();
		if (prefixValidate()) {
			// Get form
			var form = $('#numberPlanForm')[0];
			$("#Submit").prop("disabled", true);
			fire_ajax_submit(form, "createprefix");
		}
	})
};

function fire_ajax_submit(formData, apiUrl) {
	var data = new FormData(formData);
	$.ajax({
		type : "POST",
		enctype : 'multipart/form-data',
		url : apiUrl,
		data : data,
		processData : false,
		contentType : false,
		cache : false,
		timeout : 600000,
		success : function(data) {
			alert(data.totalCount);
		},
		error : function(e) {

		}
	});
};

function prefixValidate() {
	document.getElementById("lblop_id").style.visibility = "hidden";
	document.getElementById("lblstartrange").style.visibility = "hidden";
	document.getElementById("lblendrange").style.visibility = "hidden";
	document.getElementById("lblarea").style.visibility = "hidden";
	document.getElementById("lblrinfo").style.visibility = "hidden";
	var op_id = document.getElementById("op_id");
	var start_range = document.getElementById("start_range");
	var end_range = document.getElementById("end_range");
	var technology = document.getElementById("technology");
	var type = document.getElementById("type");
	var area = document.getElementById("area");
	var routing_info = document.getElementById("routing_info");

	var result = true;
	if (op_id.value.trim() == "0") {
		document.getElementById("lblop_id").style.visibility = "visible";
		result = false;
	}
	if (start_range.value.trim() == "") {
		document.getElementById("strangetext").textContent = "Start range cann't be empty";
		document.getElementById("lblstartrange").style.visibility = "visible";
		result = false;
	} else {
		if (start_range.value.trim().length == 10) {
			var isDigitInput = /^\d+$/.test(start_range.value);
			if (!isDigitInput) {
				document.getElementById("strangetext").textContent = "Please provide only numerical values.";
				document.getElementById("lblstartrange").style.visibility = "visible";
				result = false;
			}
		} else {
			document.getElementById("strangetext").textContent = "Start range should be 10 digits only";
			document.getElementById("lblstartrange").style.visibility = "visible";
			result = false;
		}
	}
	if (end_range.value.trim() == "") {
		document.getElementById("endrangetext").textContent = "End range cann't be empty";
		document.getElementById("lblendrange").style.visibility = "visible";
		result = false;
	} else {
		if (end_range.value.trim().length == 10) {
			var isDigitInput = /^\d+$/.test(end_range.value);
			if (!isDigitInput) {
				document.getElementById("endrangetext").textContent = "Please provide only numerical values.";
				document.getElementById("lblendrange").style.visibility = "visible";
				result = false;
			}
		} else {
			document.getElementById("endrangetext").textContent = "End range should be 10 digits only";
			document.getElementById("lblendrange").style.visibility = "visible";
			result = false;
		}
	}
	if (area.value.trim() == "") {
		document.getElementById("lblarea").style.visibility = "visible";
		result = false;
	}
	if (routing_info.value.trim() == "") {
		document.getElementById("rinforangetext").textContent = "Routing Info cann't be empty";
		document.getElementById("lblrinfo").style.visibility = "visible";
		result = false;
	} else {
		if (routing_info.value.trim().length == 4) {
			var isDigitInput = /^\d+$/.test(routing_info.value);
			if (!isDigitInput) {
				document.getElementById("rinforangetext").textContent = "Please provide only numerical values.";
				document.getElementById("lblrinfo").style.visibility = "visible";
				result = false;
			}
		} else {
			document.getElementById("rinforangetext").textContent = "Routing Info should be 4 digits only";
			document.getElementById("lblrinfo").style.visibility = "visible";
			result = false;
		}
	}
	return result;
}
