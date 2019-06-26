package com.whut.pan;

import com.whut.pan.domain.LinkSecret;
import com.whut.pan.service.impl.LinkSecretServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class PanApplication {
	@Autowired
	LinkSecretServiceImpl linkSecretService;
	public static void main(String[] args) {
		SpringApplication.run(PanApplication.class, args);
	}
	@RequestMapping("/test2")
	public void test2(){
//		LinkSecret linkSecret=new LinkSecret();
//		linkSecret.setLocalLink("zc");
//		Date date=new Date();
//		linkSecret.setExpireDate(date);
//		linkSecret.setSecret("1234");
//		linkSecret.setDownloadNum(1);
//		linkSecretService.save(linkSecret);
		LinkSecret linkSecret=linkSecretService.findLinkSecretByLink("zc");
		linkSecretService.addOneToDownloadNum(linkSecret);



//		linkSecretService.deleteLinkSecretByLink("zc");
	}

}
