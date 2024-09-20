package com.amplifiers.pathfinder.entity.report;

import com.amplifiers.pathfinder.utility.Variables.PaginationSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;
    private final Integer numReportsPerPage = PaginationSettings.NUM_REPORTS_PER_PAGE;

    @GetMapping("/unresolved/all")
    public ResponseEntity<?> findAllUnresolvedReports(
            @RequestParam(name = "page", defaultValue = "0") Integer page
    ) {
        Pageable pageable = PageRequest.of(page, numReportsPerPage);
        return ResponseEntity.ok(reportService.findAllUnResolvedReports(pageable));
    }

    @GetMapping("/resolved/all")
    public ResponseEntity<?> findAllResolvedReports(
            @RequestParam(name = "page", defaultValue = "0") Integer page
    ) {
        Pageable pageable = PageRequest.of(page, numReportsPerPage);
        return ResponseEntity.ok(reportService.findAllResolvedReports(pageable));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createReport(
            @RequestBody ReportCreateRequest reportCreateRequest
    ) {
        return ResponseEntity.ok(reportService.createReport(reportCreateRequest));
    }

    @PostMapping("/resolve/{reportId}")
    public ResponseEntity<?> resolveReport(
            @PathVariable Integer reportId
    ) {
        return ResponseEntity.ok(reportService.resolveReport(reportId));
    }
}
