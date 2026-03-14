package com.healthcare.claims.portal.batch.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExcelImportService {

    /**
     * Parse the uploaded Excel file and create batch member records.
     * Expected columns: MemberId, FirstName, LastName, DateOfBirth, PlanId, GroupId
     *
     * @param file the uploaded Excel file
     * @return a summary map containing import results
     */
    public Map<String, Object> importMembersFromExcel(MultipartFile file) throws Exception {
        List<Map<String, String>> members = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new IllegalArgumentException("Excel file contains no sheets");
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalArgumentException("Excel file has no header row");
            }

            // Build header index map
            Map<Integer, String> headerMap = new LinkedHashMap<>();
            for (Cell cell : headerRow) {
                headerMap.put(cell.getColumnIndex(), getCellValueAsString(cell).trim());
            }

            log.info("Excel headers detected: {}", headerMap.values());

            // Process data rows
            for (int rowIdx = 1; rowIdx <= sheet.getLastRowNum(); rowIdx++) {
                Row row = sheet.getRow(rowIdx);
                if (row == null) {
                    continue;
                }

                try {
                    Map<String, String> memberData = new LinkedHashMap<>();
                    for (Map.Entry<Integer, String> entry : headerMap.entrySet()) {
                        Cell cell = row.getCell(entry.getKey());
                        String value = cell != null ? getCellValueAsString(cell) : "";
                        memberData.put(entry.getValue(), value);
                    }

                    // Basic validation
                    if (memberData.getOrDefault("FirstName", "").isBlank()
                            && memberData.getOrDefault("LastName", "").isBlank()) {
                        log.debug("Skipping empty row at index {}", rowIdx);
                        continue;
                    }

                    members.add(memberData);
                } catch (Exception e) {
                    String errorMsg = String.format("Error processing row %d: %s", rowIdx + 1, e.getMessage());
                    log.warn(errorMsg, e);
                    errors.add(errorMsg);
                }
            }
        }

        log.info("Excel import completed: {} members parsed, {} errors", members.size(), errors.size());

        // Build result summary
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("fileName", file.getOriginalFilename());
        result.put("totalRecordsParsed", members.size());
        result.put("errorCount", errors.size());
        result.put("errors", errors);
        result.put("status", errors.isEmpty() ? "COMPLETED" : "COMPLETED_WITH_ERRORS");
        result.put("members", members);

        return result;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toLocalDate().toString();
                }
                double numVal = cell.getNumericCellValue();
                if (numVal == Math.floor(numVal) && !Double.isInfinite(numVal)) {
                    yield String.valueOf((long) numVal);
                }
                yield String.valueOf(numVal);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            case BLANK -> "";
            default -> "";
        };
    }
}
