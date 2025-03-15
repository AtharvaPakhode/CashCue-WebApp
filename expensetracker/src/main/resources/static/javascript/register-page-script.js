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