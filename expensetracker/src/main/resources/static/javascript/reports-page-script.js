const buttons = document.querySelectorAll('#buttonGroup button');

buttons.forEach(button => {
  button.addEventListener('click', (e) => {

    // Submit the form manually after styling update
    button.closest('form').submit();
  });
});
