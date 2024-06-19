package com.example.railwaybiddingdata_goi;


public class Railway {
    String id;
    String bidAmount;
    String auctionId;
    String bidderId;
    String enterpriseName;
    String samStatus;
    String dunsNumber;
    String creationDate;
    String updationDate;


    public Railway() {
        // Default constructor required for Firebase deserialization
    }
    public Railway(String id, String bidAmount, String auctionId, String bidderId, String enterpriseName, String samStatus, String dunsNumber, String creationDate, String updationDate) {
        this.id = id;
        this.bidAmount = bidAmount;
        this.auctionId = auctionId;
        this.bidderId = bidderId;
        this.enterpriseName = enterpriseName;
        this.samStatus = samStatus;
        this.dunsNumber = dunsNumber;
        this.creationDate = creationDate;
        this.updationDate = updationDate;
    }

    public String getId()
    {
        return id;
    }

    public String getBidAmount()
    {
        return bidAmount;
    }

    public String getAuctionId()
    {
        return auctionId;
    }

    public String getBidderId()
    {
        return bidderId;
    }

    public String getEnterpriseName()
    {
        return enterpriseName;
    }

    public String getSamStatus()
    {
        return samStatus;
    }

    public String getDunsNumber()
    {
        return dunsNumber;
    }

    public String getCreationDate()
    {
        return creationDate;
    }

    public String getUpdationDate()
    {
        return updationDate;
    }
}

