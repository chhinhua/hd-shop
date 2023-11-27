package com.hdshop.controller;

import com.hdshop.service.image.ImageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@Tag(name = "Image upload")
@RestController
@RequestMapping("/api/v1/image")
public class ImageController {
    private final ImageService imageService;
    private final MessageSource messageSource;

    public ImageController(ImageService imageService, MessageSource messageSource) {
        this.imageService = imageService;
        this.messageSource = messageSource;
    }

    @PostMapping("/product/upload/{product_id}")
    public ResponseEntity<String> uploadProductImages(@PathVariable("product_id") Long product_id,
                                                      @RequestParam("images") List<MultipartFile> files) {
        imageService.uploadProudctImages(files, product_id);
        return ResponseEntity.ok(getMessage("image-upload-successfully"));
    }

    @PostMapping("/avatar/upload")
    public ResponseEntity<String> uploadAvatar(@RequestParam("image") MultipartFile file, Principal principal) {
        return ResponseEntity.ok(imageService.uploadAvatar(file, principal));
    }

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("image") MultipartFile file) {
        String image_url = (String) imageService.upload(file).get("secure_url");
        return ResponseEntity.ok(image_url);
    }

    // upload list of sku images
    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
