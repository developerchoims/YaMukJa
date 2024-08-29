package backend.yamukja.csv;

import backend.yamukja.csv.controller.CsvController;
import backend.yamukja.csv.service.CsvService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CsvController.class)
public class CsvControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CsvService csvService;

    @Test
    @DisplayName("CsvController의 uploadCsv 메소드 테스트")
    public void uploadCsvTest() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.csv",
                "text/csv",
                "header1,header2\nvalue1,value2".getBytes());

        doNothing().when(csvService).completeCsv(any(String.class), any(MockMultipartFile.class));

        // when, then
        mvc.perform(multipart("/csv/upload")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk());
    }
}
