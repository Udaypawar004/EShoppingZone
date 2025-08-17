package com.orderservice.app.dto;

import com.orderservice.app.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private String orderId;
    private String paymentUrl;
    private String sessionId;
    private Status status;
    private String message;

    public static class Builder {
        private String paymentUrl;
        private String orderId;
        private Status status;
        private String message;
        private String sessionId;

        public Builder paymentUrl(String url) {
            this.paymentUrl = url;
            return this;
        }
        public Builder sessionID(String url) {
            this.sessionId = url;
            return this;
        }
        public Builder orderId(String id) {
            this.orderId = id;
            return this;
        }
        public Builder status(Status status) {
            this.status = status;
            return this;
        }
        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public OrderResponse build() {
            OrderResponse response = new OrderResponse();
            response.paymentUrl = this.paymentUrl;
            response.orderId = this.orderId;
            response.sessionId = this.sessionId;
            response.status = this.status;
            response.message = this.message;
            return response;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
