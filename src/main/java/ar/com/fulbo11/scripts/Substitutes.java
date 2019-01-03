package ar.com.fulbo11.scripts;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import ar.com.fulbo11.domain.HonourGroup;
import ar.com.fulbo11.domain.Substitute;

import static ar.com.fulbo11.domain.HonourGroup.ACTIVE;
import static ar.com.fulbo11.domain.HonourGroup.INACTIVE;
import static ar.com.fulbo11.domain.HonourGroup.LATE;
import static ar.com.fulbo11.domain.HonourGroup.UNRESPONSIVE;

public class Substitutes {

    public static final String INPUT_FILE_PATH = "/home/kuvic/suplentes.csv";
    private static final String DELIMETER = ",";
    private static final String DATE_PATTERN = "yy/MM/dd HH:mm";
    public static final String TIME_ZONE = "America/Argentina/Buenos_Aires";
    private static final int CONFIRMATION_DEADLINE = 72;
    private static final int MATCH_HOUR = 9;
    private static final int MATCH_MINUTE = 0;
    public static final int NEXT_DAY_OF_MATCH = Calendar.SUNDAY + 7;
    public static final Comparator<Substitute> HONOUR_COMPARATOR =
            Comparator.comparing(Substitute::getHonour).reversed().thenComparing(Substitute::getConfidence);
    private static SimpleDateFormat SDF = new SimpleDateFormat(DATE_PATTERN);

    public static void main(String[] args) throws IOException, ParseException {
        BufferedReader br = new BufferedReader(
                new InputStreamReader(new DataInputStream(new FileInputStream(INPUT_FILE_PATH))));
        List<Substitute> substitutes = parseSubstitutes(br);
        splitSortAndPrint(substitutes);
    }

    private static List<Substitute> parseSubstitutes(BufferedReader br) throws IOException, ParseException {
        List<Substitute> substitues = new ArrayList();
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            substitues.add(parseSubstitute(line));
        }
        return substitues;
    }

    private static Substitute parseSubstitute(String line) throws ParseException {
        final String[] s = line.split(DELIMETER);
        Calendar confirmationDate = parseDate(s[3]);
        return new Substitute(s[0], Integer.parseInt(s[1]), Double.parseDouble(s[2]),
                getHonourGroup(s[4], confirmationDate), confirmationDate);
    }

    private static Calendar parseDate(String date) throws ParseException {
        final GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone(TIME_ZONE));
        calendar.setTime(SDF.parse(date));
        return calendar;
    }

    private static HonourGroup getHonourGroup(String group, Calendar confirmationDate) {
        Calendar nextMatchDate = getNextMatchDate();
        Calendar confirmationDate2 = (Calendar) confirmationDate.clone();
        confirmationDate2.add(Calendar.HOUR, CONFIRMATION_DEADLINE);
        if (confirmationDate2.after(nextMatchDate)) {
            return LATE;
        } else {
            return HonourGroup.valueOf(group);
        }
    }

    private static Calendar getNextMatchDate() {
        final GregorianCalendar nextSunday = new GregorianCalendar(TimeZone.getTimeZone(TIME_ZONE));
        nextSunday.add(Calendar.DATE, NEXT_DAY_OF_MATCH - nextSunday.get(Calendar.DAY_OF_WEEK));
        nextSunday.set(Calendar.HOUR_OF_DAY, MATCH_HOUR);
        nextSunday.set(Calendar.MINUTE, MATCH_MINUTE);
        nextSunday.set(Calendar.SECOND, 0);
        nextSunday.set(Calendar.MILLISECOND, 0);
        return nextSunday;
    }

    private static void splitSortAndPrint(List<Substitute> subs) {
        List<Substitute> active =
                subs.stream().filter(s -> ACTIVE.equals(s.getHonourGroup())).collect(Collectors.toList());
        List<Substitute> inactive =
                subs.stream().filter(s -> INACTIVE.equals(s.getHonourGroup())).collect(Collectors.toList());
        List<Substitute> unresponsive =
                subs.stream().filter(s -> UNRESPONSIVE.equals(s.getHonourGroup())).collect(Collectors.toList());
        List<Substitute> late = subs.stream().filter(s -> LATE.equals(s.getHonourGroup())).collect(Collectors.toList());

        active.sort(HONOUR_COMPARATOR);
        inactive.sort(HONOUR_COMPARATOR);
        unresponsive.sort(HONOUR_COMPARATOR);
        late.sort(Comparator.comparing(Substitute::getConfirmationDate));

        printGroup(active, inactive, unresponsive, late);
    }

    private static void printGroup(List<Substitute> active, List<Substitute> inactive, List<Substitute> unresponsive,
            List<Substitute> late) {
        System.out.println("*Activos*");
        AtomicInteger order = new AtomicInteger(0);
        active.forEach(s -> System.out.println(getSubstituteInfo(s, order.getAndIncrement())));
        System.out.println("*Inactivos con Aviso*");
        inactive.forEach(s -> System.out.println(getSubstituteInfo(s, order.getAndIncrement())));
        System.out.println("*Inactivos sin Aviso*");
        unresponsive.forEach(s -> System.out.println(getSubstituteInfo(s, order.getAndIncrement())));
        System.out.println("*Que confirmaron a menos de 72hs*");
        late.forEach(s -> System.out.println(getSubstituteInfo(s, order.getAndIncrement())));
    }

    private static String getSubstituteInfo(Substitute substitute, int i) {
        final StringBuilder sb = new StringBuilder();
        sb.append(i)
                .append(". ")
                .append(substitute.getFullName())
                .append(" (");
        if (HonourGroup.LATE.equals(substitute.getHonourGroup())) {
            sb.append(SDF.format(substitute.getConfirmationDate().getTime()));
        } else {
            sb.append(substitute.getHonour());
            sb.append(" (");
            sb.append(String.format("%f.00", substitute.getConfidence()));
            sb.append(")");
        }
        sb.append(")");
        return sb.toString();
    }
}
