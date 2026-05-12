package org.app.backend.config;

import com.cloudinary.Cloudinary;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.app.backend.common.constants.CloudinaryProperties;
import org.app.backend.modules.notification.Notification;
import org.app.backend.modules.notification.dto.NotificationDTO;
import org.app.backend.modules.notification.enums.NotificationReadStatus;
import org.app.backend.modules.user.User;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationConfig {

  CloudinaryProperties cloudinaryProperties;

  @Bean
  OpenAPI openAPI() {
    return new OpenAPI()
        .servers(List.of(new Server().url("/").description("Current server")))
        .info(
            new Info()
                .title("Restful API server")
                .version("1.0.0")
                .contact(new Contact().name("Khanh Nguyen").email("knguyen1411b@gmail.com")))
        .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
        .components(
            new Components()
                .addSecuritySchemes(
                    "Bearer Authentication",
                    new SecurityScheme()
                        .name("Bearer Authentication")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
  }

  @Bean
  public Cloudinary cloudinary() {
    Map<String, String> config = new HashMap<>();
    config.put("cloud_name", cloudinaryProperties.getCloudName());
    config.put("api_key", cloudinaryProperties.getApiKey());
    config.put("api_secret", cloudinaryProperties.getApiSecret());
    return new Cloudinary(config);
  }

  @Bean
  ModelMapper modelMapper() {
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STANDARD);

    // Custom mapping for Notification -> NotificationDTO
    // Map user.id to userId
    Converter<User, UUID> userToIdConverter =
        ctx -> ctx.getSource() == null ? null : ctx.getSource().getId();
    Converter<NotificationReadStatus, String> enumToStringConverter =
        ctx -> ctx.getSource() == null ? null : ctx.getSource().name();

    modelMapper
        .typeMap(Notification.class, NotificationDTO.class)
        .addMappings(
            mapper ->
                mapper
                    .using(userToIdConverter)
                    .map(Notification::getUser, NotificationDTO::setUserId))
        .addMappings(
            mapper ->
                mapper
                    .using(enumToStringConverter)
                    .map(Notification::getReadStatus, NotificationDTO::setReadStatus));

    return modelMapper;
  }
}
