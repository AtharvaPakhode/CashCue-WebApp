document.addEventListener("DOMContentLoaded", () => {
    const filterBtn = document.getElementById("filterTransactions");
    const filterForm = document.getElementById("filterForm");
    const applyFiltersBtn = document.getElementById("applyFilters");

    if (filterForm && filterBtn && applyFiltersBtn) {
        filterForm.style.display = "none";
        filterBtn.classList.remove("visible");
        applyFiltersBtn.classList.remove("visible");

        setTimeout(() => {
            filterBtn.classList.add("visible");
            applyFiltersBtn.classList.add("visible");
        }, 300);

        filterBtn.addEventListener("click", () => {
            filterBtn.classList.add('clicked');
            setTimeout(() => filterBtn.classList.remove('clicked'), 100);
            filterForm.style.display = filterForm.style.display === "none" ? "block" : "none";
        });

        applyFiltersBtn.addEventListener('click', () => {
            applyFiltersBtn.classList.add('clicked');
            setTimeout(() => applyFiltersBtn.classList.remove('clicked'), 100);
        });

        filterForm.addEventListener("submit", (e) => {
            // Optional: only if you want to hide form after submit (e.g., via JS submit)
            filterForm.style.display = "none";
        });
    }

    // ------- Edit Modal Setup for Expenses -------
    const editModal = document.getElementById('edit-modal');
    const deleteModal = document.getElementById('delete-modal');
    const backdrop = document.getElementById('modal-backdrop');
    const navbar = document.getElementById('navbar');

    const closeEditModal = () => {
        editModal.classList.add('hidden');
        backdrop.classList.add('hidden');
        editModal.style.visibility = 'hidden';
        editModal.style.display = 'none';
    };

    document.getElementById('close-modal-btn').addEventListener('click', closeEditModal);

    document.querySelectorAll('.edit-expense-btn').forEach(button => {
        button.addEventListener('click', (e) => {
            const btn = e.currentTarget;
            const title = btn.dataset.title;
            const amount = btn.dataset.amount;
            const expenseId = btn.dataset.id;

            document.getElementById('expenseTitleChange').value = title;
            document.getElementById('expenseAmountChange').value = amount;

            const form = document.getElementById('editExpenseForm');
            form.action = `/user/updateExpense/${expenseId}`;

            editModal.style.visibility = 'visible';
            editModal.style.display = 'flex';
            editModal.style.zIndex = '60';

            backdrop.classList.remove('hidden');
            backdrop.style.zIndex = '50';

            if (navbar) navbar.style.zIndex = '40';
        });
    });

    // ------- Delete Modal Setup for Expenses -------
    const closeDeleteModal = () => {
        deleteModal.classList.add('hidden');
        backdrop.classList.add('hidden');
        deleteModal.style.visibility = 'hidden';
        deleteModal.style.display = 'none';
    };

    document.getElementById('close-delete-modal').addEventListener('click', closeDeleteModal);

    document.querySelectorAll('.delete-expense-btn').forEach(button => {
        button.addEventListener('click', (e) => {
            const btn = e.currentTarget;
            const expenseId = btn.dataset.id;
            const title = btn.closest('.flex').querySelector('p').textContent;

            const deleteMessage = document.getElementById('delete-modal-message');
            deleteMessage.textContent = `Are you sure you want to delete the Expense: "${title}"?`;

            deleteModal.style.visibility = 'visible';
            deleteModal.style.display = 'flex';
            deleteModal.style.zIndex = '60';
            backdrop.classList.remove('hidden');
            backdrop.style.zIndex = '50';

            if (navbar) navbar.style.zIndex = '40';

            document.getElementById('delete-confirm-btn').onclick = () => {
                fetch(`/user/deleteExpense/${expenseId}`, { method: 'DELETE' })
                    .then(response => {
                        if (response.ok) {
                            closeDeleteModal();
                            location.reload();
                        } else {
                            console.error('Failed to delete expense');
                        }
                    })
                    .catch(error => {
                        console.error('Error deleting expense:', error);
                    });
            };
        });
    });
});
