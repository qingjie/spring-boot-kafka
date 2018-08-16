package com.qingjie;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qingjie.model.ImageResizeRequest;

//@ComponentScan(basePackages = { "com.qingjie.config", "com.qingjie.consumer", "com.qingjie.controller", "com.qingjie.model" })

@SpringBootApplication
public class AsyncKafkaConsumerApplication {

	private final static Logger LOGGER = LoggerFactory.getLogger(AsyncKafkaConsumerApplication.class);

	public static void main(String... args) {
		new SpringApplicationBuilder(AsyncKafkaConsumerApplication.class).web(WebApplicationType.NONE).build()
				.run(args);
	}

	@KafkaListener(id = "server", topics = "asyncRequests")
	@SendTo
	public String listen(String input) {
		try {
			ImageResizeRequest imageResizeRequest = objectMapper().readValue(input, ImageResizeRequest.class);
			File imageFile = new File(imageResizeRequest.getInputFile());
			System.out.println("====The file path is ====: "+imageFile.getPath());
			String[] nameParts = imageFile.getName().split("/.");
			System.out.println("=======nameParts========"+nameParts[0]);
			BufferedImage image = ImageIO.read(imageFile);
			ImageIO.write(resize(image, imageResizeRequest.getWidth(), imageResizeRequest.getHeight()), "png", new File(imageFile.getParent(), imageResizeRequest.getWidth() + "x" + imageResizeRequest.getHeight() + "-" + nameParts[0] + ".png"));
		} catch (IOException e) {
			LOGGER.error("Error while processing input {}", input, e);
		}
		return input;
	}

	@Bean
	public NewTopic asyncRequests() {
		return new NewTopic("asyncRequests", 10, (short) 2);
	}

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}

	public static BufferedImage resize(BufferedImage img, int newW, int newH) {
		Image temporaryImage = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		BufferedImage newImage = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = newImage.createGraphics();
		g2d.drawImage(temporaryImage, 0, 0, null);
		g2d.dispose();

		return newImage;
	}
}