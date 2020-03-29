package io.github.vananos.sosedi.controllers;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
public class SettingsControllerTest {
//
//    @Autowired
//    private MockMvc mvc;
//
//    @MockBean
//    private UserRepository userRepository;
//
//    @MockBean
//    private PasswordEncoder passwordEncoder;
//
//    @Test
//    public void ownerCanChangeOwnPasswordTest() throws Exception {
//        User user = getValidUser();
//
//        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
//        changePasswordRequest.setUserId(user.getId());
//        changePasswordRequest.setPassword("New_Password_1");
//
//        when(userRepository.findById(eq(user.getId()))).thenReturn(Optional.of(getValidUser()));
//        when(passwordEncoder.encode(any())).thenReturn("encoded_pass");
//        when(userRepository.save(any())).thenReturn(user);
//
//        mvc.perform(post("/settings/pincode")
//                .contentType(MediaType.APPLICATION_JSON)
//                .characterEncoding("utf-8")
//                .with(user(new UserDetailsImpl(user)))
//                .content(toJson(changePasswordRequest)))
//                .andExpect(status().isOk());
//
//        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
//
//        verify(userRepository, times(1)).save(argumentCaptor.capture());
//
//        assertThat(argumentCaptor.getValue().getPinCode()).isEqualTo("encoded_pass");
//    }
//
//
//    @Test
//    public void notOwnerCouldNotChangePasswordTest() throws Exception {
//        User user = getValidUser();
//
//        ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest();
//        changePasswordRequest.setUserId(user.getId() + 1);
//        changePasswordRequest.setPassword("New_Password_1");
//
//        mvc.perform(post("/settings/pincode")
//                .contentType(MediaType.APPLICATION_JSON)
//                .characterEncoding("utf-8")
//                .with(user(new UserDetailsImpl(user)))
//                .content(toJson(changePasswordRequest)))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    public void ownerCanChangeNotificationSettings() throws Exception {
//        User user = getValidUser();
//
//        ChangeNotificationSettingsRequest req = new ChangeNotificationSettingsRequest();
//        req.setUserId(user.getId());
//        req.setNotificationFrequency(NotificationFrequency.NEVER);
//
//        when(userRepository.findById(eq(user.getId()))).thenReturn(Optional.of(getValidUser()));
//        when(userRepository.save(any())).thenReturn(user);
//
//        mvc.perform(post("/settings/notifications")
//                .contentType(MediaType.APPLICATION_JSON)
//                .characterEncoding("utf-8")
//                .with(user(new UserDetailsImpl(user)))
//                .content(toJson(req)))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    public void notOwnerCanNotChangeNotificationSettings() throws Exception {
//        User user = getValidUser();
//
//        ChangeNotificationSettingsRequest req = new ChangeNotificationSettingsRequest();
//        req.setUserId(user.getId() + 1);
//        req.setNotificationFrequency(NotificationFrequency.NEVER);
//
//        mvc.perform(post("/settings/notifications")
//                .contentType(MediaType.APPLICATION_JSON)
//                .characterEncoding("utf-8")
//                .with(user(new UserDetailsImpl(user)))
//                .content(toJson(req)))
//                .andExpect(status().isForbidden());
//    }
}
