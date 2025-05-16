package com.ufomap.api.model;

public enum SubmissionStatus {
    PENDING("pending"),
    APPROVED("approved"),
    REJECTED("rejected");

    private final String status;

    SubmissionStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public static SubmissionStatus fromString(String status) {
        for (SubmissionStatus submissionStatus : SubmissionStatus.values()) {
            if (submissionStatus.status.equalsIgnoreCase(status)) {
                return submissionStatus;
            }
        }
        throw new IllegalArgumentException("Unknown submission status: " + status);
    }
}
