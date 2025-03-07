package mobios.crm.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;

    @OneToMany(mappedBy = "file",cascade = CascadeType.ALL,fetch=FetchType.LAZY)
    private List<Nic> nics;

}
