package com.qingjie;

import java.awt.Dimension;
import java.io.File;
import java.io.FilenameFilter;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qingjie.model.ImageResizeRequest;

import reactor.core.publisher.Flux;

@Component
public class ScheduledImageResizeRequestSubmitter {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledImageResizeRequestSubmitter.class);

	private final ReplyingKafkaTemplate<String, String, String> template;
	private final ObjectMapper objectMapper;
	private final String imagesDirectory;

	public ScheduledImageResizeRequestSubmitter(ReplyingKafkaTemplate<String, String, String> template,
			ObjectMapper objectMapper, @Value("${images.input.directory}") String imagesInputDirectory) {
		this.template = template;
		this.objectMapper = objectMapper;
		this.imagesDirectory = imagesInputDirectory;
	}

	
	
	public void scheduleTaskWithCronExpression() {
		System.out.println("------------------------start----------------");
		
		File[] files = new File(imagesDirectory).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return !name.equals(".DS_Store");
			}
		});

		//File[] files = new File(imagesDirectory).listFiles((dir, name) -> !name.equals(".DS_Store"));
		System.out.println("=====" + files[0].getName() +"---");
		Flux.just(files).filter(File::isFile).subscribe(f -> {

			Flux.just(new Dimension(800, 600), new Dimension(180, 180), new Dimension(1200, 630)).subscribe(d -> {
				try {
					System.out.println("---0-------");
					ImageResizeRequest imageResizeRequest = new ImageResizeRequest((int) d.getWidth(),
							(int) d.getHeight(), f.getAbsolutePath());
					System.out.println("---1-------" + imageResizeRequest);
					ProducerRecord<String, String> record = new ProducerRecord<>("asyncRequests",
							objectMapper.writeValueAsString(imageResizeRequest));

					record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, "asyncReplies".getBytes()));

					RequestReplyFuture<String, String, String> replyFuture = template.sendAndReceive(record);
					System.out.println("---4-------");
					ConsumerRecord<String, String> consumerRecord = replyFuture.get();
					System.out.println("---5-------");
				} catch (Exception e) {
					System.out.println("---6-------");
					LOGGER.error("Error while sending message", e);

				}
				System.out.println("---7-------");
			}, e -> LOGGER.error("Error while running lambda"),

					() -> f.renameTo(new File(f.getParent() + "/Done", f.getName())));

		});
	}

}
