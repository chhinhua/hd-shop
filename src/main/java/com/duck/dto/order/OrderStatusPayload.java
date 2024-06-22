package com.duck.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderStatusPayload {
    @JsonProperty("CODAmount")
    int codAmount;
    @JsonProperty("CODTransferDate")
    String codTransferDate;
    @JsonProperty("ClientOrderCode")
    String clientOrderCode;
    @JsonProperty("ConvertedWeight")
    int convertedWeight;
    @JsonProperty("Description")
    String description;
    @JsonProperty("Fee")
    Fee fee;
    @JsonProperty("Height")
    int height;
    @JsonProperty("IsPartialReturn")
    boolean isPartialReturn;
    @JsonProperty("Length")
    int length;
    @JsonProperty("OrderCode")
    String orderCode;
    @JsonProperty("PartialReturnCode")
    String partialReturnCode;
    @JsonProperty("PaymentType")
    int paymentType;
    @JsonProperty("Reason")
    String reason;
    @JsonProperty("ReasonCode")
    String reasonCode;
    @JsonProperty("ShopID")
    int shopID;
    @JsonProperty("Status")
    String status;
    @JsonProperty("Time")
    String time;
    @JsonProperty("TotalFee")
    int totalFee;
    @JsonProperty("Type")
    String type;
    @JsonProperty("Warehouse")
    String warehouse;
    @JsonProperty("Weight")
    int weight;
    @JsonProperty("Width")
    int width;


    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Fee {
        @JsonProperty("CODFailedFee")
        int codFailedFee;
        @JsonProperty("CODFee")
        int codFee;
        @JsonProperty("Coupon")
        int coupon;
        @JsonProperty("DeliverRemoteAreasFee")
        int deliverRemoteAreasFee;
        @JsonProperty("DocumentReturn")
        int documentReturn;
        @JsonProperty("DoubleCheck")
        int doubleCheck;
        @JsonProperty("Insurance")
        int insurance;
        @JsonProperty("MainService")
        int mainService;
        @JsonProperty("PickRemoteAreasFee")
        int pickRemoteAreasFee;
        @JsonProperty("R2S")
        int r2s;
        @JsonProperty("Return")
        int returnFee;
        @JsonProperty("StationDO")
        int stationDO;
        @JsonProperty("StationPU")
        int stationPU;
        @JsonProperty("Total")
        int total;
    }
}
