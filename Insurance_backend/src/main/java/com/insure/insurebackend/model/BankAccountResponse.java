package com.insure.insurebackend.model;

public class BankAccountResponse {

    private String bankName;
    private String maskedAccountNumber;
    private String ifscCode;
    private boolean verified;

    public BankAccountResponse() {
    }

    public BankAccountResponse(String bankName,
                               String maskedAccountNumber,
                               String ifscCode,
                               boolean verified) {
        this.bankName = bankName;
        this.maskedAccountNumber = maskedAccountNumber;
        this.ifscCode = ifscCode;
        this.verified = verified;
    }

    public static BankAccountResponse fromEntity(BankAccount account) {
        if (account == null) return null;

        String acc = account.getAccountNumber();
        String masked = acc == null || acc.length() < 4
                ? "****"
                : "****" + acc.substring(acc.length() - 4);

        return new BankAccountResponse(
                account.getBankName(),
                masked,
                account.getIfscCode(),
                account.isVerified()
        );
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getMaskedAccountNumber() {
        return maskedAccountNumber;
    }

    public void setMaskedAccountNumber(String maskedAccountNumber) {
        this.maskedAccountNumber = maskedAccountNumber;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}

