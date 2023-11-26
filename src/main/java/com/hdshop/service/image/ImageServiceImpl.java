package com.hdshop.service.image;

import com.cloudinary.Cloudinary;
import com.hdshop.entity.Product;
import com.hdshop.entity.User;
import com.hdshop.exception.InvalidException;
import com.hdshop.repository.ProductRepository;
import com.hdshop.repository.UserRepository;
import com.hdshop.service.product.ProductService;
import com.hdshop.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final ProductRepository productRepository;
    private final ProductService productService;
    private final MessageSource messageSource;
    private final UserService userService;
    private final Cloudinary cloudinary;
    private final UserRepository userRepository;

    @Override
    public Map upload(MultipartFile file) {
        try {
            Map<String, Object> options = new HashMap<>();
            options.put("folder", "duck_shop");

            Map data = this.cloudinary.uploader().upload(file.getBytes(), options);
            return data;
        } catch (IOException io) {
            throw new InvalidException(getMessage("image-upload-failed"));
        }
    }

    @Override
    public void uploadProudctImages(List<MultipartFile> files, Long product_id) {
        Product product = productService.findById(product_id);
        deleteListImages(product.getListImages());

        List<String> listImageUrl = new ArrayList<>();
        files.forEach(file -> {
            String imageUrl = (String) this.upload(file).get("secure_url");
            listImageUrl.add(imageUrl);
        });
        product.setListImages(listImageUrl);
        productRepository.save(product);
    }

    @Override
    public void deleteByPath(String image_url) {
        if (image_url != null) {
            try {
                String[] parts = image_url.split("/");
                String public_id = parts[parts.length - 1].split("\\.")[0];

                // Sử dụng public ID để xóa hình ảnh từ Cloudinary
                cloudinary.uploader().destroy(public_id, Map.of());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void uploadAvatar(MultipartFile file, Principal principal) {
        User user = userService.getUserByUsername(principal.getName());
        this.deleteByPath(user.getAvatarUrl());

        String imageUrl = (String) this.upload(file).get("secure_url");
        user.setAvatarUrl(imageUrl);
        userRepository.save(user);
    }

    public void deleteListImages(List<String> image_urls) {
        image_urls.forEach(url -> {
            this.deleteByPath(url);
        });
    }

    private String getMessage(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }
}
