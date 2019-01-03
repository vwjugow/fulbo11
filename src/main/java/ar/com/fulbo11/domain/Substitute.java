package ar.com.fulbo11.domain;

import java.util.Calendar;
import java.util.Comparator;

public class Substitute {
    public static final Comparator<Substitute> HONOUR_COMPARATOR =
            Comparator.comparing(Substitute::getHonour).thenComparing(Substitute::getConfidence);


    protected final String fullName;
    protected final int honour;
    protected final double confidence;
    protected final Calendar confirmationDate;
    protected final HonourGroup honourGroup;

    public Substitute(String fullName, int honour, double confidence, HonourGroup honourGroup,
            Calendar confirmationDate) {
        this.fullName = fullName;
        this.honour = honour;
        this.confidence = confidence;
        this.confirmationDate = confirmationDate;
        this.honourGroup = honourGroup;
    }


    public String getFullName() {
        return fullName;
    }

    public int getHonour() {
        return honour;
    }

    public double getConfidence() {
        return confidence;
    }

    public Calendar getConfirmationDate() {
        return confirmationDate;
    }

    public HonourGroup getHonourGroup() {
        return honourGroup;
    }

    @Override
    public String toString() {
        return "Substitute{" +
                "fullName='" + fullName + '\'' +
                ", honour=" + honour +
                ", confidence=" + confidence +
                ", confirmationDate=" + confirmationDate +
                ", honourGroup=" + honourGroup +
                '}';
    }
}
