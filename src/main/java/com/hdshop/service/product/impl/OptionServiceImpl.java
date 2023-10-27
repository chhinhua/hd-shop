package com.hdshop.service.product.impl;

import com.hdshop.entity.product.Option;
import com.hdshop.entity.product.OptionValue;
import com.hdshop.entity.product.Product;
import com.hdshop.repository.product.OptionRepository;
import com.hdshop.repository.product.OptionValueRepository;
import com.hdshop.service.product.OptionService;
import com.hdshop.service.product.OptionValueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OptionServiceImpl implements OptionService {
    private final OptionRepository optionRepository;
    private final OptionValueService optionValueService;
    private final OptionValueRepository optionValueRepository;

    @Override
    public List<Option> addOptions(Long productId, List<Option> options) {
        return null;
    }

    /**
     * Lưu danh sách option ứng vs product vào database
     * @param productId
     * @param options
     * @return option list
     */
    @Override
    public List<Option> addOptionsByProductId(Long productId, List<Option> options) {
        List<Option> savedOptions = new ArrayList<>();
        for (Option option : options) {

            //check existing option
            Option existingOption = optionRepository
                    .findByOptionNameAndProduct_ProductId(option.getOptionName(), productId)
                    .orElse(option);

            for (OptionValue value : option.getValues()) {
                Optional<OptionValue> newOptionValue = optionValueService
                        .getByOptionNameAndProductId(value.getValueName(), productId);

                // Kiểm tra nếu đã tồn tại optionvalue thì thay đổi imageUrl mới
                // Nếu không tồn tại thì set option cho nó và lưu
                if (newOptionValue.isPresent()) {
                    newOptionValue.get().setImageUrl(value.getImageUrl());
                } else {
                    value.setOption(existingOption);
                    existingOption.getValues().add(optionValueRepository.save(value));
                }
            }
            optionRepository.flush();

            savedOptions.add(existingOption);
        }

        return savedOptions.stream().toList();
    }

    @Override
    public List<Option> saveOptionsFromProduct(Product product) {
        List<Option> savedOptions = new ArrayList<>();
        for (Option option : product.getOptions()) {

            //check existing option
            Option existingOption = optionRepository
                    .findByOptionNameAndProduct_ProductId(option.getOptionName(), product.getProductId())
                    .orElse(option);

            List<OptionValue> newOptionValues = new ArrayList<>();
            for (OptionValue value : existingOption.getValues()) {
                Optional<OptionValue> newOptionValue = optionValueService
                        .getByOptionNameAndProductId(value.getValueName(), product.getProductId());

                if (newOptionValue.isPresent()) {
                    newOptionValues.add(newOptionValue.get());
                } else {
                    newOptionValues.add(optionValueRepository.save(value));
                }
            }
            optionRepository.flush();

            existingOption.setValues(newOptionValues);
            savedOptions.add(optionRepository.save(existingOption));
        }
        return savedOptions.stream().toList();
    }
}
