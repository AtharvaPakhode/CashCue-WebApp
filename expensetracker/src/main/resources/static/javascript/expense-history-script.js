document.addEventListener("DOMContentLoaded", () => {
    const filterBtn = document.getElementById("filterTransactions");
    const filterForm = document.getElementById("filterForm");

    // Initially hide the form
    filterForm.style.display = "none";

    // Toggle form visibility when filter button is clicked
    filterBtn.addEventListener("click", () => {
      filterForm.style.display = filterForm.style.display === "none" ? "block" : "none";
    });

    // Hide form on Apply Filters
    filterForm.addEventListener("submit", (e) => {
      // You can handle filter logic here before hiding the form
      filterForm.style.display = "none";
    });
  });

