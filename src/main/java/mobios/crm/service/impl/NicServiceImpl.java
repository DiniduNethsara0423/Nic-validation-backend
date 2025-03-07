package mobios.crm.service.impl;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import lombok.RequiredArgsConstructor;
import mobios.crm.entity.Nic;
import mobios.crm.repository.NicRepository;
import mobios.crm.service.NicService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NicServiceImpl implements NicService {

    final NicRepository repository;

    final ModelMapper  mapper;


    @Override
    public boolean saveCsv(MultipartFile[] files) {
        for (MultipartFile file:files){

            try {
                CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()));

                List<String[]> csvFiles= csvReader.readAll();

                for (String[] row:csvFiles){

                    for (String column:row){

                        if(!(column.isEmpty())){
                            Nic nic = validateNic(column);
                            if (null != nic) {
                                repository.save(nic);
                            }
                        }

                    }
                }


            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (CsvException e) {
                throw new RuntimeException(e);
            }

        }

        return false;
    }

    @Override
    public List<Nic> getAllNicDetails() {
        return repository.findAll();
    }


    private Nic validateNic(String nic) {
        Nic entity = new Nic();
        String gender = "MALE";
        int birthYear, dayValue;

        if (nic.matches("\\d{12}")) {
            // Handle new NIC format (12 digits)
            birthYear = Integer.parseInt(nic.substring(0, 4));
            dayValue = Integer.parseInt(nic.substring(4, 7));
        } else if (nic.matches("\\d{9}[VXvx]")) {
            // Handle old NIC format (9 digits + V/X)
            birthYear = 1900 + Integer.parseInt(nic.substring(0, 2));
            dayValue = Integer.parseInt(nic.substring(2, 5));
        } else {
            return null; // Invalid NIC
        }

        // Determine gender and adjust day value
        if (dayValue > 500) {
            gender = "FEMALE";
            dayValue -= 500;
        }

        // Calculate birth date
        int month = 0, day = 0;
        int[] daysInMonth = {0, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int daysAccumulated = 0;
        for (int i = 1; i <= 12; i++) {
            if (dayValue <= daysAccumulated + daysInMonth[i]) {
                month = i;
                day = dayValue - daysAccumulated;
                break;
            }
            daysAccumulated += daysInMonth[i];
        }

        // Adjust for leap years
        boolean isLeapYear = (birthYear % 4 == 0 && birthYear % 100 != 0) || (birthYear % 400 == 0);
        if (month == 2 && day == 29 && !isLeapYear) {
            month = 3;
            day = 1;
        }

        // Create date object
        LocalDate birthDate = LocalDate.of(birthYear, month, day);
        int age = Period.between(birthDate, LocalDate.now()).getYears();

        // Set entity properties
        entity.setAge(age);
        entity.setBirthday(birthDate.toString());
        entity.setGender(gender);
        entity.setNicNumber(nic);
        return entity;
    }



}




