// Show loader on page load
document.getElementById('wifi-loader').style.display = 'flex';

// Button group form submission
const buttons = document.querySelectorAll('#buttonGroup button');
buttons.forEach(button => {
    button.addEventListener('click', (e) => {
        e.preventDefault();
        button.closest('form').submit();
    });
});

// Wait for DOM to load
document.addEventListener("DOMContentLoaded", function () {
    // Delay execution by 3 seconds
    setTimeout(() => {
        // Line Chart Rendering
        const ctx = document.getElementById("expenseChart").getContext("2d");

        new Chart(ctx, {
            type: "line",
            data: {
                labels: chartData.xValue,
                datasets: [
                    {
                        label: "Income",
                        data: chartData.income,
                        borderColor: "rgb(34,197,94)",
                        tension: 0
                    },
                    {
                        label: "Expense",
                        data: chartData.expense,
                        borderColor: "rgb(239,68,68)",
                        tension: 0
                    }
                ]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: { position: "top" },
                    tooltip: {
                        enabled: true,
                        mode: 'index',
                        intersect: false,
                        backgroundColor: "rgba(0,0,0,0.7)",
                        titleFont: { size: 14, weight: 'bold' },
                        bodyFont: { size: 12 },
                        callbacks: {
                            label: function (tooltipItem) {
                                return tooltipItem.dataset.label + ": " + tooltipItem.raw;
                            }
                        }
                    },
                    title: {
                        display: false,
                        text: "Monthly Expense Trend"
                    }
                },
                animation: {
                    onComplete: function () {
                        populateExpenseTable();
                        console.log("Chart rendering complete.");

                        // Capture chart image
                        const chartImgData = ctx.canvas.toDataURL("image/png");

                        // Capture table image
                        html2canvas(document.querySelector("#expenseTable-lineChart"), {
                            useCORS: true,
                            allowTaint: false,
                            backgroundColor: null
                        }).then(function (canvas) {
                            const tableImgData = canvas.toDataURL("image/png");
                            saveChartImage(chartImgData, "line-chart-image", tableImgData);
                        });
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            stepSize: 5000,
                            max: Math.ceil(Math.max(...chartData.income, ...chartData.expense) / 5000) * 5000,
                            min: 0
                        },
                        grid: {
                            color: "rgba(0,0,0,0.1)"
                        }
                    }
                }
            }
        });

        // Pie Chart Rendering
        const categoryCtx = document.getElementById("categoryExpenseChart").getContext("2d");

        new Chart(categoryCtx, {
            type: "pie",
            data: {
                labels: chartDataCategory.category,
                datasets: [{
                    label: "Expenses by Category",
                    data: chartDataCategory.expense,
                    backgroundColor: [
                        "#FF6B6B", "#4ECDC4", "#FFD93D",
                        "#1A535C", "#FF9F1C", "#6A4C93"
                    ],
                    borderColor: "#ffffff",
                    borderWidth: 2
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: { position: "bottom" },
                    title: {
                        display: true,
                        text: "Expenses by Category"
                    }
                }
            }
        });

        // Hide loader after everything is rendered
        document.getElementById('wifi-loader').style.display = 'none';
    }, 3000); // 3-second delay
});

// Populate line chart expense table
function populateExpenseTable() {
    const tableBody = document.querySelector("#expenseTable-lineChart tbody");
    tableBody.innerHTML = "";

    chartData.xValue.forEach((label, index) => {
        const income = chartData.income[index];
        const expense = chartData.expense[index];

        const row = document.createElement("tr");
        row.className = "mb-2";
        row.innerHTML = `
            <td class="py-2 px-4">${label}</td>
            <td class="py-2 px-4">${income}</td>
            <td class="py-2 px-4">${expense}</td>
        `;
        tableBody.appendChild(row);
    });
}

// Save chart and table images to the server
function saveChartImage(imgData, chartType, tableImgData) {
    console.log("Sending chart and table image data to server");

    fetch('/user/save-chart-image', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ chartImgData: imgData, chartType, tableImgData })
    });
}

// Toggle line chart and table visibility
const toggleBtn = document.getElementById('toggleChartBtn');
const chartContainer = document.getElementById('chartContainer');
const expenseTablelineChart = document.getElementById('expenseTable-lineChart');
const toggleText = document.getElementById('toggleChartText');

toggleBtn.addEventListener('click', () => {
    const isHidden = chartContainer.classList.contains('hidden');
    expenseTablelineChart.classList.toggle('hidden');
    chartContainer.classList.toggle('hidden');
    toggleText.textContent = isHidden ? 'Click to hide' : 'Click to view';
});

// Toggle pie chart visibility
const PieToggleBtn = document.getElementById('toggleChartBtnCategory');
const PiechartContainer = document.getElementById('CategoryChartContainer');
const PieToggleText = document.getElementById('togglePieChartText');

PieToggleBtn.addEventListener('click', () => {
    const isHidden = PiechartContainer.classList.contains('hidden');
    PiechartContainer.classList.toggle('hidden');
    PieToggleText.textContent = isHidden ? 'Click to hide' : 'Click to view';
});
