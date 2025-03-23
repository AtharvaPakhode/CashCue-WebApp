document.addEventListener('DOMContentLoaded', function() {
    // Hide the modals after the DOM has loaded
    const editModal = document.getElementById('edit-modal');
    const deleteModal = document.getElementById('delete-modal');

    editModal.style.display = 'none';
    deleteModal.style.display = 'none';
    editModal.style.visibility = 'hidden';
    deleteModal.style.visibility = 'hidden';
});

// Function to show the edit modal
const showEditModal = (name, budget) => {
    // Populate the form with the current category data
    document.getElementById('categoryNameChange').value = name;
    document.getElementById('monthlyBudgetChange').value = budget;

    const editModal = document.getElementById('edit-modal');
    const backdrop = document.getElementById('modal-backdrop');
    const navbar = document.getElementById('navbar');

    // Show the modal and backdrop
    editModal.style.visibility = 'visible';  // Make sure it is visible
    editModal.style.display = 'flex';  // Ensure it's shown in the layout
    backdrop.classList.remove('hidden');  // Show the backdrop

    // Set the modal z-index to ensure it's on top of the backdrop
    editModal.style.zIndex = '60';
    backdrop.style.zIndex = '50';
    navbar.style.zIndex = "40";

    // Set the form action to update the category
    const form = document.getElementById('editCategoryForm');
    form.action = `/user/category/update/${name}`;
};

// Close Modal
const closeEditModal = () => {
    const editModal = document.getElementById('edit-modal');
    const backdrop = document.getElementById('modal-backdrop');

    editModal.classList.add('hidden');
    backdrop.classList.add('hidden');

    editModal.style.visibility = 'hidden';  // Hide the modal's visibility
    editModal.style.display = 'none';  // Ensure it's removed from the layout
};

// Attach event listener to close modal button
document.getElementById('close-modal-btn').addEventListener('click', closeEditModal);

// Attach event listeners to edit buttons
document.querySelectorAll('.edit-btn').forEach(button => {
    button.addEventListener('click', (e) => {
        const name = e.target.dataset.name;
        const budget = e.target.dataset.budget;
        showEditModal(name, budget);
    });
});

// Handle form submission for saving changes
document.getElementById('editCategoryForm').addEventListener('submit', function(e) {
    e.preventDefault(); // Prevent default form submission

    const formData = new FormData(this);

    fetch(this.action, {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (response.ok) {
            // Close the modal and reload the page or update the UI dynamically
            closeEditModal();
            location.reload(); // or update the category list dynamically
        } else {
            // Handle errors here (e.g., show an error message)
        }
    })
    .catch(err => {
        console.error('Error updating category:', err);
    });
});

// Function to show the delete confirmation modal
const showDeleteModal = (name) => {
    const deleteModal = document.getElementById('delete-modal');
    const deleteMessage = document.getElementById('delete-modal-message');
    const confirmButton = document.getElementById('delete-confirm-btn');
    const navbar = document.getElementById('navbar');

    deleteMessage.textContent = `Are you sure you want to delete the category: "${name}"?`;

    deleteModal.classList.remove('hidden');
    document.getElementById('modal-backdrop').classList.remove('hidden');
    deleteModal.style.visibility = 'visible';  // Make sure it is visible
    deleteModal.style.display = 'flex';  // Ensure it's shown in the layout

    // Set the delete modal and backdrop z-index
    deleteModal.style.zIndex = '60';
    document.getElementById('modal-backdrop').style.zIndex = '50';
    navbar.style.zIndex = "40";

    confirmButton.onclick = () => deleteCategory(name);
};

// Function to delete the category (You can call your API here)
const deleteCategory = (name) => {
    console.log(name);

    fetch(`/user/deleteCategory/${name}`, { method: 'DELETE' })
    .then(response => {
        if (response.ok) {
            closeDeleteModal();
            location.reload();
        } else {
            console.error('Failed to delete the category');
        }
    })
    .catch(error => {
        console.error('Error deleting category:', error);
    });
};

// Function to close the delete confirmation modal
const closeDeleteModal = () => {
    const deleteModal = document.getElementById('delete-modal');
    const backdrop = document.getElementById('modal-backdrop');

    deleteModal.classList.add('hidden');
    backdrop.classList.add('hidden');

    deleteModal.style.visibility = 'hidden';  // Hide the modal's visibility
    deleteModal.style.display = 'none';  // Ensure it's removed from the layout
};

// Attach event listeners to delete buttons
document.querySelectorAll('.delete-btn').forEach(button => {
    button.addEventListener('click', (e) => {
        const name = e.target.dataset.name;  // Assuming category name is available in data-name
        showDeleteModal(name);
    });
});

// Close the modal when clicking the close icon (X)
document.getElementById('close-delete-modal').addEventListener('click', closeDeleteModal);
