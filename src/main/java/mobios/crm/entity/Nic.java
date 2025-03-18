package mobios.crm.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Nic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String nicNumber;
    private String birthday;
    private int age;
    private String gender;

    @ManyToOne()
    @JoinColumn(name = "file_id")
    private File file;


}
