package com.hdshop.service.product.impl;

import com.hdshop.entity.Option;
import com.hdshop.entity.OptionValue;
import com.hdshop.repository.OptionRepository;
import com.hdshop.repository.OptionValueRepository;
import com.hdshop.service.product.OptionService;
import com.hdshop.service.product.OptionValueService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OptionServiceImpl implements OptionService {
    OptionRepository optionRepository;
    OptionValueService valueService;
    OptionValueRepository valueRepository;

    /**
     * Lưu danh sách option ứng vs product vào database
     * @param productId
     * @param options
     * @return option list
     */
    @Override
    public List<Option> saveOrUpdateOptions(Long productId, List<Option> options) {
        List<Option> savedOptions = new ArrayList<>();
        for (Option option : options) {

            // check existing option
            Optional<Option> existingOption = optionRepository
                    .findByOptionNameAndProduct_ProductId(option.getOptionName(), productId);

            if (existingOption.isPresent()) {
                for (OptionValue value : option.getValues()) {
                    Optional<OptionValue> existingOptionValue = valueService
                            .findByValueNameAndProductId(value.getValueName(), productId);

                    // Kiểm tra nếu đã tồn tại optionvalue thì thay đổi imageUrl mới
                    // Nếu không tồn tại thì set option cho nó và set nó cho option
                    // Khi lưu option nó sẽ được lưu
                    if (existingOptionValue.isPresent()) {
                        existingOptionValue.get().setImageUrl(value.getImageUrl());
                    } else {
                        value.setOption(existingOption.get());
                        existingOption.get().getValues().add(valueRepository.save(value));
                    }
                }

                Option updateOption = optionRepository.saveAndFlush(existingOption.get());

                deleteUnusedValues(updateOption, option.getValues());

                savedOptions.add(updateOption);
            }
        }

        return savedOptions.stream().toList();
    }

    @Transactional
    protected void deleteUnusedValues(Option existingOption, List<OptionValue> newOptionValues) {
        List<OptionValue> existingOptionValues = existingOption.getValues();

        // Xác định các OptionValues không còn được sử dụng
        List<OptionValue> optionValuesToDelete = existingOptionValues.stream()
                .filter(optionValue -> !valueNameExists(optionValue, newOptionValues))
                .collect(Collectors.toList());

        // Xóa các OptionValues không còn được sử dụng khỏi cơ sở dữ liệu
        for (OptionValue optionValue : optionValuesToDelete) {
            existingOption.getValues().remove(optionValue);
            valueRepository.delete(optionValue);
        }
    }

    private boolean valueNameExists(OptionValue optionValue, List<OptionValue> newOptionValues) {
        String valueNameToMatch = optionValue.getValueName();
        return newOptionValues.stream()
                .anyMatch(newOptionValue -> newOptionValue.getValueName().equals(valueNameToMatch));
    }

}
