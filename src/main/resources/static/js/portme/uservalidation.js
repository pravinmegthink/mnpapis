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
		if (userValidate()) {
			// Get form
			var form = $('#userForm')[0];
//			$("#Submit").prop("disabled", true);
			fire_ajax_submit(form, "createuser.html");
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

function userValidate() {
	document.getElementById("lblfirstName").style.visibility = "hidden";
	document.getElementById("lbllastName").style.visibility = "hidden";
	document.getElementById("lblusername").style.visibility = "hidden";
	document.getElementById("lblpassword").style.visibility = "hidden";
	document.getElementById("lblcontactNumber").style.visibility = "hidden";
	document.getElementById("lblemailId").style.visibility = "hidden";
	document.getElementById("lblcontactPerson").style.visibility = "hidden";
	document.getElementById("lblcompanyName").style.visibility = "hidden";

	var firstName = document.getElementById("firstName");
	var lastName = document.getElementById("lastName");
	var userName = document.getElementById("username");
	var password = document.getElementById("password");
	var contactNumber = document.getElementById("contactNumber");
	var email = document.getElementById("emailId");
	var contactPerson = document.getElementById("contactPerson");
	var companyName = document.getElementById("companyName");

	var result = true;

	if (firstName.value.trim() == "") {
		document.getElementById("lblfirstName").style.visibility = "visible";
		result = false;
	}
	if (lastName.value.trim() == "") {
		document.getElementById("lbllastName").style.visibility = "visible";
		result = false;
	}
	if (userName.value.trim() == "") {
		document.getElementById("lblusernameTxt").textContent = "Username cann't be empty ";
		document.getElementById("lblusername").style.visibility = "visible";
		result = false;
	} else {
		if (isUsernameValid(userName.value)) {
		} else {
			document.getElementById("lblusernameTxt").textContent = "Username contains spaces and is not valid";
			document.getElementById("lblusername").style.visibility = "visible";
			result = false;
		}
	}
	if (password.value.trim() == "") {
		document.getElementById("lblpassword").style.visibility = "visible";
		result = false;
	}
	if (contactNumber.value.trim() == "") {
		document.getElementById("contactNumberTxt").textContent = "Contact Number cann't be empty ";
		document.getElementById("lblcontactNumber").style.visibility = "visible";
		result = false;
	} else {
		if (contactNumber.value.trim().length == 10) {
			var isDigitInput = /^\d+$/.test(contactNumber.value);
			if (!isDigitInput) {
				document.getElementById("contactNumberTxt").textContent = "Please provide only numerical values.";
				document.getElementById("lblcontactNumber").style.visibility = "visible";
				result = false;
			}
		} else {
			document.getElementById("contactNumberTxt").textContent = "Contact Number should be 10 digits only";
			document.getElementById("lblcontactNumber").style.visibility = "visible";
			result = false;
		}
	}
	if (email.value.trim() == "") {
		document.getElementById("lblemailtxt").textContent = "Email cann't be empty";
		document.getElementById("lblemailId").style.visibility = "visible";
		result = false;
	} else {
		if (validateEmail(email.value)) {
		} else {
			document.getElementById("lblemailtxt").textContent = "Email is not valid";
			document.getElementById("lblemailId").style.visibility = "visible";
			result = false;
		}
	}
	if (contactPerson.value.trim() == "") {
		document.getElementById("lblcontactPerson").style.visibility = "visible";
		result = false;
	}
	if (companyName.value.trim() == "") {
		document.getElementById("lblcompanyName").style.visibility = "visible";
		result = false;
	}
	return result;
}

function isUsernameValid(username) {
	// Regular expression to check for spaces
	const spaceRegex = /\s/;
	return !spaceRegex.test(username);
}

function validateEmail(email) {
	const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;
	return emailRegex.test(email);
}
