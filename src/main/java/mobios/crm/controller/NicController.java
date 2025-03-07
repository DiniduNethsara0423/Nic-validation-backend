package mobios.crm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobios.crm.entity.Nic;
import mobios.crm.service.NicService;
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
    public boolean saveCsv(@RequestParam("csv")MultipartFile[] files){
        return nicService.saveCsv(files);
    }

    @GetMapping("/get-all")
    public List<Nic> getAll(){
        return nicService.getAllNicDetails();
    }

    @GetMapping("/nicCount")
    public ResponseEntity<Long> getTotalNicCount(){
        return ResponseEntity.ok((long) nicService.getNicCount());
    }
}
