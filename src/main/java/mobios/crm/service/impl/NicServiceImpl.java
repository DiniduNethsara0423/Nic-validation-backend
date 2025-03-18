package mobios.crm.service.impl;


import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import lombok.RequiredArgsConstructor;
import mobios.crm.dto.NicDto;
import mobios.crm.dto.NicProcessingResponseDto;
import mobios.crm.entity.File;
import mobios.crm.entity.Nic;
import mobios.crm.repository.FileRepository;
import mobios.crm.repository.NicRepository;
import mobios.crm.service.NicService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@Service
@RequiredArgsConstructor
public class NicServiceImpl implements NicService {

    final NicRepository nicRepository;

    final FileRepository fileRepository;

    final ModelMapper mapper;


    @Override
    public NicProcessingResponseDto saveCsv(MultipartFile[] files) {
        int totalNicProcessed = 0;
        int duplicateCount = 0;
        int invalidCount = 0;
        int savedCount = 0;

        Set<String> invalidNicSet = new HashSet<>();

        for (MultipartFile file : files) {
            try {
                String fileName = file.getOriginalFilename();

                File existingFile = fileRepository.findByFileName(fileName)
                        .orElseGet(() -> {
                            File newFile = new File();
                            newFile.setFileName(fileName);
                            return fileRepository.save(newFile);
                        });

                CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()));
                List<String[]> csvFiles = csvReader.readAll();

                for (String[] row : csvFiles) {
                    for (String column : row) {
                        if (column == null || column.trim().isEmpty()) {
                            continue;
                        }

                        try {
                            Nic nic = validateNic(column);
                            totalNicProcessed++;

                            Optional<Nic> existingNic = nicRepository.findByNicNumber(nic.getNicNumber());
                            if (existingNic.isPresent()) {
                                duplicateCount++;
                                continue;
                            }

                            nic.setFile(existingFile);
                            nicRepository.save(nic);
                            savedCount++;

                        } catch (IllegalArgumentException e) {
                            if (invalidNicSet.add(column)) {
                                invalidCount++;
                            }
                            System.err.println("Invalid NIC found: " + column);
                        }
                    }
                }

            } catch (IOException | CsvException e) {
                throw new RuntimeException("Error processing CSV file: " + file.getOriginalFilename(), e);
            }
        }

        return new NicProcessingResponseDto(totalNicProcessed, duplicateCount, invalidCount, savedCount);
    }




    @Override
    public List<NicDto> getAllNicDetails() {
        List<NicDto> nicDtos=new ArrayList<>();
        Iterable<Nic> all = nicRepository.findAll();
        Iterator<Nic> iterator = all.iterator();
        while (iterator.hasNext()){
            Nic next = iterator.next();
            NicDto map = mapper.map(next, NicDto.class);
            nicDtos.add(map);
        }
        return nicDtos;

    }

    @Override
    public int getNicCount() {
        return (int) nicRepository.count();
    }

    @Override
    public long getMaleCount() {
        return nicRepository.countByGender("male");
    }

    @Override
    public long getfemaleCount() {
        return nicRepository.countByGender("female");
    }

    @Override
    public List<NicDto> getNicsByFileName(String fileName) {
        List<NicDto> nicDtos = new ArrayList<>();
        Iterable<Nic> nics = fileRepository.findByFileName(fileName).map(File::getNics).orElse(Collections.emptyList());
        Iterator<Nic> iterator = nics.iterator();
        while (iterator.hasNext()) {
            Nic next = iterator.next();
            NicDto map = mapper.map(next, NicDto.class);
            nicDtos.add(map);

        }
        return nicDtos;

    }

    @Override
    public byte[] generatePdf(String fileName) {
        List<NicDto> nicDtos = getNicsByFileName(fileName);

        try (PDDocument document = new PDDocument();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("NIC Report");
                contentStream.endText();

                float startY = 700;
                contentStream.setFont(PDType1Font.HELVETICA, 10);

                for (NicDto nicDto : nicDtos) {
                    if (startY < 50) {
                        contentStream.close();
                        page = new PDPage();
                        document.addPage(page);
                        contentStream.close();
                        try (PDPageContentStream newContentStream = new PDPageContentStream(document, page)) {
                            contentStream.setFont(PDType1Font.HELVETICA, 10);
                            startY = 750;
                        }
                    }

                    contentStream.beginText();
                    contentStream.newLineAtOffset(50, startY);
                    contentStream.showText(nicDto.getAge() + " | " +
                            nicDto.getBirthday() + " | " +
                            nicDto.getGender() + " | " +
                            (nicDto.getNicNumber() != null ? nicDto.getNicNumber() : "N/A"));
                    contentStream.endText();
                    startY -= 20;
                }
            }

            document.save(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }

    @Override
    public byte[] generateCsv(String fileName) {
        List<NicDto> nicDtos = getNicsByFileName(fileName);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(outputStream)) {
            writer.println("Age,Birthday,Gender,NIC Number");
            for (NicDto nic : nicDtos) {
                writer.println(nic.getAge() + "," +
                        nic.getBirthday() + "," +
                        nic.getGender() + "," +
                        (nic.getNicNumber() != null ? nic.getNicNumber() : "N/A"));
            }
            writer.flush();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating CSV", e);
        }
    }

    @Override
    public byte[] generateXlsx(String fileName) {
        List<NicDto> nicDtos = getNicsByFileName(fileName);
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("NIC Report");
            Row header = (Row) sheet.createRow(0);
            header.createCell(0).setCellValue("Age");
            header.createCell(1).setCellValue("Birthday");
            header.createCell(2).setCellValue("Gender");
            header.createCell(3).setCellValue("NIC Number");

            int rowIdx = 1;
            for (NicDto nic : nicDtos) {
                Row row = (Row) sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(nic.getAge());
                row.createCell(1).setCellValue(nic.getBirthday().toString());
                row.createCell(2).setCellValue(nic.getGender());
                row.createCell(3).setCellValue(nic.getNicNumber() != null ? nic.getNicNumber() : "N/A");
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating XLSX", e);
        }
    }

    @Override
    public List<NicDto> getAllMales() {
        List<NicDto> nicDtos = new ArrayList<>();
        Iterable<Nic> nics = nicRepository.findByGender("Male");

        Iterator<Nic> iterator = nics.iterator();
        while (iterator.hasNext()) {
            Nic next = iterator.next();
            NicDto mappedNic = mapper.map(next, NicDto.class);
            nicDtos.add(mappedNic);
        }
        return nicDtos;
    }

    @Override
    public List<NicDto> getAllFemales() {
        List<NicDto> nicDtos = new ArrayList<>();
        Iterable<Nic> nics = nicRepository.findByGender("Female");

        Iterator<Nic> iterator = nics.iterator();
        while (iterator.hasNext()) {
            Nic next = iterator.next();
            NicDto mappedNic = mapper.map(next, NicDto.class);
            nicDtos.add(mappedNic);
        }
        return nicDtos;
    }

    private Nic validateNic(String nic) {
        Nic entity = new Nic();
        String gender = "MALE";
        int birthYear, dayValue;

        if (nic.matches("\\d{12}")) {
            birthYear = Integer.parseInt(nic.substring(0, 4));
            dayValue = Integer.parseInt(nic.substring(4, 7));
        } else if (nic.matches("\\d{9}[VXvx]")) {
            birthYear = 1900 + Integer.parseInt(nic.substring(0, 2));
            dayValue = Integer.parseInt(nic.substring(2, 5));
        } else {
            throw new IllegalArgumentException("Invalid NIC format: " + nic);
        }

        // Adjust for gender
        if (dayValue > 500) {
            gender = "FEMALE";
            dayValue -= 500;
        }

        // Leap year
        boolean isLeapYear = (birthYear % 4 == 0 && birthYear % 100 != 0) || (birthYear % 400 == 0);
        int maxDays = isLeapYear ? 366 : 365;

        //  dayValue is in the valid range
        if (dayValue < 1 || dayValue > maxDays) {
            throw new IllegalArgumentException("Invalid day value in NIC: " + nic);
        }

        // Calculate month & day
        int month = 0, day = 0;
        int[] daysInMonth = {0, 31, (isLeapYear ? 29 : 28), 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int daysAccumulated = 0;

        for (int i = 1; i <= 12; i++) {
            if (dayValue <= daysAccumulated + daysInMonth[i]) {
                month = i;
                day = dayValue - daysAccumulated;
                break;
            }
            daysAccumulated += daysInMonth[i];
        }


        if (month == 0 || day == 0) {
            throw new IllegalArgumentException("Failed to determine birth date from NIC: " + nic);
        }

        //  LocalDate
        LocalDate birthDate = LocalDate.of(birthYear, month, day);
        int age = Period.between(birthDate, LocalDate.now()).getYears();


        entity.setAge(age);
        entity.setBirthday(birthDate.toString());
        entity.setGender(gender);
        entity.setNicNumber(nic);
        return entity;
    }


}




