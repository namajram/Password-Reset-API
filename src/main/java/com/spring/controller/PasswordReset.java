package com.spring.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spring.Entity.User;
import com.spring.dao.UserRepo;
import com.spring.dto.Reset;
import com.spring.service.UserService;

@RestController
@RequestMapping("/reset")
public class PasswordReset {

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepo userRepository;

	@PutMapping("/reset-otp")
	public ResponseEntity<String> regenerateOtp(@RequestParam String email) {
		return new ResponseEntity<>(userService.regenerateOtp(email), HttpStatus.OK);
	}
	@PutMapping("/updatepwd")
	public String resetPassword(@RequestBody Reset reset) {
		try {
			String email = reset.getEmail();
			User user = userRepository.findByEmail(email)
					.orElseThrow(() -> new RuntimeException("User not found with this email: " + email));
			if (user.getOtp().equals(reset.getOpt())) {
				user.setPassword(reset.getNewPassword());
				userRepository.save(user);
				return "Password Change SuccessFull";
			} else {
				return "Please Regenearted OTP   :  "  + email;
			}
		} catch (Exception e) {

			return "Email Not Found ! Please Enter Valid email  : " + reset.getEmail();
		}
	}
	@GetMapping("/email/{email}")
	public Optional<User> getEmail(@PathVariable String email) {
		return userRepository.findByEmail(email);
	}

}
