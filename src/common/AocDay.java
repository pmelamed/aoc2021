package common;

public interface AocDay<T1, T2> {
    String sampleName();

    T1 task1() throws Throwable;

    T2 task2() throws Throwable;

    interface DayLong extends AocDay<Long, Long> {
    }

    interface DayInt extends AocDay<Integer, Integer> {
    }

    interface DayStr extends AocDay<String, String> {
    }
}
