package mobios.crm.service;

import mobios.crm.dto.NicDto;
import mobios.crm.dto.NicProcessingResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface NicService {

    NicProcessingResponseDto saveCsv(MultipartFile[] files);

    List<NicDto> getAllNicDetails();

    int getNicCount();
    long getMaleCount();
    long getfemaleCount();
    List<NicDto> getNicsByFileName(String fileName);
    byte [] generatePdf(String fileName);
     byte[] generateCsv(String fileName) ;
    byte[] generateXlsx(String fileName);
    public List<NicDto> getAllMales();
    public List<NicDto> getAllFemales();


}
