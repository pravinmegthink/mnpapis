// start code for download sample file
var quoteData = [ [ '9810100001', '200101', '1001', '2002' ] ];
function download_quotefile() {
	var csv = 'msisdn,pincode,sim,imsi\n';
	quoteData.forEach(function(row) {
		csv += row.join(',');
		csv += "\n";
	});
	var hiddenElement = document.createElement('a');
	hiddenElement.href = 'data:text/csv;charset=utf-8,' + encodeURI(csv);
	hiddenElement.target = '_blank';
	hiddenElement.download = 'plannumber.csv';
	hiddenElement.click();
}

// start code for change input column for personal and corporate plan number
$(document).ready(function() {
	$('#dataType').on('change', function() {
		if (this.value == '1') {
			$("#corporate").hide();
			$("#personal").show();
		} else {
			$("#personal").hide();
			$("#corporate").show();
		}
	});
});

/* start code for add new row in table */
$('document')
		.ready(
				function() {
					$('.add_another')
							.click(
									function() {
										$("#example1")
												.append(
														'<tr><td contenteditable="true"></td><td contenteditable="true"></td><td contenteditable="true"></td><td contenteditable="true"></td></tr>');
									});
				});

/* end code for add new row in table */
var bulkfile;
var docfile;
var fileSize = 0;
$(document)
		.ready(
				function() {
					uploadFiles()
					function uploadFiles() {
						var bulkUploader = $('.form-controls');
						bulkUploader.on('change', function() {
							bulkfile = bulkUploader[0].files[0];
						});
						var docUploader = $('.doc-control');
						docUploader
								.on(
										'change',
										function() {
											var myFile = "";
											myFile = docUploader.val();
											var upld = myFile.split('.').pop();
											if (upld == 'pdf') {
												fileSize = docUploader[0].files[0].size;
												fileSize = Math
														.floor(fileSize / 1048576);
												if (fileSize <= 20) {
													docfile = docUploader[0].files[0];
												} else {
													alert('Document is too large. Please upload less than 10mb.');
												}
											} else {
												alert("Only PDF are allowed");
											}
										})
					}
				})
/* start code to submit request into BACKEND */
var billingUID1 = document.getElementById("billingUID1")
var instanceID = document.getElementById("instanceID")
var rno = document.getElementById("rno")
var dno = document.getElementById("dno")
var area = document.getElementById("area")
var rn = document.getElementById("rn")
var companyCode = document.getElementById("companyCode")
var service = document.getElementById("service")
var dataType = document.getElementById("dataType")
var orderType = document.getElementById("orderType")
var partnerID = document.getElementById("partnerID")
var msisdn = document.getElementById("msisdn")
var owner = document.getElementById("owner")
var dummyMSISDN = document.getElementById("dummyMSISDN")
// var sim = document.getElementById("sim")
// var imsi = document.getElementById("imsi")
var hlr = document.getElementById("hlr")
// var pinCode = document.getElementById("pinCode")

var btnSubmit = document.getElementById("btnSubmit")

// add click event listener, to get data when data is entered
btnSubmit.addEventListener("click", function() {
	// GET data from table for corporate request
	var table = $('#example1').DataTable({
		"dom" : 'rtip',
		"bPaginate" : false,
		"bLengthChange" : false,
		"bFilter" : true,
		"bInfo" : false,
		"bAutoWidth" : false,
		"destroy" : true,
	});
	var data = table.rows().data();
	var arrayLength = data.length;
	var corporateData = [];
	for (var i = 0; i < arrayLength; i++) {
		var col1 = data[i][0];
		var col2 = data[i][1];
		var col3 = data[i][2];
		var col4 = data[i][3];
		var dtCorporate = {
			"msisdn" : col1,
			"pinCode" : col2,
			"sim" : col3,
			"imsi" : col4,
			"dummyMSISDN" : dummyMSISDN.value,
			"hlr" : hlr.value
		};
		corporateData.push(dtCorporate);
	}
	// store data in json object
	var jsonDatas;
	if (dataType.value == 2) {
		jsonDatas = {
			"billingUID1" : billingUID1.value,
			"instanceID" : instanceID.value,
			"rno" : rno.value,
			"dno" : dno.value,
			"area" : area.value,
			"rn" : rn.value,
			"companyCode" : companyCode.value,
			"service" : service.value,
			"dataType" : dataType.value,
			"orderType" : orderType.value,
			"partnerID" : partnerID.value,
			"hlr" : hlr.value,
			"dummyMSISDN" : dummyMSISDN.value,
			"subscriberArrType" : corporateData,
			"customerData" : {
				"subscriberId" : "123456789",
				"remark1" : null,
				"remark2" : null,
				"remark3" : null,
				"remark4" : null,
				"remark5" : null
			}
		}
	} else {
		jsonDatas = {
			"billingUID1" : billingUID1.value,
			"instanceID" : instanceID.value,
			"rno" : rno.value,
			"dno" : dno.value,
			"area" : area.value,
			"rn" : rn.value,
			"companyCode" : companyCode.value,
			"service" : service.value,
			"dataType" : dataType.value,
			"orderType" : orderType.value,
			"partnerID" : partnerID.value,
			"hlr" : hlr.value,
			"dummyMSISDN" : dummyMSISDN.value,
			"subscriberSequence" : {
				"subscriberNumber" : msisdn.value
			},
			"personCustomer" : {
				"ownerName" : owner.value,
				"ownerId" : companyCode.value,
				"typeOfId" : 1,
				"signatureDate" : "2023-01-01"
			}
		}
	}
	// convert json data into formDat and send ajax request to application
	var form = new FormData();
	form.append("portme", JSON.stringify(jsonDatas));
	form.append("docFile", docfile);
	if (bulkfile === undefined) {
	} else {
		form.append("bulkUpload", bulkfile);
	}
	$('#loader').show();
	$.ajax({
		type : "POST",
		url : "api/initportrequest",
		success : function(result) {
			var response = JSON.parse(result);
			alert(response.responseMessage);
			if (response.responseCode == 200) {
				$("#btnSubmit").prop("disabled", true);
				$('#loader').hide();
			} else {
				$('#loader').hide();
			}
		},
		error : function(error) {
			console.log(error);
			$('#loader').hide();
		},
		async : true,
		data : form,
		cache : false,
		contentType : false,
		processData : false,
		timeout : 60000
	});
});

// start shobhit
$("#area").on('change', function() {
	console.log("list item selected");
	const area = this.value;
	var opId = document.getElementById("rno").value;
	$.ajax({
		type : "GET",
		url : "getrn?op_id=" + opId + "&area=" + area,
		success : function(result) {
			document.getElementById("rn").value = JSON.parse(result);
		},
		error : function(error) {
			console.log(error);
		},
		async : true,
		cache : false,
		contentType : false,
		processData : false,
		timeout : 60000
	});

});

$("#msisdn").on('change', function() {
	console.log("Item selected");
	var msisdn = $("#msisdn").val();

	// Show loading indicator
	$("#dno").val("Loading...");

	$.ajax({
		type : "GET",
		url : "getmsisdn",
		data : {
			msisdn : msisdn
		},
		success : function(result) {
			$("#dno").val(JSON.parse(result));
		},
		error : function(jqXHR, textStatus, errorThrown) {
			console.error("Error fetching data:", textStatus, errorThrown);
			$("#dno").val("Number range not defined ");
		},
		async : true,
		cache : false,
		contentType : "application/json",
		processData : true,
		timeout : 60000
	});
});
function prefixValidate() {
	// document.getElementById("lblop_id").style.visibility = "hidden";
	document.getElementById("msisdn").style.visibility = "hidden";
	var start_range = document.getElementById("msisdn");
	var result = true;

	if (msisdn.value.trim() == "") {
		document.getElementById("msisdntext").textContent = "msisdn can't be empty";
		document.getElementById("msisdn").style.visibility = "visible";
		result = false;
	} else {
		if (msisdn.value.trim().length == 10) {
			var isDigitInput = /^\d+$/.test(msisdn.value);
			if (!isDigitInput) {
				document.getElementById("msisdntext").textContent = "Please provide only numerical values.";
				document.getElementById("msisdn").style.visibility = "visible";
				result = false;
			}
		} else {
			document.getElementById("msisdntext").textContent = "msisdn should be 10 digits only";
			document.getElementById("msisdn").style.visibility = "visible";
			result = false;
		}
	}
	return result;
}

// end shobhit
