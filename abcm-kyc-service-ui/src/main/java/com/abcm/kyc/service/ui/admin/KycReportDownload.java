package com.abcm.kyc.service.ui.admin;

import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.abcm.kyc.service.ui.dto.ApiResponseModel;
import com.abcm.kyc.service.ui.dto.KycReportRequestModel;
import com.abcm.kyc.service.ui.dto.ReportModel;
import com.abcm.kyc.service.ui.service.AdminService;
import com.abcm.kyc.service.ui.service.KycReportService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/app/public")
@Slf4j
@RequiredArgsConstructor
public class KycReportDownload {

    private final KycReportService kycReportService;
    private final AdminService adminService;

    @PostMapping(value = "downloadTxnReport")
    public void downloadKycReport(HttpServletResponse response,
                                  @RequestParam("mid") String mid,
                                  @RequestParam("dateRange") String dateRange,
                                  @RequestParam("product") String product,
                                  @RequestParam(value = "UserType", required = false) String userType) throws IOException {
    	log.info("downloadKycReport method started");
        String[] dates = dateRange.split(" to ");
        if (dates.length != 2) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid date range format. Expected format: 'dd-MM-yyyy to dd-MM-yyyy'");
            return;
        }
        String fromDate = dates[0].trim();
        String toDate = dates[1].trim();
        log.info("Request received - MID: {}, From Date: {}, To Date: {}, Product: {}, UserType: {}", mid, fromDate, toDate, product, userType);
        try {
            KycReportRequestModel requestModel = new KycReportRequestModel();
            requestModel.setMid(mid);
            requestModel.setFormDate(fromDate);
            requestModel.setToDate(toDate);
            requestModel.setProduct(product);
            requestModel.setPage(1);
            requestModel.setSize(0);
            requestModel.setSearch(mid);
            log.debug("KYC Download Report Request: {}", requestModel);
            ReportModel apiResponse = kycReportService.fetchKycReportData(requestModel);
            log.info("The Download excel Response"+apiResponse);
            if ("user".equalsIgnoreCase(userType)) {
            	log.info("User Report or Merchnat Excel Download");
                generateMerchantExcelReport(response, apiResponse);
            } else {
                log.info("UserType is not 'user' generating standard report.");
                generateExcelReport(response, apiResponse);
            }

        } catch (Exception e) {
            log.error("Error generating KYC report", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating report");
        }
    }


    private void generateExcelReport(HttpServletResponse response, ReportModel apiResponse) throws IOException {
    	log.info(" Admin generateExcelReport method started");
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("KYC Report");

        // Header style
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        String[] headers = {
            "Sr.No", "MID", "Merchant Name", "Provider Name", "Product", "Billable",
            "Identification No", "Customer Name", "Legal Name",
            "Created_At", "Request_At", "Response_At",
            "Track_Id", "Reference_id", "Response Massage", "Status"
        };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Data style
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> dataList = (List<Map<String, Object>>) apiResponse.getData();

        int rowNum = 1;
        for (Map<String, Object> data : dataList) {
            Row dataRow = sheet.createRow(rowNum++);
            int colNum = 0;

            dataRow.createCell(colNum++).setCellValue(rowNum - 1); // Sr.No

            String product = getString(data, "product");
            String identificationNo = getString(data, "identificationNo");
            String maskedIdentificationNo = maskIdentificationNo(identificationNo, product);

            dataRow.createCell(colNum++).setCellValue(getString(data, "merchantId"));
            dataRow.createCell(colNum++).setCellValue(getString(data, "merchantName"));
            dataRow.createCell(colNum++).setCellValue(getString(data, "clientProviderName"));
            dataRow.createCell(colNum++).setCellValue(product);
            dataRow.createCell(colNum++).setCellValue(getString(data, "billable"));
            dataRow.createCell(colNum++).setCellValue(maskedIdentificationNo);
            dataRow.createCell(colNum++).setCellValue(getString(data, "customerName").toUpperCase());
            dataRow.createCell(colNum++).setCellValue(getString(data, "legalName"));
            dataRow.createCell(colNum++).setCellValue(getFormattedDate(data, "created_at"));
            dataRow.createCell(colNum++).setCellValue(getFormattedDate(data, "merchantRequestAt"));
            dataRow.createCell(colNum++).setCellValue(getFormattedDate(data, "merchantResponseAt"));
            dataRow.createCell(colNum++).setCellValue(getString(data, "trackId"));
            dataRow.createCell(colNum++).setCellValue(getString(data, "requestId"));
            //dataRow.createCell(colNum++).setCellValue(getString(data, "orderId"));
            dataRow.createCell(colNum++).setCellValue(getString(data, "responseMessage"));
            dataRow.createCell(colNum++).setCellValue(getString(data, "status"));

            for (int i = 0; i < headers.length; i++) {
                dataRow.getCell(i).setCellStyle(dataStyle);
            }
        }

        // Auto-size columns or set default
        for (int i = 0; i < headers.length; i++) {
            if (!GraphicsEnvironment.isHeadless()) {
                sheet.autoSizeColumn(i);
            } else {
                sheet.setColumnWidth(i, 20 * 256);
            }
        }

        // Filename timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSS");
        String timestamp = sdf.format(new Date());

        // Response headers
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=kycReport_" + timestamp + ".xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    
    
    
    
    
    private void generateMerchantExcelReport(HttpServletResponse response, ReportModel apiResponse) throws IOException {
    	log.info("generateMerchantExcelReport method started");
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("KYC Report");

        // Header style
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        String[] headers = {
            "Sr.No", "MID", "Merchant Name", "Product", "Billable",
            "Request_Date_Time", "Track_Id", "Response Message", "Status"
        };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Data style
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> dataList = (List<Map<String, Object>>) apiResponse.getData();

        int rowNum = 1;
        for (Map<String, Object> data : dataList) {
            Row dataRow = sheet.createRow(rowNum++);
            int colNum = 0;

            dataRow.createCell(colNum++).setCellValue(rowNum - 1); // Sr.No
            dataRow.createCell(colNum++).setCellValue(getString(data, "merchantId"));
            dataRow.createCell(colNum++).setCellValue(getString(data, "merchantName"));
            dataRow.createCell(colNum++).setCellValue(getString(data, "product"));
            dataRow.createCell(colNum++).setCellValue(getString(data, "billable"));
            dataRow.createCell(colNum++).setCellValue(getFormattedDate(data, "merchantRequestAt"));
            dataRow.createCell(colNum++).setCellValue(getString(data, "trackId"));
            //dataRow.createCell(colNum++).setCellValue(getString(data, "orderId"));
            dataRow.createCell(colNum++).setCellValue(getString(data, "responseMessage"));
            dataRow.createCell(colNum++).setCellValue(getString(data, "status"));

            for (int i = 0; i < headers.length; i++) {
                dataRow.getCell(i).setCellStyle(dataStyle);
            }
        }

        // Auto-size columns or fallback width
        for (int i = 0; i < headers.length; i++) {
            if (!GraphicsEnvironment.isHeadless()) {
                sheet.autoSizeColumn(i);
            } else {
                sheet.setColumnWidth(i, 20 * 256);
            }
        }

        String timestamp = new SimpleDateFormat("yyyyMMddHHmmssSS").format(new Date());

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=kycReport_" + timestamp + ".xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    // 🔹 Utility methods for null-safe extraction
    private String getString(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null ? val.toString() : "";
    }

    private String getFormattedDate(Map<String, Object> map, String key) {
        Object val = map.get(key);
        return val != null && !val.toString().isBlank() ? ConvertDateFormate(val.toString()) : "";
    }

    
    
    
    
    private String maskIdentificationNo(String identificationNo, String product) {
        int length = identificationNo.length();
        if ("OKYC".equalsIgnoreCase(product) && length == 12) {
            return "XXXXXXXX" + identificationNo.substring(8);
        } if ("PAN".equalsIgnoreCase(product) &&length == 10) {
            return identificationNo.substring(0, 5) + "XXXXX";
        }  if ("GSTIN".equalsIgnoreCase(product) &&length == 15) {
            return identificationNo.substring(0, 7) + "XXXXXXXX";
        } else {
            return identificationNo;
        }
    }
    
    
    /*Kyc Count Download Method*/
    
    @PostMapping(value = "/downloadKycCount")
    public void downloadKycCount(HttpServletResponse response,
            @RequestParam("mid") String mid,
            @RequestParam("dateRange") String dateRange,
            @RequestParam("product") String product) throws IOException
    {
    	
    	
    	String[] dates = dateRange.split(" to ");
        if (dates.length != 2) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid date range format. Expected format: 'dd-MM-yyyy to dd-MM-yyyy'");
            return;
        }
        String fromDate = dates[0].trim();
        String toDate = dates[1].trim();
        log.info("Request received - MID: {}, From Date: {}, To Date: {}, Product: {}", mid, fromDate, toDate, product);
        ApiResponseModel apiResponseModel=adminService.kycCountReport(fromDate, toDate, mid, product);
        log.info("The KycCount Response is"+apiResponseModel);
        generateKycCountExcelReport(response, apiResponseModel);
    }
    
    
    
    
    
    
    
    
    
    
    private void generateKycCountExcelReport(HttpServletResponse response, ApiResponseModel apiResponseModel) throws IOException {
    	log.info(" generateKycCountExcelReport method started");
    	 Workbook workbook = new XSSFWorkbook();
         Sheet sheet = workbook.createSheet("KYC Report");
         CellStyle headerStyle = workbook.createCellStyle();
         Font headerFont = workbook.createFont();
         headerFont.setBold(true);
         headerStyle.setFont(headerFont);
         headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
         headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
         headerStyle.setBorderBottom(BorderStyle.THIN);
         headerStyle.setBorderTop(BorderStyle.THIN);
         headerStyle.setBorderLeft(BorderStyle.THIN);
         headerStyle.setBorderRight(BorderStyle.THIN);
         Row headerRow = sheet.createRow(0);
         String[] headers = {
             "Sr.No", "MID","MERCHANT NAME", "OKYC", "PAN", "GSTIN","DRIVING_LICENSE","VOTER-ID","E-SIGN","TOTAL COUNT"
         };
         for (int i = 0; i < headers.length; i++) {
             Cell cell = headerRow.createCell(i);
             cell.setCellValue(headers[i]);
             cell.setCellStyle(headerStyle);
         }
         CellStyle dataStyle = workbook.createCellStyle();
         dataStyle.setBorderBottom(BorderStyle.THIN);
         dataStyle.setBorderTop(BorderStyle.THIN);
         dataStyle.setBorderLeft(BorderStyle.THIN);
         dataStyle.setBorderRight(BorderStyle.THIN);
         @SuppressWarnings("unchecked")
 		List<Map<String, Object>> dataList = (List<Map<String, Object>>) ((Map<String, Object>) apiResponseModel.getData()).get("data");
         int rowNum = 1;
         for (Map<String, Object> data : dataList) {
             Row dataRow = sheet.createRow(rowNum++);
             int colNum = 0;
             // Sr.No
             dataRow.createCell(colNum++).setCellValue(rowNum - 1);
             dataRow.createCell(colNum++).setCellValue(data.get("merchantId").toString());
             dataRow.createCell(colNum++).setCellValue(data.get("merchantName").toString());
             dataRow.createCell(colNum++).setCellValue(data.get("okycCount").toString());
             dataRow.createCell(colNum++).setCellValue(data.get("gstinCount").toString());        
             dataRow.createCell(colNum++).setCellValue(data.get("panCount").toString());
             dataRow.createCell(colNum++).setCellValue(data.get("driving_license").toString());        
             dataRow.createCell(colNum++).setCellValue(data.get("voter_id").toString());
             dataRow.createCell(colNum++).setCellValue(data.get("esign").toString());
             dataRow.createCell(colNum++).setCellValue(data.get("totalCount").toString());
             for (int i = 0; i < headers.length; i++) {
                 dataRow.getCell(i).setCellStyle(dataStyle);
             }
         }

         // Auto-size columns
         for (int i = 0; i < headers.length; i++) {
             if (!GraphicsEnvironment.isHeadless()) {
                 sheet.autoSizeColumn(i);
             } else {
                 // Fallback: set a default width (20 characters wide)
                 sheet.setColumnWidth(i, 20 * 256); // 256 = unit per character
             }
         }
         SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSS");
         String timestamp = sdf.format(new Date());
         response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
         response.setHeader("Content-Disposition", "attachment; filename=TxnCountReport_" + timestamp + ".xlsx");
         workbook.write(response.getOutputStream());
         workbook.close();
		
	}

	public String ConvertDateFormate(String Date)
    {
		log.info("ConvertDateFormate method started");
				 // Parse the input date string
    	 LocalDateTime dateTime = LocalDateTime.parse(Date);
         
         // Format to "08-05-2025 12:49:29"
         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
         return dateTime.format(formatter);
    }
}