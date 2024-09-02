package backend.yamukja.place.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "sgg")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sgg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "do")
    private String doField;  // `do` is a reserved word, so use `doField` or a similar name.

    @Column(name = "sgg")
    private String sgg;

    @Column(name = "lon")
    private String lon;

    @Column(name = "lat")
    private String lat;
}