package com.hdshop.service.product.impl;

import com.hdshop.entity.Option;
import com.hdshop.entity.OptionValue;
import com.hdshop.entity.Product;
import com.hdshop.repository.OptionRepository;
import com.hdshop.repository.OptionValueRepository;
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

    /**
     * Lưu danh sách option ứng vs product vào database
     * @param productId
     * @param options
     * @return option list
     */
    @Override
    public List<Option> saveOrUpdateOptionsByProductId(Long productId, List<Option> options) {
        List<Option> savedOptions = new ArrayList<>();
        for (Option option : options) {

            //check existing option
            Option existingOption = optionRepository
                    .findByOptionNameAndProduct_ProductId(option.getOptionName(), productId)
                    .orElse(option);

            List<OptionValue> newListOptionValues = new ArrayList<>();
            for (OptionValue value : option.getValues()) {
                Optional<OptionValue> existingOptionValue = optionValueService
                        .getByValueNameAndProductId(value.getValueName(), productId);

                // Kiểm tra nếu đã tồn tại optionvalue thì thay đổi imageUrl mới
                // Nếu không tồn tại thì set option cho nó và set nó cho option
                // Khi lưu option nó sẽ được lưu
                if (existingOptionValue.isPresent()) {
                    existingOptionValue.get().setImageUrl(value.getImageUrl());
                    newListOptionValues.add(existingOptionValue.get());
                } else {
                    value.setOption(existingOption);
                    newListOptionValues.add(optionValueRepository.save(value));
                }
            }

            existingOption.setValues(newListOptionValues);
            savedOptions.add(optionRepository.save(existingOption));
        }

        return savedOptions.stream().toList();
    }
}
