package com.epp.demoapp;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Controller
@RequestMapping("/cloud")
public class UploadingController {

    @Autowired
    private AmazonS3 s3client;
    @Value("${jsa.s3.bucket}")
    private String bucketName;

    @RequestMapping("/")
    public String uploading() {
        return "uploading";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadingPost(@RequestParam("multipartFile") MultipartFile multipartFile, RedirectAttributes redirectAttributes) throws IOException {
        String fileName = multipartFile.getOriginalFilename();

        try {
            //creating the file in the server (temporarily)
            File file = new File(fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(multipartFile.getBytes());
            fos.close();
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, file);
            PutObjectResult putObjectResult = s3client.putObject(putObjectRequest);
            file.delete();
            System.out.println(putObjectResult.getVersionId());
            System.out.println(putObjectResult.getMetadata().toString());
        } catch (IOException | AmazonServiceException ex) {
            ex.printStackTrace();
        }
        return "redirect:/";
    }

    @RequestMapping(value = "/download/{keyname}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public byte[] printBIll(@PathVariable("keyname") String keyname, HttpServletResponse response) throws Exception {
        keyname = keyname + ".PNG";
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, keyname);
        S3Object s3Object = s3client.getObject(getObjectRequest);
        S3ObjectInputStream s3is = s3Object.getObjectContent();
        FileOutputStream fos = new FileOutputStream(new File(keyname));

        byte[] read_buf = new byte[1024];
        int read_len = 0;
        while ((read_len = s3is.read(read_buf)) > 0) {
            fos.write(read_buf, 0, read_len);
        }
        response.setContentLength(read_buf.length);
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
//        response.getOutputStream().write(read_buf);
        s3is.close();
        fos.close();
        return read_buf;
    }
}