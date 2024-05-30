package com.hdshop.dto.ghn;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties("code_message_value")
public class CreateGhnOrderResponse {
    private int code;
    private String codeMessageValue;
    private GhnOrderData data;
    private String message;
    private String messageDisplay;

    @Data
    public static class GhnOrderData {
        private String orderCode;
        private String sortCode;
        private String transType;
        private String wardEncode;
        private String districtEncode;
        private GhnOrderFee fee;
        private int totalFee;
        private String expectedDeliveryTime;
        private String operationPartner;

        // Inner class for nested "fee" object (optional)
        @Data
        public static class GhnOrderFee {
            private int mainService;
            private int insurance;
            private int codFee;
            private int stationDo;
            private int stationPu;
            private int returnFee;
            private int r2s;
            private int returnAgain;
            private int coupon;
            private int documentReturn;
            private int doubleCheck;
            private int doubleCheckDeliver;
            private int pickRemoteAreasFee;
            private int deliverRemoteAreasFee;
            private int pickRemoteAreasFeeReturn;
            private int deliverRemoteAreasFeeReturn;
            private int codFailedFee;
        }
    }
}
