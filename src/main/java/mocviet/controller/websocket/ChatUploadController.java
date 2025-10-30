package mocviet.controller.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatUploadController {

    // Theo quy ước hệ thống: lưu dưới /static/images/messages/
    private static final String SRC_DIR = "src/main/resources/static/images/messages/";
    private static final String TARGET_DIR = "target/classes/static/images/messages/";

    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("File trống");
            }
            String contentType = file.getContentType();
            if (contentType == null || !contentType.toLowerCase().startsWith("image/")) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body("Chỉ hỗ trợ ảnh");
            }

            Path srcBase = Paths.get(SRC_DIR, "temp");
            Path targetBase = Paths.get(TARGET_DIR, "temp");
            Files.createDirectories(srcBase);
            Files.createDirectories(targetBase);

            String originalName = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "image");
            String ext = "";
            int dot = originalName.lastIndexOf('.');
            if (dot >= 0 && dot < originalName.length() - 1) {
                ext = originalName.substring(dot);
            }
            String filename = UUID.randomUUID() + ext;
            Path srcPath = srcBase.resolve(filename);
            Path targetPath = targetBase.resolve(filename);
            Files.copy(file.getInputStream(), srcPath);
            Files.copy(file.getInputStream(), targetPath);

            Map<String, Object> body = new HashMap<>();
            body.put("url", "/static/images/messages/temp/" + filename);
            return ResponseEntity.ok(body);
        } catch (IOException e) {
            log.error("Lỗi upload ảnh", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi server");
        }
    }

    // Không cần endpoint serve ảnh vì đã phục vụ qua static /static/**
}


