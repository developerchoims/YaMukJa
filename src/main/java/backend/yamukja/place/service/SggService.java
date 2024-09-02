package backend.yamukja.place.service;

import backend.yamukja.place.model.Sgg;
import backend.yamukja.place.repository.SggRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SggService {

    private final JdbcTemplate jdbcTemplate;
    private final SggRepository sggRepository;

    public SggService(JdbcTemplate jdbcTemplate, SggRepository sggRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.sggRepository = sggRepository;
    }

    @Transactional
    public void copyData() {
        String sql = "INSERT INTO sgg (do, sgg, lon, lat) " +
                "SELECT `\uFEFFdo-si`, sgg, lon, lat FROM sgg_lat_lon";

        jdbcTemplate.update(sql);
    }

    public List<Sgg> getAllSggData() {
        return sggRepository.findAll();
    }

}
