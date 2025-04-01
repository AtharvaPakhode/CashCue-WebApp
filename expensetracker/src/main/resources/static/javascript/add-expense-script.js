 document.addEventListener("DOMContentLoaded", function () {
        let today = new Date().toISOString().split("T")[0];
        document.getElementById("dateOfExpense").setAttribute("max", today);
    });

/*


Summary of Outputs

Step	           Code	                    Output
1	          new Date();	                Tue Apr 01 2025 10:15:30 GMT+0000
2	         .toISOString();	            "2025-04-01T10:15:30.123Z"
3	         .split("T")[0];	            "2025-04-01"
4	         .setAttribute("max", today);	<input type="date" max="2025-04-01">


*/