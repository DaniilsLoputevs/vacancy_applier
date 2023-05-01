package jobs.tools;


import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

/**
 * This tool JUST WORK!!!
 *
 * @author Daniils Loputevs
 * @version 1.3 - 01.05.2023
 */
public class TimeMarker {

//    public static void main(String[] args) {
////         0d 1h 15m 36s
//        val mills = 3_600_000 + 900_000 + 36_000 + 0L;
//    }
    
    private static final List<TimeMark> marks = new ArrayList<>();
    
    public static void addMark(String name) {
        marks.add(new TimeMark(name, System.nanoTime()));
    }
    
    
    public static void printMarks() {
        var rsl = new StringJoiner(System.lineSeparator()).add("TimeMark: -Name-  -time from last mark-");
        boolean isFirsts = true;
        TimeMark prevMark = null;
        for (var mark : marks) {
            if (isFirsts) {
                rsl.add(String.format("TimeMark: \"%s\"", mark.name));
                isFirsts = false;
            } else {
                var duration = mark.timeNano - prevMark.timeNano;
                var time = timeToString(duration, TimeUnit.NANOSECONDS, TimeUnit::toNanos);
                rsl.add(String.format("TimeMark: \"%s\" %s", mark.name, time));
            }
            prevMark = mark;
        }
        System.out.println(rsl);
    }
    
    public static void printBetweenMarks(String oneName, String twoName) {
        var one = findMarkByName(oneName);
        var two = findMarkByName(twoName);
        var rsl = timeToString(two.timeNano - one.timeNano, TimeUnit.NANOSECONDS, TimeUnit::toNanos);
        System.out.printf("Difference between TimeMarks(oneName=\"%s\" -> twoName=\"%s\") : %s MILLISECONDS %s",
                one.name, two.name, rsl, System.lineSeparator());
    }
    
    private static TimeMark findMarkByName(String name) {
        for (TimeMark mark : marks) if (mark.name.equals(name)) return mark;
        throw new NoSuchElementException(String.format("Doesn't found TimeMark with name=\"%s\"%s", name, System.lineSeparator()));
    }
    
    public static String timeToString(Long source,
                                      TimeUnit sourceTimeUnit,
                                      BiFunction<TimeUnit, Long, Long> backConverter) {
        var exclude = 0L;
        
        var days = sourceTimeUnit.toDays(source);
        exclude += backConverter.apply(TimeUnit.DAYS, days);
        
        var hours = sourceTimeUnit.toHours(source - exclude);
        exclude += backConverter.apply(TimeUnit.HOURS, hours);
        
        var minutes = sourceTimeUnit.toMinutes(source - exclude);
        exclude += backConverter.apply(TimeUnit.MINUTES, minutes);
        
        var seconds = sourceTimeUnit.toSeconds(source - exclude);
        exclude += backConverter.apply(TimeUnit.SECONDS, seconds);
        
        var millis = sourceTimeUnit.toMillis(source - exclude);
        exclude += backConverter.apply(TimeUnit.MILLISECONDS, millis);
        
        var micro = sourceTimeUnit.toMicros(source - exclude);
        exclude += backConverter.apply(TimeUnit.MICROSECONDS, micro);
        
        var nano = sourceTimeUnit.toNanos(source - exclude);
        
        return days + "d " +
                hours + "h " +
                minutes + "m " +
                seconds + "s " +
                millis + "ms " +
                micro + "us " +
                nano + "ns ";
    }
    
    private static class TimeMark {
        private final String name;
        private final long timeNano;
    
        public TimeMark(String name, long timeNano) {
            this.name = name;
            this.timeNano = timeNano;
        }
    }
    
    
}
