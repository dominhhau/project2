package com.nhom8.uploadController;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

@Controller
public class UploadController {

	@PostMapping("/upload")
	public String handleFileUpload(@RequestParam("file") MultipartFile file,
		RedirectAttributes redirectAttributes, HttpServletRequest request){

		//check the file
		if (file.isEmpty()) {
            System.out.println( "Please select a file to upload");
            return null;
        }
		
		//(access key , sceret key)
		BasicAWSCredentials creds = new BasicAWSCredentials("AKIAIQAOPLTOY275PQKQ", "2vOFqGRwTNTGIBqSfoN5Fy8xwynqYWmin0Q9ssSc"); 
		
		AmazonS3 s3client = AmazonS3Client.builder()
			    .withRegion("ap-southeast-1")
			    .withCredentials(new AWSStaticCredentialsProvider(creds))
			    .build();
		
		String bucketName = "nhom8-s3-bucket";
		try{

			InputStream is = file.getInputStream();
			
			//save on s3 with public read access
			s3client.putObject(new PutObjectRequest(bucketName, file.getOriginalFilename(), is ,new ObjectMetadata()).withCannedAcl(CannedAccessControlList.PublicRead));
			
			//get a object
			S3Object s3object = s3client.getObject(new GetObjectRequest(bucketName, file.getOriginalFilename()));		
			
			//add to model
			redirectAttributes.addAttribute("file",s3object.getObjectContent().getHttpRequest().getURI().toString());
			request.setAttribute("file", s3object.getObjectContent().getHttpRequest().getURI().toString());
			
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
			
		return "uploadfile";
	}
	
}
