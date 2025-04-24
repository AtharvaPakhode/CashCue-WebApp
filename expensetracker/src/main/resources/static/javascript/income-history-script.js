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
    }

    // ------- Edit Modal Setup -------
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

    document.querySelectorAll('.edit-income-btn').forEach(button => {
        button.addEventListener('click', (e) => {
            const btn = e.currentTarget;
            const source = btn.dataset.source;
            const amount = btn.dataset.amount;
            const incomeId = btn.dataset.id;

            document.getElementById('incomeSourceChange').value = source;
            document.getElementById('incomeAmountChange').value = amount;

            const form = document.getElementById('editIncomeForm');
            form.action = `/user/updateIncome/${incomeId}`;

            editModal.style.visibility = 'visible';
            editModal.style.display = 'flex';
            editModal.style.zIndex = '60';

            backdrop.classList.remove('hidden');
            backdrop.style.zIndex = '50';

            if (navbar) navbar.style.zIndex = '40';
        });
    });

    // ------- Delete Modal Setup -------
    const closeDeleteModal = () => {
        deleteModal.classList.add('hidden');
        backdrop.classList.add('hidden');
        deleteModal.style.visibility = 'hidden';
        deleteModal.style.display = 'none';
    };

    document.getElementById('close-delete-modal').addEventListener('click', closeDeleteModal);

    document.querySelectorAll('.delete-income-btn').forEach(button => {
        button.addEventListener('click', (e) => {
            const btn = e.currentTarget;
            const incomeId = btn.dataset.id;
            const source = btn.closest('.flex').querySelector('p').textContent;

            const deleteMessage = document.getElementById('delete-modal-message');
            deleteMessage.textContent = `Are you sure you want to delete the Income: "${source}"?`;

            deleteModal.style.visibility = 'visible';
            deleteModal.style.display = 'flex';
            deleteModal.style.zIndex = '60';
            backdrop.classList.remove('hidden');
            backdrop.style.zIndex = '50';

            if (navbar) navbar.style.zIndex = '40';

            document.getElementById('delete-confirm-btn').onclick = () => {
                fetch(`/user/deleteIncome/${incomeId}`, { method: 'DELETE' })
                    .then(response => {
                        if (response.ok) {
                            closeDeleteModal();
                            location.reload();
                        } else {
                            console.error('Failed to delete income');
                        }
                    })
                    .catch(error => {
                        console.error('Error deleting income:', error);
                    });
            };
        });
    });
});
