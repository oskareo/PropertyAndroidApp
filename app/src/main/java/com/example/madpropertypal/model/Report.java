package com.example.madpropertypal.model;

public class Report {

    private int reportId;
    private int propertyId;
    private long dateOfViewing;
    private String interest;
    private long offerPrice;
    private long offerExpiryDate;
    private String conditionsOfOffer;
    private String viewingComments;

    public Report(int reportId, int propertyId, long dateOfViewing, String interest, long offerPrice, long offerExpiryDate, String conditionsOfOffer, String viewingComments) {
        this.reportId = reportId;
        this.propertyId = propertyId;
        this.dateOfViewing = dateOfViewing;
        this.interest = interest;
        this.offerPrice = offerPrice;
        this.offerExpiryDate = offerExpiryDate;
        this.conditionsOfOffer = conditionsOfOffer;
        this.viewingComments = viewingComments;
    }

    public int getReportId() {
        return reportId;
    }

    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(int propertyId) {
        this.propertyId = propertyId;
    }

    public long getDateOfViewing() {
        return dateOfViewing;
    }

    public void setDateOfViewing(long dateOfViewing) {
        this.dateOfViewing = dateOfViewing;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }

    public long getOfferPrice() {
        return offerPrice;
    }

    public void setOfferPrice(long offerPrice) {
        this.offerPrice = offerPrice;
    }

    public long getOfferExpiryDate() {
        return offerExpiryDate;
    }

    public void setOfferExpiryDate(long offerExpiryDate) {
        this.offerExpiryDate = offerExpiryDate;
    }

    public String getConditionsOfOffer() {
        return conditionsOfOffer;
    }

    public void setConditionsOfOffer(String conditionsOfOffer) {
        this.conditionsOfOffer = conditionsOfOffer;
    }

    public String getViewingComments() {
        return viewingComments;
    }

    public void setViewingComments(String viewingComments) {
        this.viewingComments = viewingComments;
    }
}
