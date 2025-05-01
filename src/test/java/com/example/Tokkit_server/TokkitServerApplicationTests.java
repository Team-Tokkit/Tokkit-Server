// package com.example.Tokkit_server;
//
// import static org.assertj.core.api.Assertions.*;
// import static org.junit.jupiter.api.Assertions.*;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
// import java.util.List;
// import java.util.Optional;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Nested;
// import org.junit.jupiter.api.Test;
//
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.http.MediaType;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.transaction.annotation.Transactional;
//
// import com.example.Tokkit_server.domain.Notification;
// import com.example.Tokkit_server.domain.NotificationCategory;
// import com.example.Tokkit_server.domain.User;
// import com.example.Tokkit_server.repository.NotificationRepository;
// import com.example.Tokkit_server.repository.UserRepository;
// import com.fasterxml.jackson.databind.ObjectMapper;
//
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @AutoConfigureMockMvc
// @Transactional
// class TokkitServerApplicationTests {
//
// 	@Autowired
// 	private MockMvc mockMvc;
//
// 	@Autowired
// 	private ObjectMapper objectMapper;
//
// 	@Autowired
// 	private UserRepository userRepository;
//
// 	@Autowired
// 	private NotificationRepository notificationRepository;
//
// 	@Autowired
// 	private NotificationSettingRepository settingRepository;
//
// 	private User testUser;
//
// 	@BeforeEach
// 	void setup() {
// 		testUser = userRepository.save(User.builder()
// 			.name("홍길동")
// 			.email("hong@test.com")
// 			.password("1234")
// 			.phoneNumber("010-0000-0000")
// 			.simplePassword(111111)
// 			.build());
//
// 		settingRepository.save(NotificationSetting.builder().user(testUser).build());
//
// 		notificationRepository.saveAll(List.of(
// 			Notification.builder()
// 				.user(testUser)
// 				.title("결제 완료")
// 				.content("결제가 완료되었습니다.")
// 				.category(NotificationCategory.PAYMENT)
// 				.isDeleted(false).build(),
// 			Notification.builder()
// 				.user(testUser)
// 				.title("바우처 발급")
// 				.content("바우처가 발급되었습니다.")
// 				.category(NotificationCategory.VOUCHER)
// 				.isDeleted(false).build()
// 		));
// 	}
//
// 	@Nested
// 	@DisplayName("[알림 조회 API]")
// 	class GetNotifications {
//
// 		@Test
// 		void 모든알림_조회_성공() throws Exception {
// 			mockMvc.perform(get("/api/notifications")
// 					.param("userId", String.valueOf(testUser.getId())))
// 				.andExpect(status().isOk())
// 				.andExpect(jsonPath("$.isSuccess").value(true))
// 				.andExpect(jsonPath("$.result.length()").value(2));
// 		}
//
// 		@Test
// 		void 카테고리별_조회_성공() throws Exception {
// 			mockMvc.perform(get("/api/notifications")
// 					.param("userId", String.valueOf(testUser.getId()))
// 					.param("category", "PAYMENT"))
// 				.andExpect(status().isOk())
// 				.andExpect(jsonPath("$.result[0].category").value("PAYMENT"));
// 		}
//
// 		@Test
// 		void 유저없을때_예외반환() throws Exception {
// 			mockMvc.perform(get("/api/notifications")
// 					.param("userId", "999999"))
// 				.andExpect(status().is4xxClientError())
// 				.andExpect(jsonPath("$.code").value("USER404"));
// 		}
// 	}
//
// 	@Nested
// 	@DisplayName("[알림 삭제 API]")
// 	class DeleteNotification {
//
// 		@Test
// 		@DisplayName("알림 삭제 성공")
// 		void 알림삭제_성공() throws Exception {
// 			// given
// 			User user = userRepository.saveAndFlush(User.builder().email("test@test.com").build());
// 			Notification notification = notificationRepository.saveAndFlush(Notification.builder()
// 				.user(user)
// 				.category(NotificationCategory.SYSTEM)
// 				.title("제목")
// 				.content("내용")
// 				.build());
//
// 			// when & then
// 			mockMvc.perform(delete("/api/notifications")
// 					.param("userId", String.valueOf(user.getId()))
// 					.param("notificationId", String.valueOf(notification.getId())))
// 				.andExpect(status().isOk());
//
// 			// 실제 삭제 확인
// 			Optional<Notification> deleted = notificationRepository.findById(notification.getId());
// 			assertThat(deleted).isEmpty();
// 		}
//
// 	}
//
// 	@Nested
// 	@DisplayName("[알림 설정 조회/수정 API]")
// 	class NotificationSettingTest {
//
// 		@Test
// 		void 설정조회_성공() throws Exception {
// 			mockMvc.perform(get("/api/notifications/setting")
// 					.param("userId", String.valueOf(testUser.getId())))
// 				.andExpect(status().isOk())
// 				.andExpect(jsonPath("$.result.system").value(true))
// 				.andExpect(jsonPath("$.result.payment").value(true));
// 		}
//
// 		@Test
// 		void 설정수정_성공() throws Exception {
// 			NotificationSettingDto dto = NotificationSettingDto.builder()
// 				.system(false).payment(false).voucher(true).token(true)
// 				.build();
//
// 			mockMvc.perform(put("/api/notifications/setting")
// 					.param("userId", String.valueOf(testUser.getId()))
// 					.contentType(MediaType.APPLICATION_JSON)
// 					.content(objectMapper.writeValueAsString(dto)))
// 				.andExpect(status().isOk());
//
// 			NotificationSetting updated = settingRepository.findByUser(testUser).get();
// 			assertThat(updated.isNotificationSystem()).isFalse();
// 			assertThat(updated.isNotificationPayment()).isFalse();
// 			assertThat(updated.isNotificationVoucher()).isTrue();
// 		}
//
// 		@Test
// 		void 설정조회_실패_없는유저() throws Exception {
// 			mockMvc.perform(get("/api/notifications/setting")
// 					.param("userId", "9999"))
// 				.andExpect(status().is4xxClientError())
// 				.andExpect(jsonPath("$.code").value("USER404"));
// 		}
// 	}
// }
