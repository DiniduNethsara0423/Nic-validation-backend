package mobios.crm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobios.crm.service.NicService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/nic")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class NicController {

    final NicService service;

    @PostMapping
    public boolean saveCsv(@RequestParam("csv")MultipartFile[] files){
        service.saveCsv(files);
return true;
    }
}
