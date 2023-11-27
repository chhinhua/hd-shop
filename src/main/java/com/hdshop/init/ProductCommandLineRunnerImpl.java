package com.hdshop.init;

import com.hdshop.repository.OrderRepository;
import com.hdshop.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ProductCommandLineRunnerImpl implements CommandLineRunner {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    public void run(String... args) throws Exception {
        //orderRepository.deleteAll();
    }
}
