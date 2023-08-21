package com.spring.service;


import com.spring.Entity.User;
import com.spring.dao.UserRepo;
import com.spring.dto.LoginDto;
import com.spring.dto.RegisterDto;
import com.spring.util.EmailUtil;
import com.spring.util.OtpUtil;

import java.time.Duration;
import java.time.LocalDateTime;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  @Autowired
  private OtpUtil otpUtil;
  @Autowired
  private EmailUtil emailUtil;
  @Autowired
  private UserRepo userRepository;

  // **** For user signUp *******
  
  public String register(RegisterDto registerDto) {
    String otp = otpUtil.generateOtp();
    
    try {
      emailUtil.sendOtpEmail(registerDto.getEmail(), otp);
    } catch (MessagingException e) {
      throw new RuntimeException("Unable to send otp please try again");
    }
    User user = new User();
    user.setName(registerDto.getName());
    user.setEmail(registerDto.getEmail());
    user.setPassword(registerDto.getPassword());
    user.setOtp(otp);
    user.setOtpGeneratedTime(LocalDateTime.now());
    userRepository.save(user);
    
    return "User registration successfull   :" + user.getName();
  }
// ***** Account Activate  ******  
  public String verifyAccount(String email, String otp) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("User not found with this email  : " + email));
    if (user.getOtp().equals(otp) && Duration.between(user.getOtpGeneratedTime(),
        LocalDateTime.now()).getSeconds() < (3 * 60)) {
      user.setActive(true);
      userRepository.save(user);
      return "OTP verified you can login";
    }
    return "Please regenerate otp and try again";
  }

  // **** Otp Regenerate *****
  public String regenerateOtp(String email)  {
   User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("User not found with this email  : " + email));
    String otp = otpUtil.generateOtp();
 try {
      emailUtil.sendOtpEmail(email, otp);
    } catch (MessagingException e) {
      throw new RuntimeException("Unable to send otp please try again");
    }
    user.setOtp(otp);
    user.setOtpGeneratedTime(LocalDateTime.now());
    userRepository.save(user);
    return "Email sent... please verify account within 3 minute";
  }
// ****** login user ******
  public String login(LoginDto loginDto) {
    User user = userRepository.findByEmail(loginDto.getEmail())
        .orElseThrow(
            () -> new RuntimeException("User not found with this email  : " + loginDto.getEmail()));
    if (!loginDto.getPassword().equals(user.getPassword())) {
      return "Password is incorrect";
    } else if (!user.isActive()) {
      return "your account is not verified";
    }
    return "Login successful";
  }
}
