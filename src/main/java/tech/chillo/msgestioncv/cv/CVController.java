package tech.chillo.msgestioncv.cv;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(value = "cv")
public class CVController {
    CVService cvService;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public void generate() {
        this.cvService.generate();
    }
}
