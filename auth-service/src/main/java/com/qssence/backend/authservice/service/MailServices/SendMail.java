package com.qssence.backend.authservice.service.MailServices;

import com.qssence.backend.authservice.entity.SmtpConfig;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class SendMail {

    @Autowired
    private MailConfiguration mailConfigurationService;

    @Autowired
    private MailService mailService;

    /**
     * Send a generic email.
     */
    public void sendEmail(String recipientEmail, String subject, String body) {
        try {
            SmtpConfig smtpConfig = mailService.getActiveMailConfig();
            JavaMailSenderImpl mailSender = createMailSender(smtpConfig);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(smtpConfig.getMailUsername());
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(body, true); // true = enable HTML

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    /**
     * Send a user-specific email with personalized content.
     */
    public void sendUserEmail(String recipientEmail, String firstName, String lastName, String password) {
        sendUserEmail(recipientEmail, firstName, lastName, password, null, null, null);
    }

    /**
     * Enhanced method: Send user creation email with groups, roles, and employeeId
     */
    public void sendUserEmail(String recipientEmail, String firstName, String lastName, 
                              String password, String employeeId, 
                              java.util.Set<String> groups, java.util.Set<String> roles) {
        try {
            SmtpConfig smtpConfig = mailService.getActiveMailConfig();
            JavaMailSenderImpl mailSender = createMailSender(smtpConfig);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            String subject = "Welcome to Venturing Digitally Pvt. Ltd. !";

            // Build groups list
            String groupsList = "";
            if (groups != null && !groups.isEmpty()) {
                groupsList = "<li><strong>Assigned Groups:</strong> " + String.join(", ", groups) + "</li>";
            } else {
                groupsList = "<li><strong>Assigned Groups:</strong> No groups assigned</li>";
            }

            // Build roles list
            String rolesList = "";
            if (roles != null && !roles.isEmpty()) {
                rolesList = "<li><strong>Assigned Roles:</strong> " + String.join(", ", roles) + "</li>";
            } else {
                rolesList = "<li><strong>Assigned Roles:</strong> No roles assigned</li>";
            }

            // Body with inline image reference (cid:logoImage)
            String body = "<!DOCTYPE html>"
                    + "<html>"
                    + "<head><style>"
                    + "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }"
                    + "ul { padding-left: 20px; }"
                    + "li { margin: 5px 0; }"
                    + "</style></head>"
                    + "<body>"
                    + "<div style='text-align: center;'>"
                    + "  <img src='cid:logoImage' alt='Company Logo' style='max-height:70px;'/>"
                    + "  <h2>Welcome to Venturing Digitally Pvt. Ltd.</h2>"
                    + "</div>"
                    + "<p>Hello <strong>" + firstName + " " + lastName + "</strong>,</p>"
                    + "<p>Your account has been created successfully. Please find your details below:</p>"
                    + "<ul>"
                    + "  <li><strong>Employee ID:</strong> " + (employeeId != null ? employeeId : "Not assigned") + "</li>"
                    + "  <li><strong>Username (Email):</strong> " + recipientEmail + "</li>"
                    + "  <li><strong>Temporary Password:</strong> " + password + "</li>"
                    + groupsList
                    + rolesList
                    + "</ul>"
                    + "<p><strong>Important:</strong> Please reset your password after login.</p>"
                    + "<p>Best regards,<br/>Venturing Digitally Pvt. Ltd.</p>"
                    + "</body>"
                    + "</html>";

            helper.setFrom(smtpConfig.getMailUsername());
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(body, true);

            // ✅ Attach logo as inline resource (local file path or resources folder)
            helper.addInline("logoImage", new ClassPathResource("static/images/company_logo.jpg"));

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send user email: " + e.getMessage(), e);
        }
    }

    /**
     * ✅ New Method: Send forgot password email with reset link.
     */
//    public void sendForgotPasswordEmail(String recipientEmail, String resetLink) {
//        String subject = "Password Reset Request - Venturing Digitally Pvt. Ltd.";
//        String body = "<!DOCTYPE html>"
//                + "<html><body>"
//                + "<p>Hello,</p>"
//                + "<p>We received a request to reset your password. Please click the link below to reset it:</p>"
//                + "<p><a href='" + resetLink + "' style='color:blue;font-weight:bold;'>Reset Password</a></p>"
//                + "<p>If you didn’t request this, you can safely ignore this email.</p>"
//                + "<p>Regards,<br/>Venturing Digitally Pvt. Ltd.</p>"
//                + "</body></html>";
//
//        sendEmail(recipientEmail, subject, body);
//    }

    public void sendForgotPasswordEmail(String recipientEmail, String resetLink, String token) {
        String subject = "Password Reset Request - Venturing Digitally Pvt. Ltd.";
        String body = "<!DOCTYPE html>"
                + "<html><body>"
                + "<p>Hello,</p>"
                + "<p>We received a request to reset your password.</p>"
                + "<p><strong>Your Reset Token:</strong> " + token + "</p>"
                + "<p>You can reset your password in two ways:</p>"
                + "<ul>"
                + " <li>Click this link: <a href='" + resetLink + "' style='color:blue;font-weight:bold;'>Reset Password</a></li>"
                + " <li>Or enter the above token manually in your reset form.</li>"
                + "</ul>"
                + "<p>If you didn’t request this, you can safely ignore this email.</p>"
                + "<p>Regards,<br/>Venturing Digitally Pvt. Ltd.</p>"
                + "</body></html>";

        sendEmail(recipientEmail, subject, body);
    }


    /**
     * ✅ New Method: Send confirmation after successful password reset.
     */
    public void sendPasswordResetSuccessEmail(String recipientEmail, String newPassword) {
        String subject = "Password Reset Successful - Venturing Digitally Pvt. Ltd.";
        String body = "<!DOCTYPE html>"
                + "<html><body>"
                + "<p>Hello,</p>"
                + "<p>Your password has been successfully reset.</p>"
                + "<p>You can now login with your new password.</p>"
                + "<p>Regards,<br/>Venturing Digitally Pvt. Ltd.</p>"
                + "</body></html>";

        sendEmail(recipientEmail, subject, body);
    }

    /**
     * Send email notification when user is assigned to groups
     */
    public void sendGroupAssignmentEmail(String recipientEmail, String firstName, String lastName, 
                                         java.util.Set<String> groupNames) {
        try {
            SmtpConfig smtpConfig = mailService.getActiveMailConfig();
            JavaMailSenderImpl mailSender = createMailSender(smtpConfig);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            String subject = "Group Assignment Notification - Venturing Digitally Pvt. Ltd.";

            String groupsHtml = "";
            if (groupNames != null && !groupNames.isEmpty()) {
                groupsHtml = "<ul>";
                for (String groupName : groupNames) {
                    groupsHtml += "<li>" + groupName + "</li>";
                }
                groupsHtml += "</ul>";
            } else {
                groupsHtml = "<p>No groups assigned.</p>";
            }

            String body = "<!DOCTYPE html>"
                    + "<html>"
                    + "<head><style>"
                    + "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }"
                    + "ul { padding-left: 20px; }"
                    + "li { margin: 5px 0; }"
                    + "</style></head>"
                    + "<body>"
                    + "<div style='text-align: center;'>"
                    + "  <img src='cid:logoImage' alt='Company Logo' style='max-height:70px;'/>"
                    + "</div>"
                    + "<p>Hello <strong>" + firstName + " " + lastName + "</strong>,</p>"
                    + "<p>You have been assigned to the following group(s):</p>"
                    + groupsHtml
                    + "<p>You will now have access to resources and permissions associated with these groups.</p>"
                    + "<p>If you have any questions, please contact your administrator.</p>"
                    + "<p>Best regards,<br/>Venturing Digitally Pvt. Ltd.</p>"
                    + "</body>"
                    + "</html>";

            helper.setFrom(smtpConfig.getMailUsername());
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(body, true);

            helper.addInline("logoImage", new ClassPathResource("static/images/company_logo.jpg"));

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send group assignment email: " + e.getMessage(), e);
        }
    }

    /**
     * Send email notification when user is assigned to roles
     */
    public void sendRoleAssignmentEmail(String recipientEmail, String firstName, String lastName, 
                                        java.util.Set<String> roleNames) {
        try {
            SmtpConfig smtpConfig = mailService.getActiveMailConfig();
            JavaMailSenderImpl mailSender = createMailSender(smtpConfig);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            String subject = "Role Assignment Notification - Venturing Digitally Pvt. Ltd.";

            String rolesHtml = "";
            if (roleNames != null && !roleNames.isEmpty()) {
                rolesHtml = "<ul>";
                for (String roleName : roleNames) {
                    rolesHtml += "<li>" + roleName + "</li>";
                }
                rolesHtml += "</ul>";
            } else {
                rolesHtml = "<p>No roles assigned.</p>";
            }

            String body = "<!DOCTYPE html>"
                    + "<html>"
                    + "<head><style>"
                    + "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }"
                    + "ul { padding-left: 20px; }"
                    + "li { margin: 5px 0; }"
                    + "</style></head>"
                    + "<body>"
                    + "<div style='text-align: center;'>"
                    + "  <img src='cid:logoImage' alt='Company Logo' style='max-height:70px;'/>"
                    + "</div>"
                    + "<p>Hello <strong>" + firstName + " " + lastName + "</strong>,</p>"
                    + "<p>You have been assigned the following role(s):</p>"
                    + rolesHtml
                    + "<p>You will now have access to permissions and capabilities associated with these roles.</p>"
                    + "<p>If you have any questions, please contact your administrator.</p>"
                    + "<p>Best regards,<br/>Venturing Digitally Pvt. Ltd.</p>"
                    + "</body>"
                    + "</html>";

            helper.setFrom(smtpConfig.getMailUsername());
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(body, true);

            helper.addInline("logoImage", new ClassPathResource("static/images/company_logo.jpg"));

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send role assignment email: " + e.getMessage(), e);
        }
    }

    /**
     * Send email notification when user is assigned to both groups and roles
     */
    public void sendGroupAndRoleAssignmentEmail(String recipientEmail, String firstName, String lastName, 
                                                 java.util.Set<String> groupNames, 
                                                 java.util.Set<String> roleNames) {
        try {
            SmtpConfig smtpConfig = mailService.getActiveMailConfig();
            JavaMailSenderImpl mailSender = createMailSender(smtpConfig);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            String subject = "Group & Role Assignment Notification - Venturing Digitally Pvt. Ltd.";

            String groupsHtml = "";
            if (groupNames != null && !groupNames.isEmpty()) {
                groupsHtml = "<ul>";
                for (String groupName : groupNames) {
                    groupsHtml += "<li>" + groupName + "</li>";
                }
                groupsHtml += "</ul>";
            } else {
                groupsHtml = "<p><em>No groups assigned.</em></p>";
            }

            String rolesHtml = "";
            if (roleNames != null && !roleNames.isEmpty()) {
                rolesHtml = "<ul>";
                for (String roleName : roleNames) {
                    rolesHtml += "<li>" + roleName + "</li>";
                }
                rolesHtml += "</ul>";
            } else {
                rolesHtml = "<p><em>No roles assigned.</em></p>";
            }

            String body = "<!DOCTYPE html>"
                    + "<html>"
                    + "<head><style>"
                    + "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }"
                    + "ul { padding-left: 20px; }"
                    + "li { margin: 5px 0; }"
                    + "h3 { color: #2c3e50; margin-top: 20px; }"
                    + "</style></head>"
                    + "<body>"
                    + "<div style='text-align: center;'>"
                    + "  <img src='cid:logoImage' alt='Company Logo' style='max-height:70px;'/>"
                    + "</div>"
                    + "<p>Hello <strong>" + firstName + " " + lastName + "</strong>,</p>"
                    + "<p>Your group and role assignments have been updated:</p>"
                    + "<h3>Assigned Groups:</h3>"
                    + groupsHtml
                    + "<h3>Assigned Roles:</h3>"
                    + rolesHtml
                    + "<p>You will now have access to resources, permissions, and capabilities associated with these groups and roles.</p>"
                    + "<p>If you have any questions, please contact your administrator.</p>"
                    + "<p>Best regards,<br/>Venturing Digitally Pvt. Ltd.</p>"
                    + "</body>"
                    + "</html>";

            helper.setFrom(smtpConfig.getMailUsername());
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(body, true);

            helper.addInline("logoImage", new ClassPathResource("static/images/company_logo.jpg"));

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send group and role assignment email: " + e.getMessage(), e);
        }
    }



    /**
     * Helper method to configure JavaMailSender using SMTP settings.
     */
    private JavaMailSenderImpl createMailSender(SmtpConfig smtpConfig) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(smtpConfig.getMailHost());
        mailSender.setPort(smtpConfig.getMailPort());
        mailSender.setUsername(smtpConfig.getMailUsername());
        mailSender.setPassword(smtpConfig.getMailPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", smtpConfig.getMailProtocol());
        props.put("mail.smtp.auth", String.valueOf(smtpConfig.isMailAuth())); // ✅ fixed
        props.put("mail.smtp.starttls.enable", String.valueOf(smtpConfig.isMailStarttlsEnable())); // ✅ fixed
        props.put("mail.smtp.starttls.required", "true"); // ✅ force STARTTLS
        props.put("mail.smtp.ssl.trust", smtpConfig.getMailHost()); // ✅ trust smtp host
        props.put("mail.debug", "true");

        return mailSender;
    }

}
