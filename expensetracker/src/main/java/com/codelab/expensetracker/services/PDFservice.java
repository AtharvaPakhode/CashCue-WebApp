package com.codelab.expensetracker.services;

import com.codelab.expensetracker.models.User;
import com.codelab.expensetracker.repositories.CategoryRepository;
import com.codelab.expensetracker.repositories.ExpenseRepository;
import com.codelab.expensetracker.repositories.IncomeRepository;
import com.codelab.expensetracker.repositories.UserRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;


@Service
public class PDFservice {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private IncomeRepository incomeRepository;
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private  CategoryService categoryService;
    

    public void generatePdf(HttpServletResponse response, String period, User user) throws IOException, DocumentException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=report.pdf");

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // Fonts with better styling
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, new BaseColor(35, 40, 45));  // Darker tones for headings
        Font infoFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, new BaseColor(35, 40, 45));
        Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 14, Font.ITALIC, new BaseColor(97, 112, 119));  // Grey for subtitles
        Font tableHeaderFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);  // White for table headers
        Font bodyFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, new BaseColor(50, 50, 50));  // Dark grey for body text


        // Title Section
        Paragraph header = new Paragraph("Financial Report", titleFont);
        header.setAlignment(Element.ALIGN_CENTER);
        document.add(header);
        document.add(Chunk.NEWLINE);

        // Subtitle Section
        Paragraph subtitle = new Paragraph("Detailed overview of expenses and income.", subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        document.add(subtitle);
        document.add(Chunk.NEWLINE);

        // Header section (User Info)
        int userId= user.getUserId();
        String username = user.getUserName();
        String userEmail = user.getUserEmail(); 

        Paragraph userInfoHeader = new Paragraph("User Information", infoFont);
        userInfoHeader.setAlignment(Element.ALIGN_LEFT);
        document.add(userInfoHeader);
        document.add(new Paragraph("User ID: "+userId, bodyFont));
        document.add(new Paragraph("Username: "+username , bodyFont));
        document.add(new Paragraph("User Email: "+userEmail, bodyFont));        
        document.add(Chunk.NEWLINE);
        
        
        

        // First Table: Summary of expenses and income
        PdfPTable summaryTable = new PdfPTable(3);
        summaryTable.setWidthPercentage(100);
        summaryTable.setSpacingBefore(10f);
        summaryTable.setSpacingAfter(10f);

        Double totalExpense = null;
        Double totalIncome = null;
        Double netSavings = null;


        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).atTime(LocalTime.MAX);


        int month = LocalDate.now().getMonthValue();
        int year =LocalDate.now().getYear();
        LocalDate startDate;
        LocalDate endDate;

        switch (period.toLowerCase()) {
            case "monthly":
                totalExpense = this.expenseRepository.findSumOfExpensesForCurrentMonth(user, startOfMonth, endOfMonth);
                totalIncome = this.incomeRepository.findSumOfIncomeForCurrentMonth(user, startOfMonth, endOfMonth);
                totalExpense = (totalExpense != null) ? totalExpense : 0.0;
                totalIncome = (totalIncome != null) ? totalIncome : 0.0;

                netSavings=totalIncome-totalExpense;
                
                addTableHeader(summaryTable, new String[]{
                        "Total Expenses for Current Month",
                        "Total Income for Current Month",
                        "Net Savings for Current Month"
                }, tableHeaderFont);

                summaryTable.addCell(new PdfPCell(new Phrase("₹ "+totalExpense, bodyFont)));
                summaryTable.addCell(new PdfPCell(new Phrase("₹ "+totalIncome, bodyFont)));
                summaryTable.addCell(new PdfPCell(new Phrase("₹ "+netSavings, bodyFont)));

                document.add(summaryTable);
                document.add(Chunk.NEWLINE);
                
                break;

            case "quarterly":
                if (month >= 1 && month <= 3) {
                    startDate = LocalDate.of(year, 1, 1);
                    endDate = LocalDate.of(year, 3, 31);
                } else if (month >= 4 && month <= 6) {
                    startDate = LocalDate.of(year, 4, 1);
                    endDate = LocalDate.of(year, 6, 30);
                } else if (month >= 7 && month <= 9) {
                    startDate = LocalDate.of(year, 7, 1);
                    endDate = LocalDate.of(year, 9, 30);
                } else {
                    startDate = LocalDate.of(year, 10, 1);
                    endDate = LocalDate.of(year, 12, 31);
                }
                LocalDateTime startOfQuarter = startDate.atStartOfDay();
                LocalDateTime endOfQuarter = endDate.atTime(LocalTime.MAX);

                totalExpense=this.expenseRepository.findSumOfExpenseForQuarter(user, startOfQuarter, endOfQuarter);
                totalIncome=this.incomeRepository.findSumOfIncomeForQuarter(user, startOfQuarter, endOfQuarter);
                totalExpense = (totalExpense != null) ? totalExpense : 0.0;
                totalIncome = (totalIncome != null) ? totalIncome : 0.0;

                netSavings=totalIncome-totalExpense;

                addTableHeader(summaryTable, new String[]{
                        "Total Expenses for Current Quarter",
                        "Total Income for Current Quarter",
                        "Net Savings for Current Quarter"
                }, tableHeaderFont);

                summaryTable.addCell(new PdfPCell(new Phrase("₹ "+totalExpense, bodyFont)));
                summaryTable.addCell(new PdfPCell(new Phrase("₹ "+totalIncome, bodyFont)));
                summaryTable.addCell(new PdfPCell(new Phrase("₹ "+netSavings, bodyFont)));

                document.add(summaryTable);
                document.add(Chunk.NEWLINE);
                
                break;

            case "yearly":
                startDate=LocalDate.of(year,1,1);
                endDate=LocalDate.of(year,12,31);

                LocalDateTime startOfYear = startDate.atStartOfDay();
                LocalDateTime endOfYear = endDate.atTime(LocalTime.MAX);

                totalIncome = this.incomeRepository.findSumOfIncomeForYear(user, startOfYear, endOfYear);
                totalExpense = this.expenseRepository.findSumOfExpenseForYear(user, startOfYear, endOfYear);
                totalExpense = (totalExpense != null) ? totalExpense : 0.0;
                totalIncome = (totalIncome != null) ? totalIncome : 0.0;

                netSavings=totalIncome-totalExpense;

                addTableHeader(summaryTable, new String[]{
                        "Total Expenses for Current Year",
                        "Total Income for Current Year",
                        "Net Savings for Current Year"
                }, tableHeaderFont);

                summaryTable.addCell(new PdfPCell(new Phrase("₹ "+totalExpense, bodyFont)));
                summaryTable.addCell(new PdfPCell(new Phrase("₹ "+totalIncome, bodyFont)));
                summaryTable.addCell(new PdfPCell(new Phrase("₹ "+netSavings, bodyFont)));

                document.add(summaryTable);
                document.add(Chunk.NEWLINE);

                break;

            default:
                System.out.println("Invalid option.");
                break;
        }

        

        // Second Table: Category Breakdown with alternating row colors
        PdfPTable categoryTable = new PdfPTable(3);
        categoryTable.setWidthPercentage(100);
        categoryTable.setSpacingBefore(10f);
        categoryTable.setSpacingAfter(10f);


        Map<String, Double> currentCategorySums = null;        
        Double totalExpenseByUser;
        Double percentageOfTotal;

        switch (period.toLowerCase()) {
            case "monthly":
                document.add(new Paragraph("Monthwise Spending of Category", bodyFont));
                addTableHeader(categoryTable, new String[]{
                        "Expense Category",
                        "Total Amount Spend",
                        "% of Total Expense"
                }, tableHeaderFont);

                currentCategorySums = this.categoryService.getCurrentMonthCategorySums(user);
                for (Map.Entry<String, Double> entry : currentCategorySums.entrySet()) {

                    totalExpenseByUser = this.expenseRepository.findSumOfExpensesCurrentMonth(user);

                    String category = entry.getKey();
                    Double categoryAmount = entry.getValue();

                    if (totalExpenseByUser != null && totalExpenseByUser > 0) {
                        percentageOfTotal = (categoryAmount / totalExpenseByUser) * 100;
                    } else {
                        percentageOfTotal = 0.0;
                    }

                    String formattedPercentage = String.format("%.2f", percentageOfTotal) + "%";
                    String formattedAmount = "₹ " + String.format("%.2f", categoryAmount);

                    addCategoryRow(categoryTable, category, formattedAmount, formattedPercentage, bodyFont, BaseColor.WHITE);

                }
                break;

            case "quarterly":
                document.add(new Paragraph("Quarterwise Spending of Category", bodyFont));
                addTableHeader(categoryTable, new String[]{
                        "Expense Category",
                        "Total Amount Spend",
                        "% of Total Expense"
                }, tableHeaderFont);

                currentCategorySums = this.categoryService.getCurrentQuarterCategorySums(user);
                for (Map.Entry<String, Double> entry : currentCategorySums.entrySet()) {

                    totalExpenseByUser = this.expenseRepository.findSumOfExpensesCurrentQuarter(user);

                    String category = entry.getKey();
                    Double categoryAmount = entry.getValue();

                    if (totalExpenseByUser != null && totalExpenseByUser > 0) {
                        percentageOfTotal = (categoryAmount / totalExpenseByUser) * 100;
                    } else {
                        percentageOfTotal = 0.0;
                    }

                    String formattedPercentage = String.format("%.2f", percentageOfTotal) + "%";
                    String formattedAmount = "₹ " + String.format("%.2f", categoryAmount);

                    addCategoryRow(categoryTable, category, formattedAmount, formattedPercentage, bodyFont, BaseColor.WHITE);

                }

                break;

            case "yearly":
                document.add(new Paragraph("Yearwise Spending of Category", bodyFont));
                addTableHeader(categoryTable, new String[]{
                        "Expense Category",
                        "Total Amount Spend",
                        "% of Total Expense"
                }, tableHeaderFont);

                currentCategorySums = this.categoryService.getCurrentYearCategorySums(user);
                for (Map.Entry<String, Double> entry : currentCategorySums.entrySet()) {

                    totalExpenseByUser = this.expenseRepository.findSumOfExpensesCurrentYear(user);

                    String category = entry.getKey();
                    Double categoryAmount = entry.getValue();

                    if (totalExpenseByUser != null && totalExpenseByUser > 0) {
                        percentageOfTotal = (categoryAmount / totalExpenseByUser) * 100;
                    } else {
                        percentageOfTotal = 0.0;
                    }

                    String formattedPercentage = String.format("%.2f", percentageOfTotal) + "%";
                    String formattedAmount = "₹ " + String.format("%.2f", categoryAmount);

                    addCategoryRow(categoryTable, category, formattedAmount, formattedPercentage, bodyFont, BaseColor.WHITE);

                }

                break;

            default:
                System.out.println("Invalid option.");
                break;
        }

        document.add(categoryTable);


        try {

            // Start a new page for visual content
            document.newPage();
            
            // Load chart image
            Path chartPath = Paths.get("/uploads/line-chart-image/chart.png");
            if (Files.exists(chartPath)) {
                byte[] chartBytes = Files.readAllBytes(chartPath);
                System.out.println(chartBytes);
                Image chartImage = Image.getInstance(chartBytes);
                chartImage.scaleToFit(500, 300);
                chartImage.setAlignment(Image.ALIGN_CENTER);
                Paragraph chartHeading = new Paragraph("Line Chart Visualization", infoFont);
                chartHeading.setAlignment(Element.ALIGN_CENTER);
                document.add(chartHeading);
                document.add(chartImage);
                document.add(Chunk.NEWLINE);
            }

            // Load table image
            Path tablePath = Paths.get("/uploads/line-chart-table-image/table.png");
            if (Files.exists(tablePath)) {
                byte[] tableBytes = Files.readAllBytes(tablePath);
                System.out.println(tableBytes);
                Image tableImage = Image.getInstance(tableBytes);
                tableImage.scaleToFit(500, 300);
                tableImage.setAlignment(Image.ALIGN_CENTER);
                Paragraph tableHeading = new Paragraph("Table Snapshot", infoFont);
                tableHeading.setAlignment(Element.ALIGN_CENTER);
                document.add(tableHeading);
                
                document.add(tableImage);
            }

        } catch (Exception e) {
            e.printStackTrace(); // Log error in case images are missing
        }


        try {

            // Start a new page for visual content
            document.newPage();

            // Load chart image
            Path chartPath = Paths.get("/uploads/pie-chart-image/chart.png");
            if (Files.exists(chartPath)) {
                byte[] chartBytes = Files.readAllBytes(chartPath);
                System.out.println(chartBytes);
                Image chartImage = Image.getInstance(chartBytes);
                chartImage.scaleToFit(500, 300);
                chartImage.setAlignment(Image.ALIGN_CENTER);
                Paragraph chartHeading = new Paragraph("Pie Chart Visualization", infoFont);
                chartHeading.setAlignment(Element.ALIGN_CENTER);
                document.add(chartHeading);
                document.add(chartImage);
                document.add(Chunk.NEWLINE);
            }

            // Load table image
            Path tablePath = Paths.get("/uploads/pie-chart-table-image/table.png");
            if (Files.exists(tablePath)) {
                byte[] tableBytes = Files.readAllBytes(tablePath);
                System.out.println(tableBytes);
                Image tableImage = Image.getInstance(tableBytes);
                tableImage.scaleToFit(500, 300);
                tableImage.setAlignment(Image.ALIGN_CENTER);
                Paragraph tableHeading = new Paragraph("Table Snapshot", infoFont);
                tableHeading.setAlignment(Element.ALIGN_CENTER);
                document.add(tableHeading);

                document.add(tableImage);
            }

        } catch (Exception e) {
            e.printStackTrace(); // Log error in case images are missing
        }

        // Closing the document
        document.close();
    }

    private void addTableHeader(PdfPTable table, String[] headers, Font font) {
        for (String header : headers) {
            PdfPCell headerCell = new PdfPCell();
            headerCell.setBackgroundColor(new BaseColor(35, 40, 45));
            headerCell.setPhrase(new Phrase(header, font));
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(headerCell);
        }
    }

    private void addCategoryRow(PdfPTable table, String category, String amount, String percentage, Font font, BaseColor backgroundColor) {
        PdfPCell categoryCell = new PdfPCell(new Phrase(category, font));
        categoryCell.setBackgroundColor(backgroundColor);
        categoryCell.setPadding(8);
        categoryCell.setBorderWidth(0.5f);
        table.addCell(categoryCell);

        PdfPCell amountCell = new PdfPCell(new Phrase(amount, font));
        amountCell.setBackgroundColor(backgroundColor);
        amountCell.setPadding(8);
        amountCell.setBorderWidth(0.5f);
        table.addCell(amountCell);

        PdfPCell percentageCell = new PdfPCell(new Phrase(percentage, font));
        percentageCell.setBackgroundColor(backgroundColor);
        percentageCell.setPadding(8);
        percentageCell.setBorderWidth(0.5f);
        table.addCell(percentageCell);
    }


}