//package com.qssence.backend.audittrailservice.serializer;
//
//import com.fasterxml.jackson.core.JsonGenerator;
//import com.fasterxml.jackson.databind.JsonSerializer;
//import com.fasterxml.jackson.databind.SerializerProvider;
//import com.qssence.backend.audittrailservice.entity.AuthLog;
//import com.qssence.backend.audittrailservice.entity.GroupLog;
//import com.qssence.backend.audittrailservice.entity.RoleLog;
//import com.qssence.backend.audittrailservice.entity.UserLog;
//import java.io.IOException;
//import java.time.format.DateTimeFormatter;
//
//public class LogSerializer extends JsonSerializer<Object> {
//
//    @Override
//    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
//        gen.writeStartObject();
//
//        if (value instanceof UserLog) {
//            UserLog userLog = (UserLog) value;
//            gen.writeStringField("message", userLog.getMessage());
//            gen.writeStringField("status", userLog.getStatus());
//            gen.writeStringField("timestamp", userLog.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
//            gen.writeStringField("ipAddress", userLog.getIpAddress());
//        } else if (value instanceof GroupLog) {
//            GroupLog groupLog = (GroupLog) value;
//            gen.writeStringField("message", groupLog.getMessage());
//            gen.writeStringField("status", groupLog.getStatus());
//            gen.writeStringField("timestamp", groupLog.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
//            gen.writeStringField("ipAddress", groupLog.getIpAddress());
//        } else if (value instanceof RoleLog) {
//            RoleLog roleLog = (RoleLog) value;
//            gen.writeStringField("message", roleLog.getMessage());
//            gen.writeStringField("status", roleLog.getStatus());
//            gen.writeStringField("timestamp", roleLog.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
//            gen.writeStringField("ipAddress", roleLog.getIpAddress());
//        } else if (value instanceof AuthLog) {
//            AuthLog authLog = (AuthLog) value;
//            gen.writeStringField("message", authLog.getMessage());
//            gen.writeStringField("status", authLog.getStatus());
//            gen.writeStringField("timestamp", authLog.getTimestamp().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
//            gen.writeStringField("ipAddress", authLog.getIpAddress());
//        }
//
//        gen.writeEndObject();
//    }
//}
