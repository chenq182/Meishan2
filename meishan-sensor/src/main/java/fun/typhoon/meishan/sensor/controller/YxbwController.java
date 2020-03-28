package fun.typhoon.meishan.sensor.controller;

import fun.typhoon.meishan.sensor.service.YxbwService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@RestController
@RequestMapping("yxbw")
public class YxbwController {

    @Value("${meishan.sensor.yxbw.upload.path}")
    String uploadFolder;
    @Value("${meishan.sensor.yxbw.result.path}")
    String resultFolder;

    @Autowired
    YxbwService yxbwService;

    @GetMapping("conf/{id}")
    public String conf(@PathVariable("id") Integer id, @RequestParam(name = "file") String file) {
        try {
            yxbwService.build(uploadFolder + file,
                    resultFolder + id + ".out",
                    resultFolder + id + ".csv");
        } catch (IOException e) {
            return "处理失败, " + e.getMessage();
        }
        return "文件 " + file + " 处理成功";
    }

    //@PostMapping("conf")
    public String conf2(@RequestParam("upfile") MultipartFile file,
                       @Nullable @RequestParam("accum") String[] checkbox) {
        if (file.isEmpty() || file.getOriginalFilename()==null)
            return "无上传文件";
        String fileName = file.getOriginalFilename();
        try {
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(uploadFolder+fileName));
            out.write(file.getBytes());
            out.flush();
            out.close();
        } catch (IOException e) {
            return "上传失败, " + e.getMessage();
        }

        boolean accum = checkbox != null && checkbox.length > 0;////

        try {
            yxbwService.build(uploadFolder + fileName,
                    resultFolder + "utf" + fileName,
                    resultFolder + "gbk" + fileName);
        } catch (IOException e) {
            return "处理失败, " + e.getMessage();
        }
        return "文件 " + fileName + " 上传成功";
    }
}
