document.addEventListener("DOMContentLoaded", () => {
    const filterBtn = document.getElementById("filterTransactions");
    const filterForm = document.getElementById("filterForm");
    const applyFiltersBtn = document.getElementById("applyFilters");

    // Initially hide the form and buttons
    filterForm.style.display = "none";
    filterBtn.classList.remove("visible");
    applyFiltersBtn.classList.remove("visible");

    // Delay the appearance of the buttons after the page load
    setTimeout(() => {
        filterBtn.classList.add("visible");
        applyFiltersBtn.classList.add("visible");
    }, 300); // Adjust timing as necessary

    // Toggle form visibility when filter button is clicked
    filterBtn.addEventListener("click", () => {
        filterBtn.classList.add('clicked');
        setTimeout(() => filterBtn.classList.remove('clicked'), 100);
        filterForm.style.display = filterForm.style.display === "none" ? "block" : "none";
    });

    // Apply Filters button click animation
    applyFiltersBtn.addEventListener('click', () => {
        applyFiltersBtn.classList.add('clicked');
        setTimeout(() => applyFiltersBtn.classList.remove('clicked'), 100);
    });

    // Hide form on Apply Filters (if submitting the form)
    filterForm.addEventListener("submit", (e) => {
        e.preventDefault(); // Prevent form submission for the sake of handling the hide logic
        filterForm.style.display = "none";

    });
});

