package com.qssence.backend.authservice.service;

import java.util.List;
import java.util.Set;

import com.qssence.backend.authservice.dbo.UserMaster;
import com.qssence.backend.authservice.dto.UserMasterDto;
import com.qssence.backend.authservice.dto.UserRoleDto;
import com.qssence.backend.authservice.dto.responce.GroupResponseDto;
import com.qssence.backend.authservice.service.MailServices.MailConfiguration;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

public interface UserMasterService {


	public UserMasterDto createUserDetails(UserMasterDto userMasterDto) throws MessagingException;
	UserMasterDto findUserDetailsById(Long id);
	public List<UserMasterDto> getAllUserDetails();
	public UserMasterDto getUserDetailsById(Long id);
	public UserMasterDto updateUserDetails(UserMasterDto userMasterDto, Long id);
	void deleteUserMasterData(Long id);
	List<UserMasterDto> getUsersByIds(List<Long> userIds);


}
