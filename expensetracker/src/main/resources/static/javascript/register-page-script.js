//code for tooltip show and hide for create account button

document.addEventListener("DOMContentLoaded", function () {
    const checkbox = document.getElementById("registercheckbox");
    const registerButton = document.getElementById("registerbutton");



    // Create the tooltip (with 'manual' trigger)
    const tooltip = new bootstrap.Tooltip(registerButton, {
        title: "You must agree to the Terms and Conditions before submitting.",
        placement: "bottom",
        trigger: "manual" // Manually control the tooltip
    });

    // Enable the button when the checkbox is checked
    checkbox.addEventListener("change", function () {
        if (checkbox.checked) {
            registerButton.disabled = false;
            tooltip.hide(); // Hide tooltip when button is enabled
        } else {
            registerButton.disabled = true;
            tooltip.hide(); // Make sure tooltip is hidden when the button is disabled
        }
    });

    // Show the tooltip on hover when the button is disabled
    registerButton.addEventListener("mouseenter", function () {
        if (registerButton.disabled) {
            tooltip.show(); // Show tooltip when hovering over the disabled button
        }
    });

    // Hide the tooltip when the mouse leaves the button
    registerButton.addEventListener("mouseleave", function () {
        tooltip.hide(); // Hide tooltip when the mouse leaves the button
    });
});



// --------------------------------------------------------------------------

//code for eye button for password and confirm password fields

document.addEventListener("DOMContentLoaded", function () {
    const togglePassword = document.getElementById("toggle-password");
    const toggleConfirmPassword = document.getElementById("toggle-confirm-password");
    const passwordField = document.getElementById("userpassword");
    const confirmPasswordField = document.getElementById("confirmpassword");

    // Toggle password visibility
    togglePassword.addEventListener("click", function () {
        const type = passwordField.type === "password" ? "text" : "password";
        passwordField.type = type;

        // Toggle the eye icon
        this.classList.toggle("fa-eye-slash");
        this.classList.toggle("fa-eye");
    });

    // Toggle confirm password visibility
    toggleConfirmPassword.addEventListener("click", function () {
        const type = confirmPasswordField.type === "password" ? "text" : "password";
        confirmPasswordField.type = type;

        // Toggle the eye icon
        this.classList.toggle("fa-eye-slash");
        this.classList.toggle("fa-eye");
    });
});


// --------------------------------------------------------------------------