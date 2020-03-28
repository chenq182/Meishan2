package fun.typhoon.meishan.sensor.controller;

import fun.typhoon.meishan.sensor.service.GpxxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("gpxx")
public class GpxxController {

    @Value("${meishan.sensor.gpxx.upload.path}")
    String uploadFolder;
    @Value("${meishan.sensor.gpxx.result.path}")
    String resultFolder;

    @Autowired
    GpxxService gpxxService;

    @GetMapping("conf/{id}")
    public String conf(@PathVariable("id") Integer id,
                       @RequestParam(name = "file") String file,
                       @RequestParam(name = "hour") Integer hourMinCount,
                       @RequestParam(name = "day") Integer dayMinCount) {
        try {
            gpxxService.build(uploadFolder + file,
                    resultFolder + id + ".out",
                    resultFolder + id + ".csv",
                    hourMinCount, dayMinCount);
        } catch (IOException e) {
            return "处理失败, " + e.getMessage();
        }
        return "文件 " + file + " 处理成功";
    }
}
