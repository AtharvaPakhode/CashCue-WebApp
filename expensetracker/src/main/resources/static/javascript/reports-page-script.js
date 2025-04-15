// Show loader on page load
document.getElementById('wifi-loader').style.display = 'flex';

// Button group form submission
const buttons = document.querySelectorAll('#buttonGroup button');
buttons.forEach(button => {
    button.addEventListener('click', (e) => {

        button.closest('form').submit();
    });
});

const exportPDFbtn =document.getElementById('exportPdf');
exportPDFbtn.disabled = true;
exportPDFbtn.classList.add('opacity-50', 'cursor-not-allowed');


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
                        // Wait for table to render too before capturing
                        requestAnimationFrame(() => {
                            populateExpenseTable();
                            console.log("Chart rendering complete.");

                            // Wait another animation frame to be extra safe
                            requestAnimationFrame(() => {
                                // Capture chart image
                                const chartImgData = ctx.canvas.toDataURL("image/png");

                                // Capture table image using html2canvas
                                html2canvas(document.querySelector("#expenseTable-lineChart"), {
                                    useCORS: true,
                                    backgroundColor: "#ffffff", // helps ensure good PDF rendering
                                    scale: 2 // higher resolution
                                }).then(function (canvas) {
                                    const tableImgData = canvas.toDataURL("image/png");
                                    saveChartImage(chartImgData, "line-chart-image", tableImgData);
                                });
                            });
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
                   backgroundColor: ["#FF6B6B", "#4ECDC4", "#FFD93D", "#1A535C", "#FF9F1C", "#6A4C93"],
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
               },
               animation: {
                   onComplete: function () {
                       requestAnimationFrame(() => {
                                                   populatePieExpenseTable();
                                                   console.log("Chart rendering complete.");

                                                   // Wait another animation frame to be extra safe
                                                   requestAnimationFrame(() => {
                                                       // Capture chart image
                                                       const PieImgData = categoryCtx.canvas.toDataURL("image/png");

                                                       // Capture table image using html2canvas
                                                       html2canvas(document.querySelector("#expenseTable-pieChart"), {
                                                           useCORS: true,
                                                           backgroundColor: "#ffffff", // helps ensure good PDF rendering
                                                           scale: 2 // higher resolution
                                                       }).then(function (canvas) {
                                                           const pieTableImgData = canvas.toDataURL("image/png");
                                                           savePieChartImage(PieImgData, "pie-chart-image", pieTableImgData);
                                                       });
                                                   });
                                               });
                   }
               }
           }
       });


        // Hide loader after everything is rendered
        document.getElementById('wifi-loader').style.display = 'none';
        exportPDFbtn.disabled = false;
        exportPDFbtn.classList.remove('opacity-50', 'cursor-not-allowed');

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
            <td class="py-2 px-4 font-bold text-base ">${label}</td>
            <td class="py-2 px-4 font-bold text-base">${income}</td>
            <td class="py-2 px-4 font-bold text-base">${expense}</td>
        `;
        tableBody.appendChild(row);
    });
}

function populatePieExpenseTable() {
    const tableBodyPie = document.querySelector("#expenseTable-pieChart tbody");
    tableBodyPie.innerHTML = "";

    const dummyData = [
                { category: "Food", expense: 1200 },
                { category: "Rent", expense: 8000 },
                { category: "Utilities", expense: 1500 }
            ];

            dummyData.forEach(data => {
                const row = document.createElement("tr");
                row.className = "mb-2";
                row.innerHTML = `
                    <td class="py-2 px-4 font-bold text-base">${data.category}</td>
                    <td class="py-2 px-4 font-bold text-base">${data.expense}</td>
                `;
                tableBodyPie.appendChild(row);
            });

//    chartDataCategory.category.forEach((categories, index) => {
//        const category = chartDataCategory.category[index];
//        const expense = chartDataCategory.expense[index];
//
//        const row = document.createElement("tr");
//        row.className = "mb-2";
//        row.innerHTML = `
//
//            <td class="py-2 px-4 font-bold text-base">${category}</td>
//            <td class="py-2 px-4 font-bold text-base">${expense}</td>
//        `;
//        tableBody.appendChild(row);
//    });
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

function savePieChartImage(imgData, chartType, tableImgData) {
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


exportPDFbtn.addEventListener('click', function () {
// Store original content
    const originalContent = exportPDFbtn.innerHTML;

    // Change button content to "Exporting..."
    exportPDFbtn.innerHTML = 'Exporting...';
    exportPDFbtn.disabled = true;
    exportPDFbtn.classList.add('opacity-50', 'cursor-not-allowed');

    exportPDFbtn.closest('form').submit();

    // Wait 2 seconds, then restore
    setTimeout(() => {
        exportPDFbtn.innerHTML = originalContent;
        exportPDFbtn.disabled = false;
        exportPDFbtn.classList.remove('opacity-50', 'cursor-not-allowed');
    }, 2000);

});

