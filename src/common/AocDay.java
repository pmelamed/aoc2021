package common;

public interface AocDay<T1, T2> {
    String sampleName();

    T1 task1() throws Throwable;

    T2 task2() throws Throwable;
}
