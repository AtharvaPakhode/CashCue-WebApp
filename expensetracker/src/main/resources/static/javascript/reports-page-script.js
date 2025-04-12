const buttons = document.querySelectorAll('#buttonGroup button');

buttons.forEach(button => {
  button.addEventListener('click', (e) => {

    // Submit the form manually after styling update
    button.closest('form').submit();
  });
});




//chart monthly:

document.addEventListener("DOMContentLoaded", function () {
    const ctx = document.getElementById("expenseChart").getContext("2d");

    new Chart(ctx, {
        type: "line",
        data: {
            labels: chartData.months,
            datasets: [
                {
                    label: "Income",
                    data: chartData.income,
                    borderColor: "rgb(34,197,94)",
                    tension: 0,

                },
                {
                    label: "Expense",
                    data: chartData.expense,
                    borderColor: "rgb(239,68,68)",
                    tension: 0,

                }
            ]
        },
        options: {
            responsive: true,
            plugins: {
                legend: { position: "top" },
                tooltip: {
                    enabled: true,
                    mode: 'index',  // Ensures both datasets show when they intersect
                    intersect: false,  // Ensures tooltips show even if not directly on the line
                    backgroundColor: "rgba(0,0,0,0.7)",  // Tooltip background color
                    titleFont: {
                        size: 14,
                        weight: 'bold',
                    },
                    bodyFont: {
                        size: 12,
                    },
                    callbacks: {
                        label: function(tooltipItem) {
                            return tooltipItem.dataset.label + ": " + tooltipItem.raw;
                        }
                    }
                },
                title: {
                    display: false,
                    text: "Monthly Expense Trend"
                }
            },
            scales: {
                y: {
                    beginAtZero: true,  // Starts the scale from 0
                    ticks: {
                        // Set stepSize explicitly to a rounded number (5000 in this case)
                        stepSize: 5000,  // Every 5000 units (e.g., 0, 5000, 10000, etc.)
                        max: Math.ceil(Math.max(...chartData.income, ...chartData.expense) / 5000) * 5000, // Round up the max value to the nearest multiple of 5000
                        min: 0,  // You can adjust this to start from a specific value
                    },
                    grid: {
                        color: "rgba(0,0,0,0.1)" // Light grid lines
                    }
                }
            }
        }
    });
});


const toggleBtn = document.getElementById('toggleChartBtn');
    const chartContainer = document.getElementById('chartContainer');
    const toggleText = document.getElementById('toggleChartText');

    toggleBtn.addEventListener('click', () => {
        const isHidden = chartContainer.classList.contains('hidden');
        chartContainer.classList.toggle('hidden');
        toggleText.textContent = isHidden ? 'Click to hide' : 'Click to view';
    });