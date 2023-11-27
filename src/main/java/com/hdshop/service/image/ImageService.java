package com.hdshop.service.image;

import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Map;

public interface ImageService {
    Map upload(final MultipartFile file);

    void uploadProudctImages(final List<MultipartFile> files, final Long product_id);

    void deleteByPath(final String image_url);

    String uploadAvatar(final MultipartFile file, final Principal principal);
}
