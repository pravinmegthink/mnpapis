function commonValidate() {
	document.getElementById("lblmsisdn").style.visibility = "hidden";
	document.getElementById("lbldaterange").style.visibility = "hidden";

	var msisdn = document.getElementById("msisdn");
	var date_range = document.getElementById("litepickerDateRange2Months");
	var result = true;

	if (msisdn.value.trim() == "") {
	} else {
		if (msisdn.value.trim().length == 10) {
			var isDigitInput = /^\d+$/.test(msisdn.value);
			if (!isDigitInput) {
				document.getElementById("msisdntext").textContent = "Please provide only numerical values.";
				document.getElementById("lblmsisdn").style.visibility = "visible";
				result = false;
			}
		} else {
			document.getElementById("msisdntext").textContent = "Start range should be 10 digits only";
			document.getElementById("lblmsisdn").style.visibility = "visible";
			result = false;
		}
	}
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
