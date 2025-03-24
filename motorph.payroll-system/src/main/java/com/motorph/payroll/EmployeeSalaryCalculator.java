package com.motorph.payroll; // Package declaration

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.InputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**@author TheCatWhoDoesntMeow and Spiklardibirdy */

public class EmployeeSalaryCalculator {

    private static final Logger logger = Logger.getLogger(EmployeeSalaryCalculator.class.getName());

    // Employee class to hold employee details
    static class Employee {
        int employeeNumber;
        String employeeName;
        LocalDate birthday;
        int hoursWorked;
        double grossSalary;

        public Employee(int employeeNumber, String employeeName, LocalDate birthday, int hoursWorked, double grossSalary) {
            this.employeeNumber = employeeNumber;
            this.employeeName = employeeName;
            this.birthday = birthday;
            this.hoursWorked = hoursWorked;
            this.grossSalary = grossSalary;
        }
    }

    public static void main(String[] args) {
        String fileName = "Copy of MotorPH Employee Data.xlsx"; 
        List<Employee> employees = readEmployeeData(fileName);
        
        for (Employee employee : employees) {
            double netPay = calculateNetPay(employee.grossSalary);
            displaySalaryBreakdown(employee, netPay);
        }
    }

    /**
     * Reads employee data from an Excel file and returns a list of Employee objects.
     * 
     * @param fileName the name of the Excel file
     * @return a list of Employee objects
     */
    private static List<Employee> readEmployeeData(String fileName) {
        List<Employee> employees = new ArrayList<>();
        try (InputStream fis = EmployeeSalaryCalculator.class.getClassLoader().getResourceAsStream(fileName);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header row
                try {
                    int employeeNumber = (int) row.getCell(0).getNumericCellValue();
                    String employeeName = row.getCell(1).getStringCellValue();
                    LocalDate birthday = LocalDate.parse(row.getCell(2).getStringCellValue(), DateTimeFormatter.ofPattern("MM-dd-yyyy"));
                    int hoursWorked = (int) row.getCell(3).getNumericCellValue();
                    double grossSalary = row.getCell(4).getNumericCellValue();
                    employees.add(new Employee(employeeNumber, employeeName, birthday, hoursWorked, grossSalary));
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Error processing row " + row.getRowNum() + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error reading the Excel file: " + e.getMessage(), e);
        }
        return employees;
    }

    /**
     * Calculates the net pay after deductions from the gross salary.
     * 
     * @param grossSalary the gross salary of the employee
     * @return the net pay after deductions
     */
    private static double calculateNetPay(double grossSalary) {
        double totalDeductions = calculateDeductions(grossSalary);
        return grossSalary - totalDeductions;
    }

    /**
     * Calculates total deductions including fixed deductions and withholding tax.
     * 
     * @param grossSalary the gross salary of the employee
     * @return the total deductions
     */
    private static double calculateDeductions(double grossSalary) {
        double sss = 200; // Example fixed deduction for SSS
        double philHealth = 200; // Example fixed deduction for PhilHealth
        double pagIbig = 200; // Example fixed deduction for Pag-IBIG
        double incomeTax = calculateWithholdingTax(grossSalary);
        return sss + philHealth + pagIbig + incomeTax; // Total deductions
    }

    /**
     * Calculates withholding tax based on the gross salary.
     * 
     * @param grossSalary the gross salary of the employee
     * @return the calculated withholding tax
     */
    private static double calculateWithholdingTax(double grossSalary) {
        double tax = 0.0;
        if (grossSalary <= 20832) {
            tax = 0;
        } else if (grossSalary <= 33333) {
            tax = (grossSalary - 208 33) * 0.20;
        } else if (grossSalary <= 66667) {
            tax = 2500 + (grossSalary - 33333) * 0.25;
        } else if (grossSalary <= 166667) {
            tax = 10833 + (grossSalary - 66667) * 0.30;
        } else if (grossSalary <= 666667) {
            tax = 40833.33 + (grossSalary - 166667) * 0.32;
        } else {
            tax = 200833.33 + (grossSalary - 666667) * 0.35;
        }
        return tax; // Return the calculated tax
    }

    /**
     * Displays the salary breakdown for an employee.
     * 
     * @param employee the Employee object containing employee details
     * @param netPay the net pay of the employee
     */
    private static void displaySalaryBreakdown(Employee employee, double netPay) {
        DecimalFormat df = new DecimalFormat("#.00");
        System.out.println("Employee Number: " + employee.employeeNumber);
        System.out.println("Employee Name: " + employee.employeeName);
        System.out.println("Birthday: " + employee.birthday);
        System.out.println("Hours Worked: " + employee.hoursWorked);
        System.out.println("Gross Salary: " + df.format(employee.grossSalary));
        System.out.println("Net Pay: " + df.format(netPay));
        System.out.println("-------------------------------");
    }
}