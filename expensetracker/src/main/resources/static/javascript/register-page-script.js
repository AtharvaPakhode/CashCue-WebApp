document.addEventListener("DOMContentLoaded", function () {
    // --- Password Toggle Logic ---
    const togglePassword = document.getElementById("toggle-password");
    const toggleConfirmPassword = document.getElementById("toggle-confirm-password");
    const passwordField = document.getElementById("userpassword");
    const confirmPasswordField = document.getElementById("confirmpassword");

    togglePassword.addEventListener("click", function () {
        const type = passwordField.type === "password" ? "text" : "password";
        passwordField.type = type;
        this.classList.toggle("fa-eye-slash");
        this.classList.toggle("fa-eye");
    });

    toggleConfirmPassword.addEventListener("click", function () {
        const type = confirmPasswordField.type === "password" ? "text" : "password";
        confirmPasswordField.type = type;
        this.classList.toggle("fa-eye-slash");
        this.classList.toggle("fa-eye");
    });

    // --- Tooltip Logic for Register Button ---
    const checkbox = document.getElementById("registercheckbox");
    const registerButton = document.getElementById("registerbutton");
    const tooltip = document.getElementById("tooltip");

    checkbox.addEventListener("change", function () {
        if (checkbox.checked) {
            registerButton.disabled = false;
            tooltip.classList.add("hidden");
        } else {
            registerButton.disabled = true;
        }
    });

    registerButton.addEventListener("mouseenter", function () {
        if (registerButton.disabled) {
            tooltip.classList.remove("hidden");
        }
    });

    registerButton.addEventListener("mouseleave", function () {
        tooltip.classList.add("hidden");
    });
});
