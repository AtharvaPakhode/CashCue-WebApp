document.addEventListener("DOMContentLoaded", function () {
    const filterSelect = document.getElementById("filterSelect");
    const dateFilter = document.getElementById("dateFilter");
    const amountFilter = document.getElementById("amountFilter");

    function toggleFilterFields() {
        const filterValue = filterSelect.value;
        dateFilter.style.display = (filterValue === "date") ? "block" : "none";
        amountFilter.style.display = (filterValue === "amount") ? "block" : "none";
    }

    filterSelect.addEventListener("change", toggleFilterFields);
});