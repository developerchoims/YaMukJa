package backend.yamukja.csv;

import backend.yamukja.csv.service.CsvService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@Import({CsvService.class})
public class CsvServiceTest {

    @InjectMocks
    public CsvService csvService;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("CsvService의 completeCsv 메소드 테스트 - 테스트 시에만 CsvService의 메소드 public으로 변경")
    public void completeCsvTest() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                "header1,header2\nvalue1,value2".getBytes());

        doNothing().when(jdbcTemplate).execute(anyString());
        when(jdbcTemplate.update(anyString(), any(Object[].class))).thenReturn(1);

        // when, then
        assertDoesNotThrow(() -> csvService.completeCsv("test", file));
    }

    @Test
    @DisplayName("CsvService의 createTable 메소드 테스트")
    public void createTableTest() throws Exception {
        // given
        Map<String, Integer> maxLength = new HashMap<>();
        maxLength.put("header1", 10);
        maxLength.put("header2", 20);

        List<String> headers = List.of("header1", "header2");

        doNothing().when(jdbcTemplate).execute(anyString());

        // when
//        csvService.createTable("test", headers, maxLength);

        // then
        verify(jdbcTemplate).execute(eq("DROP TABLE IF EXISTS test"));
        verify(jdbcTemplate).execute(eq("CREATE TABLE test (header1 VARCHAR(10), header2 VARCHAR(20))"));
    }

    @Test
    @DisplayName("CsvService의 insertData 메소드 테스트")
    public void insertDataTest() throws Exception {
        // given
        List<String> headers = List.of("header1", "header2");
        List<List<String>> records = List.of(
                List.of("value1", "value2"),
                List.of("value3", "value4")
        );

        when(jdbcTemplate.update(anyString(), any(Object[].class))).thenReturn(1);

        // when
//        csvService.insertData(headers, "test", records);

        // then
        verify(jdbcTemplate).update(eq("INSERT INTO test (header1, header2) VALUES (?, ?)"), eq(new Object[]{"value1", "value2"}));
        verify(jdbcTemplate).update(eq("INSERT INTO test (header1, header2) VALUES (?, ?)"), eq(new Object[]{"value3", "value4"}));
    }
}
