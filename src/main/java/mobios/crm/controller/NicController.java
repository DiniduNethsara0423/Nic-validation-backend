package mobios.crm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobios.crm.dto.NicDto;
import mobios.crm.dto.NicProcessingResponseDto;
import mobios.crm.entity.Nic;
import mobios.crm.service.NicService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/nic")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class NicController {

    final NicService nicService;


    @PostMapping
    public ResponseEntity<NicProcessingResponseDto> saveCsv(@RequestParam("csv")MultipartFile[] files){
        NicProcessingResponseDto responseDto=nicService.saveCsv(files);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/get-all")
    public List<NicDto> getAll(){
        return nicService.getAllNicDetails();
    }

    @GetMapping("/nicCount")
    public ResponseEntity<Long> getTotalNicCount(){
        return ResponseEntity.ok((long) nicService.getNicCount());
    }

    @GetMapping("/getMaleCount")
    public ResponseEntity<Long> getMaleCount (){
        return ResponseEntity.ok((long) nicService.getMaleCount());
    }

    @GetMapping("/getFemaleCount")
    public ResponseEntity<Long> getFemaleCount (){
        return ResponseEntity.ok((long) nicService.getfemaleCount());
    }
    @GetMapping("/getByFileName/{fileName}")
    public List<NicDto> getNicsByFileName(@PathVariable("fileName") String fileName) {
        //List<Nic> nics = nicService.getNicsByFileName(fileName);
        return nicService.getNicsByFileName(fileName);
    }

    @GetMapping("/export/pdf/{fileName}")
    public ResponseEntity<byte[]> exportPdf(@PathVariable String fileName) {
        byte[] pdf = nicService.generatePdf(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=nics.pdf")
                .body(pdf);
    }

    @GetMapping("/export/csv/{fileName}")
    public ResponseEntity<byte[]> exportCsv(@PathVariable String fileName) {
        byte[] csv = nicService.generateCsv(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=nics.csv")
                .body(csv);
    }

    @GetMapping("/export/xlsx/{fileName}")
    public ResponseEntity<byte[]> exportXlsx(@PathVariable String fileName) {
        byte[] csv = nicService.generateXlsx(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=nics.xlsx")
                .body(csv);
    }
    @GetMapping("/allMale")
    public List<NicDto> getAllMales() {
        return nicService.getAllMales();
    }
    @GetMapping("/allFemale")
    public List<NicDto> getAllFemales() {
        return nicService.getAllFemales();
    }


}
