package common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Utils {
    public static void debug( String msg, Object... args ) {
        System.out.printf( msg + "%n", args );
    }

    public static Stream<String> lines( String path ) {
        try {
            return Files.lines( Path.of( path ) );
        } catch ( IOException e ) {
            throw new RuntimeException( e.getMessage(), e );
        }
    }

    public static List<String> readLines( String path ) {
        try {
            return Files.readAllLines( Path.of( path ) );
        } catch ( IOException e ) {
            throw new RuntimeException( e.getMessage(), e );
        }
    }

    public static <T1, T2> void executeDay( AocDay<T1, T2> day, T1 expected1, T2 expected2 ) {
        executeDay( day, day, expected1, expected2 );
    }

    public static <T1, T2> void executeDay(
            AocDay<T1, T2> dayInst1,
            AocDay<T1, T2> dayInst2,
            T1 expected1,
            T2 expected2
    ) {
        executeAndPrintAssert( dayInst1::task1, dayInst1.sampleName(), 1, expected1 );
        executeAndPrintAssert( dayInst2::task2, dayInst2.sampleName(), 2, expected2 );
    }

    private static <T> void executeAndPrintAssert( Supplier<T> task, String sampleName, int taskNo, T expected ) {
        System.out.print( sampleName + ": executing task #" + taskNo );
        long start = System.currentTimeMillis();
        T result = task.get();
        long time = System.currentTimeMillis() - start;
        System.out.print( " = " + result + " [" + time + "ms]" );
        if ( expected != null ) {
            if ( expected.equals( result ) ) {
                System.out.print( " == " + expected + " - OK!" );
            } else {
                System.out.print( " != " + expected + " - Failed!" );
            }
        }
        System.out.println();
    }

    public static String bin( long value, int digits ) {
        return padLeft( Long.toString( value, 2 ), digits, '0' );
    }

    public static String padLeft( String str, int length, char ch ) {
        if ( str.length() >= length ) {
            return str;
        }
        StringBuilder result = new StringBuilder( length );
        for ( int i = str.length(); i < length; ++i ) {
            result.append( ch );
        }
        return result.append( str ).toString();
    }
}
