package com.example.Tokkit_server;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.Tokkit_server.domain.Notification;
import com.example.Tokkit_server.domain.NotificationCategory;
import com.example.Tokkit_server.domain.User;
import com.example.Tokkit_server.repository.NotificationRepository;
import com.example.Tokkit_server.repository.UserRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class NotificationIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private NotificationRepository notificationRepository;

	@BeforeEach
	void setUp() {
		User user = userRepository.save(User.builder()
			.email("test@tokkit.com")
			.password("1234")
			.name("테스트유저")
			.phoneNumber("010-0000-0000")
			.simplePassword(111111)
			.build());

		notificationRepository.save(Notification.builder()
			.user(user)
			.title("결제 완료")
			.content("5,800원 결제 완료")
			.category(NotificationCategory.PAYMENT)
			.isDeleted(false)
			.build());
	}

	@Test
	void getNotifications() throws Exception {
		mockMvc.perform(get("/api/notifications")
				.param("userId", "1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.isSuccess").value(true))
			.andExpect(jsonPath("$.result[0].title").value("바우처가 발급되었습니다"));
	}

	@Test
	void deleteNotification() throws Exception {
		Long id = notificationRepository.findAll().get(0).getId();

		mockMvc.perform(delete("/api/notifications")
				.param("userId", "1")
				.param("notificationId", String.valueOf(id)))
			.andExpect(status().isOk());

		// ✅ 하드 삭제 확인: 더 이상 존재하지 않아야 함
		assertFalse(notificationRepository.findById(id).isPresent(), "알림이 DB에 남아있음 (삭제 실패)");
	}

}
