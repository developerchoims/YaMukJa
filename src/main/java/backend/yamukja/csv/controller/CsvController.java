package backend.yamukja.csv.controller;

import backend.yamukja.csv.service.CsvService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/csv")
public class CsvController {

    private final CsvService csvService;

    /**
     * @param file (csv file)
     * 다양한 csv 파일을 DB에 업로드
     */
    @PostMapping("/upload")
    public void uploadCsv(@RequestParam("file") MultipartFile file) throws Exception {
        // 파일 이름 추출 후 테이블 이름 생성 ( 파일 이름 = 테이블 이름)
        String fileName = Optional.ofNullable(file.getOriginalFilename()).orElseThrow(NullPointerException::new);
        String tableName = fileName.substring(0, fileName.lastIndexOf("."));
        csvService.completeCsv(tableName, file);
    }
}

