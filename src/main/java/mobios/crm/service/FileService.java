package mobios.crm.service;

import mobios.crm.dto.FileDto;

import java.util.List;

public interface FileService {
    long getFileCount();
    List<FileDto> getAllFileNames();
}
