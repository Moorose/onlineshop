package net.thumbtack.onlineshop.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponse {

    private int totalPrise;
    private int totalCount;

    private List<ReportRow> reportRows;

    public ReportResponse(int totalPrise, int totalCount) {
        this.totalPrise = totalPrise;
        this.totalCount = totalCount;
    }
}
