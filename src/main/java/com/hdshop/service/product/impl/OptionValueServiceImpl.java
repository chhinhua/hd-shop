package com.hdshop.service.product.impl;

import com.hdshop.dto.product.OptionDTO;
import com.hdshop.dto.product.OptionValueDTO;
import com.hdshop.entity.product.Option;
import com.hdshop.entity.product.OptionValue;
import com.hdshop.entity.product.OptionValueId;
import com.hdshop.entity.product.Product;
import com.hdshop.repository.product.OptionValueRepository;
import com.hdshop.service.product.OptionValueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OptionValueServiceImpl implements OptionValueService {
    private final OptionValueRepository optionValueRepository;

    @Override
    public List<OptionValue> addOptionValues(Product product, List<Option> options, List<OptionDTO> optionDTOs) {
        /*List<OptionValue> optionValues = new ArrayList<>();

        for (OptionDTO optionDTO : optionDTOs) {
            Option option = options.stream()
                    .filter(opt -> opt.getOptionId().equals(optionDTO.getOptionId()))
                    .findFirst()
                    .orElse(null);

            if (option != null) {
                for (OptionValueDTO optionValueDTO : optionDTO.getValues()) {
                    OptionValue optionValue = new OptionValue();

                    optionValue.setProductId(product.getId());
                    optionValue.setOptionId(option.getOptionId());
                    optionValue.setOption(option);
                    optionValue.setValueName(optionValueDTO.getValueName());
                    optionValue.setImageUrl(optionValueDTO.getImageUrl());

                    // Tiếp theo, tiến hành lưu optionValue vào cơ sở dữ liệu
                    optionValueRepository.save(optionValue);

                    // (Optional) Thêm optionValue vào danh sách để trả về
                    optionValues.add(optionValue);
                }
            }
        }

        return optionValues;*/
        return null;
    }
}
