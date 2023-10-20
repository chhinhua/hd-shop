package com.hdshop.service.product.impl;

import com.hdshop.entity.product.Option;
import com.hdshop.entity.product.Product;
import com.hdshop.repository.product.OptionRepository;
import com.hdshop.service.product.OptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OptionServiceImpl implements OptionService {
    private final OptionRepository optionRepository;

    /**
     * Lưu danh sách option ứng vs product vào database
     * @param product
     * @param options
     * @return option list
     */
    @Override
    public List<Option> addOptions(Product product, List<Option> options) {
        List<Option> savedOptions = options.stream()
                .peek(option -> option.setProduct(product))
                .map(optionRepository::save)
                .collect(Collectors.toList());

        return savedOptions;
    }
}
