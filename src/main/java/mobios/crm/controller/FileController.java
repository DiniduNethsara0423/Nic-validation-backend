package mobios.crm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mobios.crm.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/file")
@Slf4j
@CrossOrigin
public class FileController {
    final FileService service;

    @GetMapping("/fileCount")
    public ResponseEntity<Long> getFileCount(){
        return ResponseEntity.ok((long) service.getFileCount());
    }

}
