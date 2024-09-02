package backend.yamukja.place.controller;

import backend.yamukja.place.model.Sgg;
import backend.yamukja.place.service.SggService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sgg")
public class SggController {
    private final SggService sggService;

    public SggController(SggService sggService) {
        this.sggService = sggService;
    }

    @GetMapping("/copy")
    public ResponseEntity<String> copyData() {
        sggService.copyData();
        return ResponseEntity.ok("sgg 데이터를 모두 가져왔습니다.");
    }

    @GetMapping
    public ResponseEntity<List<Sgg>> getAllSggData() {
        List<Sgg> sggData = sggService.getAllSggData();
        return ResponseEntity.ok(sggData);
    }


}
