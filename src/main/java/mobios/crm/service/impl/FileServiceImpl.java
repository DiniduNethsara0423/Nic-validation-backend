package mobios.crm.service.impl;

import lombok.RequiredArgsConstructor;
import mobios.crm.dto.FileDto;
import mobios.crm.entity.File;
import mobios.crm.repository.FileRepository;
import mobios.crm.service.FileService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    final FileRepository repository;

    final ModelMapper mapper;

    @Override
    public long getFileCount() {
        return repository.count();
    }

    @Override
    public List<FileDto> getAllFileNames() {
        List<FileDto> fileDtos=new ArrayList<>();
        Iterable<File> all = repository.findAll();
        Iterator<File> iterator= all.iterator();
        while (iterator.hasNext()){
            File next=iterator.next();
            FileDto map = mapper.map(next, FileDto.class);
            fileDtos.add(map);
        }
        return fileDtos;
    }
}
