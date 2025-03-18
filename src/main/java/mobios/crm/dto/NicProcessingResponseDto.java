package mobios.crm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NicProcessingResponseDto {
    private int totalNicProcessed;
    private int duplicateCount;
    private int invalidCount;
    private int savedCount;

}
