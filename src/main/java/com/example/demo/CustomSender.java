package com.example.demo;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

@Service
public class CustomSender {

	private static final Logger log = LoggerFactory.getLogger(CustomSender.class);

	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Scheduled(fixedDelay = 10000L)
	public void sendMessage() {

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		log.info("Sending message...");
		ForkJoinPool myPool = new ForkJoinPool(10);
		myPool.submit(() -> {
		IntStream.range(1, 10).parallel().forEach(val -> {
			System.out.println("Starting " + Thread.currentThread().getName() + ", index=" + val + ", " + LocalDateTime.now());
			final CustomMessage message = new CustomMessage("Hello there!", val, false);
			rabbitTemplate.convertAndSend(QueueConfig.EXCHANGE_NAME, QueueConfig.ROUTING_KEY, message);
		});
		});

		stopWatch.stop();
		log.info(stopWatch.prettyPrint());
		log.info("Sending message... End");

	}
}
