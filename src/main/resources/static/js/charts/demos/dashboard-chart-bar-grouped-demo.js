function generateChart(xAxis, dashboardDataSet) {
	var ctx = document.getElementById('dashboardBarChart').getContext('2d');
	var myBarChart = new Chart(ctx, {
		type : 'bar',
		data : {
			labels : xAxis,
			datasets : dashboardDataSet
		},
		options : {
			scales : {
				x : {
					time : {
						unit : 'date'
					},
					gridLines : {
						display : false
					},
					ticks : {
						maxTicksLimit : 12
					},
				},
				y : {
					ticks : {
						min : 0,
						max : 50000,
						maxTicksLimit : 5
					},
					gridLines : {
						color : 'rgba(0, 0, 0, .075)',
					},
				},
			},
			plugins : {
				legend : {
					display : false
				},
				tooltip : {
					displayColors : true
				}
			},
		}
	})
};
