$('input[type=radio]').on("change", buttonChanged);
function buttonChanged() {
	if ($("#flexRadioFull").is(':checked')) {
		$("#showPartialModel").hide();
		$("#flexCheckLSA").prop("checked", false);
		$("#flexCheckMSISDN").prop("checked", false);
		$("#flexCheckTIMESTAMP").prop("checked", false);
		$("#showPartialModelFilter").hide();
		$("#showLSAModel").hide();
		$("#showMSISDNModel").hide();
		$("#showDateRangeModel").hide();
		if ($("#flexRadioZone2").is(':checked')) {
			$("#showLSAInputForZone2Full").show();
		} else {
			$("#showLSAInputForZone2Full").hide();
		}
		// $("#showFullModelFilter").show();
	} else if ($("#flexRadioPartial").is(':checked')) {
		$("#showLSAInputForZone2Full").hide();
		$("#showPartialModel").show();
		// $("#showFullModelFilter").hide();
		if ($("#flexRadioZone2").is(':checked')) {
			// we don;t need to allow msisdn for zone2 request
			$("#noNeedZone2").hide();
		} else {
			$("#noNeedZone2").show();
		}
		$("#showPartialModelFilter").show();
	}
};
function showLSAModel() {
	if ($("#flexCheckLSA").is(':checked')) {
		$("#showLSAModel").show();
	} else {
		$("#showLSAModel").hide();
	}
};
function showMSISDNModel() {
	if ($("#flexCheckMSISDN").is(':checked')) {
		$("#showMSISDNModel").show();
	} else {
		$("#showMSISDNModel").hide();
	}
};
function showDateRangeModel() {
	if ($("#flexCheckTIMESTAMP").is(':checked')) {
		$("#showDateRangeModel").show();
	} else {
		$("#showDateRangeModel").hide();
	}
};

// submit form to backend
var jsonData;
function validateRequest() {

	// document.getElementById("lbllsa1").style.visibility = "hidden";
	// document.getElementById("lblmsisdn1").style.visibility = "hidden";
	document.getElementById("lbllsa2").style.visibility = "hidden";
	document.getElementById("lblmsisdn2").style.visibility = "hidden";
	// document.getElementById("lblstartdatefull").style.visibility = "hidden";
	// document.getElementById("lblenddatefull").style.visibility = "hidden";
	document.getElementById("lblstartdatepartial").style.visibility = "hidden";
	// document.getElementById("lblenddatepartial").style.visibility = "hidden";

	var zoneType;
	var reqType;
	var isLSA = 0;
	var isMSISDN = 0;
	var isTimestamp = 0;
	var lsa;
	var msisdn;
	var dateRange;
	var result = false;
	if ($("#flexRadioZone1").is(':checked')) {
		zoneType = $("#flexRadioZone1").val();
		result = true;
	} else if ($("#flexRadioZone2").is(':checked')) {
		zoneType = $("#flexRadioZone2").val();
		result = true;
	} else {
		result = false;
	}
	if ($("#flexRadioFull").is(':checked')) {
		result = true;
		reqType = $("#flexRadioFull").val();
	} else if ($("#flexRadioPartial").is(':checked')) {
		reqType = $("#flexRadioPartial").val();
		result = true;
	} else {
		result = false;
	}
	if (reqType == 'Full') {
		if ($("#flexRadioZone2").is(':checked')) {
			lsa = $("#zone2lsaPartial").val();
			if (lsa.trim() == "") {
				result = false;
				document.getElementById("zone2lbllsa2").style.visibility = "visible";
			}
		}
	} else if (reqType == 'Partial') {
		if ($("#flexCheckLSA").is(':checked')) {
			isLSA = 1;
			lsa = $("#lsaPartial").val();
			if (lsa.trim() == "") {
				result = false;
				document.getElementById("lbllsa2").style.visibility = "visible";
			}
		}
		if ($("#flexCheckMSISDN").is(':checked')) {
			isMSISDN = 1;
			msisdn = $("#msisdnPartial").val();
			if (msisdn.trim() == "") {
				document.getElementById("msisdntext").textContent = "MSISDN cann't be emplty";
				document.getElementById("lblmsisdn2").style.visibility = "visible";
				result = false;
			} else {
				if (msisdn.trim().length == 10) {
					var isDigitInput = /^\d+$/.test(msisdn);
					if (!isDigitInput) {
						document.getElementById("msisdntext").textContent = "Please provide only numerical values.";
						document.getElementById("lblmsisdn2").style.visibility = "visible";
						result = false;
					}
				} else {
					document.getElementById("msisdntext").textContent = "MSISDN should be 10 digits only";
					document.getElementById("lblmsisdn2").style.visibility = "visible";
					result = false;
				}
			}
		}
		if ($("#flexCheckTIMESTAMP").is(':checked')) {
			isTimestamp = 1;
			dateRange = $("#litepickerDateRange2Months").val();
			if (dateRange.trim() == "") {
				result = false;
				document.getElementById("lblstartdatepartial").style.visibility = "visible";
			}
		}
	}

	jsonData = {
		"zoneType" : zoneType,
		"requestType" : reqType,
		"isLSA" : isLSA,
		"isMSISDN" : isMSISDN,
		"isTimestamp" : isTimestamp,
		"lsa" : lsa,
		"msisdn" : msisdn,
		"dateRange" : dateRange
	}
	return result;
}

$(document).ready(function() {
	submits();
});
function submits() {

	var button = $('#Submit')
	button.on('click', function() {
		if (validateRequest()) {
			var form = new FormData();
			form.append("recovery", JSON.stringify(jsonData));
			$.ajax({
				type : "POST",
				url : "api/recoverydb",
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
		}
	});
};

function dateRangeValidate() {
	document.getElementById("lbldaterange").style.visibility = "hidden";

	var date_range = document.getElementById("litepickerDateRange2Months");
	var result = true;

	if (date_range.value == "") {
	} else {
		var dateParts = date_range.value.split(' - ');
		var startDate = dateParts[0];
		var endDate = dateParts[1];
		var daysBetween = calculateDaysBetweenDates(startDate, endDate);
		if (daysBetween > 6) {
			document.getElementById("daterangetext").textContent = "Date should be between 7 days";
			document.getElementById("lbldaterange").style.visibility = "visible";
			result = false;
		}
	}

	return result;
}
function calculateDaysBetweenDates(startDate, endDate) {
	const date1 = new Date(startDate);
	const date2 = new Date(endDate);

	// Calculate the time difference in milliseconds
	const timeDiff = date2 - date1;

	// Calculate the number of days
	const daysDiff = Math.ceil(timeDiff / (1000 * 60 * 60 * 24));

	return daysDiff;
}
