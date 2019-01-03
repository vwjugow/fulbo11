package ar.com.fulbo11.domain;

import java.util.Comparator;

import static ar.com.fulbo11.domain.Substitute.HONOUR_COMPARATOR;

public enum HonourGroup implements Comparable<HonourGroup> {
    ACTIVE(HONOUR_COMPARATOR),
    INACTIVE(HONOUR_COMPARATOR),
    UNRESPONSIVE(HONOUR_COMPARATOR),
    LATE(Comparator.comparing(Substitute::getConfirmationDate)),
    ;

    private final Comparator<Substitute> comparator;

    HonourGroup(Comparator<Substitute> comparator) {
        this.comparator = comparator;
    }

    public Comparator<Substitute> getComparator() {
        return comparator;
    }
}
