package fun.typhoon.meishan.sensor.controller;

import fun.typhoon.meishan.sensor.service.YcyxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("ycyx")
public class YcyxController {

    @Value("${meishan.sensor.ycyx.upload.path}")
    String uploadFolder;
    @Value("${meishan.sensor.ycyx.result.path}")
    String resultFolder;

    @Autowired
    YcyxService ycyxService;

    @GetMapping("conf/{id}")
    public String conf(@PathVariable("id") Integer id,
                       @RequestParam(name = "file") String file,
                       @RequestParam(name = "hour") Integer hourMinCount,
                       @RequestParam(name = "day") Integer dayMinCount,
                       @RequestParam(name = "ratio") Double overLimit) {
        try {
            ycyxService.build(uploadFolder + file,
                    resultFolder + id + ".out",
                    resultFolder + id + ".csv",
                    hourMinCount, dayMinCount, overLimit);
        } catch (IOException e) {
            return "处理失败, " + e.getMessage();
        }
        return "文件 " + file + " 处理成功";
    }
}
