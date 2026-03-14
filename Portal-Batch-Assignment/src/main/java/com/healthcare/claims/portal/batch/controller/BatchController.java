package com.healthcare.claims.portal.batch.controller;

import com.healthcare.claims.portal.batch.service.ExcelImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/batch")
@RequiredArgsConstructor
@Slf4j
public class BatchController {

    private final ExcelImportService excelImportService;
    private final JobExplorer jobExplorer;

    /**
     * Import members from an uploaded Excel file and launch a batch job.
     */
    @PostMapping(value = "/members/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> importMembers(@RequestParam("file") MultipartFile file) {
        log.info("Received Excel import request: fileName={}, size={}", file.getOriginalFilename(), file.getSize());

        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Uploaded file is empty"));
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null ||
                (!originalFilename.endsWith(".xlsx") && !originalFilename.endsWith(".xls"))) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Only .xlsx and .xls files are supported"));
        }

        try {
            Map<String, Object> result = excelImportService.importMembersFromExcel(file);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(result);
        } catch (Exception e) {
            log.error("Failed to import members from Excel", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Import failed: " + e.getMessage()));
        }
    }

    /**
     * List all batch job executions.
     */
    @GetMapping("/jobs")
    public ResponseEntity<List<Map<String, Object>>> listBatchJobs() {
        List<String> jobNames = jobExplorer.getJobNames();
        List<Map<String, Object>> jobs = new ArrayList<>();

        for (String jobName : jobNames) {
            Set<JobExecution> executions = jobExplorer.findRunningJobExecutions(jobName);
            List<JobExecution> allExecutions = new ArrayList<>(executions);

            // Also include completed executions
            jobExplorer.getJobInstances(jobName, 0, 100).forEach(instance -> {
                List<JobExecution> instanceExecutions = jobExplorer.getJobExecutions(instance);
                allExecutions.addAll(instanceExecutions);
            });

            for (JobExecution execution : allExecutions) {
                Map<String, Object> jobInfo = buildJobInfo(execution);
                jobs.add(jobInfo);
            }
        }

        // Deduplicate by execution id and sort by start time descending
        List<Map<String, Object>> uniqueJobs = jobs.stream()
                .collect(Collectors.toMap(
                        m -> m.get("executionId"),
                        m -> m,
                        (a, b) -> a))
                .values()
                .stream()
                .sorted((a, b) -> Long.compare(
                        (Long) b.getOrDefault("startTime", 0L),
                        (Long) a.getOrDefault("startTime", 0L)))
                .collect(Collectors.toList());

        return ResponseEntity.ok(uniqueJobs);
    }

    /**
     * Get the status of a specific batch job execution.
     */
    @GetMapping("/jobs/{id}/status")
    public ResponseEntity<Map<String, Object>> getJobStatus(@PathVariable("id") Long id) {
        JobExecution execution = jobExplorer.getJobExecution(id);
        if (execution == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(buildJobInfo(execution));
    }

    private Map<String, Object> buildJobInfo(JobExecution execution) {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("executionId", execution.getId());
        info.put("jobName", execution.getJobInstance().getJobName());
        info.put("status", execution.getStatus().name());
        info.put("startTime", execution.getStartTime() != null
                ? execution.getStartTime().toString() : null);
        info.put("endTime", execution.getEndTime() != null
                ? execution.getEndTime().toString() : null);
        info.put("exitCode", execution.getExitStatus().getExitCode());
        info.put("exitDescription", execution.getExitStatus().getExitDescription());
        return info;
    }
}
