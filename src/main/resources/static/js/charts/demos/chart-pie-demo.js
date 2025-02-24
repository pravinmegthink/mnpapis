function generatePieChart(label,data) {
	var ctx = document.getElementById('myPieChart').getContext('2d');
	var myPieChart = new Chart(ctx, {
		type : 'pie',
		data : {
			labels : label,
			datasets : [ {
				data : data,
				backgroundColor : [ primaryColor, infoColor, secondaryColor,
						warningColor ],
			} ],
		},
	})
};

