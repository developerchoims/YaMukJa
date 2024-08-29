package backend.yamukja.csv.service;

import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CsvService {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void completeCsv(String tableName, MultipartFile file) throws Exception {
        // try - resources : csv파일 읽는 중 오류 나면 close
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new RuntimeException("csv file이 비어있습니다.");
            }

            //header 추출 (column names)
            List<String> headers = parseHeaders(headerLine);
            //insert될 데이터
            List<List<String>> records = new ArrayList<>();
            //데이터 추출 및 도메인 길이 설정
            Map<String, Integer> maxLength = recordsAndLength(headers, reader, records);
            try {
                //테이블 생성
                createTable(tableName, headers, maxLength);
                //데이터 삽입
                insertData(headers,tableName, records);
            } catch (SQLException e) {
                throw new RuntimeException("sql 작업 중 오류가 일어났습니다.");
            }
        } catch (FileUploadException e) {
            throw new RuntimeException("파일을 업로드할 수 없습니다.");
        }
    }

    /**
     * @param headerLine - csv file에서 추출한 헤더라인
     * @return - 띄어쓰기는 '_'로 치환 후 List headers에 저장후 반환 (스네이크 케이스)
     */
    private List<String> parseHeaders(String headerLine) {
        // ',' 기준으로 split 후 배열로 치환
        String[] split = headerLine.split(",");

        // 스네이크 케이스로 list(headers)에 저장
        List<String> headers = new ArrayList<>();

        for (String header : split) {
            headers.add(header.trim().replace(" ", "_").toLowerCase());
        }

        return headers;
    }

    /**
     * @param tableName - csv file name = table name
     * @param headers - csv file에서 추출후 스네이크 케이스로 변환한 컬럼 name
     * @throws SQLException
     * CREATE TABLE TABLENAME (HEADER VARCHAR(00), HEADER VARCHAR(00))
     */
    private void createTable(String tableName, List<String> headers, Map<String, Integer> maxLength) throws SQLException {
        // 이미 테이블이 존재할 경우 삭제
        String dropTableSql = String.format("DROP TABLE IF EXISTS `%s`", tableName);
        jdbcTemplate.execute(dropTableSql);

        // 테이블 새로 생성
        StringJoiner columns = new StringJoiner(", ");
        for (String header : headers) {
            columns.add("`" + header + "` VARCHAR(" + maxLength.get(header) + ")");
        }

        String createTablesql = String.format("CREATE TABLE `%s` (%s)", tableName, columns);
        jdbcTemplate.execute(createTablesql);
    }

    /**
     * @param headers
     * @param reader  - csv file
     * @return maxLength - 해당 열에서 가장 큰 값 map(line, length)형태로 반환
     * @throws Exception
     */
    private Map<String, Integer> recordsAndLength(List<String> headers, BufferedReader reader, List<List<String>> records) throws Exception {
        Map<String, Integer> maxLength = new HashMap<>();

        // 컬럼의 최대값 길이를 0으로 초기화
        for (String header : headers) {
            maxLength.put(header, 0);
        }

        String line;
        while ((line = reader.readLine()) != null) {
            String[] values = line.split(",");
            List<String> record = new ArrayList<>();

            // 컬럼의 최대 길이 갱신, value값 없을 경우 "" 로 값 치환
            for (int i = 0; i < headers.size(); i++) {
                String header = headers.get(i);
                String value = values[i].isEmpty() ? "" : values[i];

                int currentLength = value.length();

                if (currentLength > maxLength.get(header)) {
                    maxLength.put(header, currentLength);
                }

                record.add(value);
            }

            records.add(record);
        }

        return maxLength;
    }

    /**
     * @param headers
     * @param tableName
     * @param records
     * @throws Exception INSERT INTO TABLENAME (COLUMN, COLUMN, COLUMN) VALUES (?, ?, ?)
     */
    private void insertData(List<String> headers, String tableName, List<List<String>> records) throws Exception {
        for (List<String> record : records) {
            StringJoiner columns = new StringJoiner(", ");
            StringJoiner marks = new StringJoiner(", ");
            List<Object> values = new ArrayList<>();

            // 컬럼 및 파라미터, 데이터 설정
            for (int i = 0; i < headers.size(); i++) {
                columns.add("`" + headers.get(i) + "`");
                marks.add("?");
                values.add(record.get(i));
            }

            // INSERT 쿼리 생성 및 실행
            String insertSql = String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columns, marks);
            jdbcTemplate.update(insertSql, values.toArray());
        }
    }
}

