package mobios.crm.service;

import mobios.crm.dto.NicDto;
import mobios.crm.entity.Nic;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface NicService {

    boolean  saveCsv(MultipartFile[] files);

    List<Nic> getAllNicDetails();

    int getNicCount();
    long getMaleCount();
    long getfemaleCount();
    List<NicDto> getNicsByFileName(String fileName);
    byte [] generatePdf(String fileName);
     byte[] generateCsv(String fileName) ;
    byte[] generateXlsx(String fileName);

}
