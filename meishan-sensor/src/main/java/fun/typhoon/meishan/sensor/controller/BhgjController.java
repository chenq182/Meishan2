package fun.typhoon.meishan.sensor.controller;

import fun.typhoon.meishan.sensor.service.BhgjService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("bhgj")
public class BhgjController {

    @Value("${meishan.sensor.bhgj.upload.path}")
    String uploadFolder;
    @Value("${meishan.sensor.bhgj.result.path}")
    String resultFolder;

    @Autowired
    BhgjService bhgjService;

    @GetMapping("conf/{id}")
    public String conf(@PathVariable("id") Integer id,
                       @RequestParam(name = "file") String file,
                       @RequestParam(name = "hour") Integer hourMinCount,
                       @RequestParam(name = "day") Integer dayMinCount) {
        try {
            bhgjService.build(uploadFolder + file,
                    resultFolder + id + ".out",
                    resultFolder + id + ".csv",
                    hourMinCount, dayMinCount);
        } catch (IOException e) {
            return "处理失败, " + e.getMessage();
        }
        return "文件 " + file + " 处理成功";
    }
}
