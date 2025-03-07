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


    private Nic validateNic(String column) {
        String bornYear;
        String birthDayOfTheYear;
        String gender = "MALE";

        if (column.matches("\\d{12}")) {

            // Extract year and day of the year from the NIC
            bornYear = column.substring(0, 4);
            birthDayOfTheYear = column.substring(4, 7);

            // Female block: If the day of the year is greater than 500, it's a female
            if (Integer.parseInt(birthDayOfTheYear) > 500) {
                gender = "FEMALE";

                // Adjust the day of the year for females (subtract 500)
                birthDayOfTheYear = String.format("%03d", Integer.parseInt(birthDayOfTheYear) - 500);
            }

            // Combine year and day of the year to form a full date (yyyyDDD format)
            String fullDate = bornYear + birthDayOfTheYear;

            // Parse the full date to LocalDate using the "yyyyDDD" pattern
            LocalDate date = LocalDate.parse(fullDate, DateTimeFormatter.ofPattern("yyyyDDD"));

            // Calculate the age based on the date of birth
            int age = Period.between(date, LocalDate.now()).getYears();

            // Create an instance of CsvDao using the constructor and set values manually
            Nic entity = new Nic();
            entity.setAge(Integer.parseInt(String.valueOf(age)));  // Convert int to String
            entity.setBirthday(String.valueOf(date));
            entity.setGender(gender);
            entity.setAge(Integer.parseInt(column));  // Assuming 'column' is the NIC string

            // Return the populated CsvDao object
            return entity;
        } else {
            return null;
        }
    }



}




