package umc.parasol.domain.notification.domain;

public enum NotificationType {
    RENT_COMPLETED("대여 완료"),
    SALE_COMPLETED("판매 완료"),
    RETURN_COMPLETED("반납 완료"),
    FREE_RENTAL_END("무료대여 종료"),
    OVERDUE_FINE("연체료 누적");

    private final String value;

    NotificationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

