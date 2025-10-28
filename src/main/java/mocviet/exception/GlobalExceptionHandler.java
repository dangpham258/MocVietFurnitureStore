package mocviet.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", 
            "Kích thước file quá lớn! Vui lòng chọn file nhỏ hơn 2MB.");
        return "redirect:/manager/products";
    }
}
