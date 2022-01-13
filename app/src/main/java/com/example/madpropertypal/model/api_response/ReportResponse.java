package com.example.madpropertypal.model.api_response;

import java.util.List;

public class ReportResponse {
    private List<Data> data;

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ClassPojo [data = " + data + "]";
    }

    public class Data {
        private String dateOfViewing;

        private String conditionsOfOffer;

        private String offerPrice;

        private String interest;

        private String viewingComments;

        private String offerExpiryDate;

        private String id;

        private String propertyId;

        public String getDateOfViewing() {
            return dateOfViewing;
        }

        public void setDateOfViewing(String dateOfViewing) {
            this.dateOfViewing = dateOfViewing;
        }

        public String getConditionsOfOffer() {
            return conditionsOfOffer;
        }

        public void setConditionsOfOffer(String conditionsOfOffer) {
            this.conditionsOfOffer = conditionsOfOffer;
        }

        public String getOfferPrice() {
            return offerPrice;
        }

        public void setOfferPrice(String offerPrice) {
            this.offerPrice = offerPrice;
        }

        public String getInterest() {
            return interest;
        }

        public void setInterest(String interest) {
            this.interest = interest;
        }

        public String getViewingComments() {
            return viewingComments;
        }

        public void setViewingComments(String viewingComments) {
            this.viewingComments = viewingComments;
        }

        public String getOfferExpiryDate() {
            return offerExpiryDate;
        }

        public void setOfferExpiryDate(String offerExpiryDate) {
            this.offerExpiryDate = offerExpiryDate;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPropertyId() {
            return propertyId;
        }

        public void setPropertyId(String propertyId) {
            this.propertyId = propertyId;
        }

        @Override
        public String toString() {
            return "ClassPojo [dateOfViewing = " + dateOfViewing + ", conditionsOfOffer = " + conditionsOfOffer + ", offerPrice = " + offerPrice + ", interest = " + interest + ", viewingComments = " + viewingComments + ", offerExpiryDate = " + offerExpiryDate + ", id = " + id + ", propertyId = " + propertyId + "]";
        }
    }

}
