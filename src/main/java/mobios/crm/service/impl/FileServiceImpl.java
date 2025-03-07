package mobios.crm.service.impl;

import lombok.RequiredArgsConstructor;
import mobios.crm.repository.FileRepository;
import mobios.crm.service.FileService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    final FileRepository repository;

    final ModelMapper mapper;

    @Override
    public long getFileCount() {
        return repository.count();
    }
}
