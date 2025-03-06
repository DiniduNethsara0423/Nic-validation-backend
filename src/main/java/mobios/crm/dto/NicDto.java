package mobios.crm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NicDto {
    private Long id;
    private String nicNumber;
    private String birthday;
    private int age;
    private String gender;
    private FileDto fileDto;

}
