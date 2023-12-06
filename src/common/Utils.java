package common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
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

    public static Stream<String> readFirstLine( String path, String splitExp ) {
        try {
            return Arrays.stream(
                    Files.readAllLines( Path.of( path ) ).get( 0 ).split( splitExp )
            );
        } catch ( IOException e ) {
            throw new RuntimeException( e.getMessage(), e );
        }
    }

    public static String readFirstLine( String path ) {
        return lines( path ).limit( 1 ).findAny().orElse( null );
    }

    public static Stream<String> matchGroups( String str, String regexp ) {
        return Pattern.compile( regexp )
                      .matcher( str )
                      .results()
                      .map( MatchResult::group );
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
        if ( dayInst1 != null ) {
            executeAndPrintAssert( dayInst1::task1, dayInst1.sampleName(), 1, expected1 );
        }
        if ( dayInst2 != null ) {
            executeAndPrintAssert( dayInst2::task2, dayInst2.sampleName(), 2, expected2 );
        }
    }

    private static <T> void executeAndPrintAssert(
            ThrowingSupplier<T> task,
            String sampleName,
            int taskNo,
            T expected
    ) {
        System.out.print( sampleName + ": executing task #" + taskNo );
        long start = System.currentTimeMillis();
        try {
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
        } catch ( Throwable e ) {
            throw new RuntimeException( e.getMessage(), e );
        }
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

    public static long mcd( long a, long b ) {
        while ( a != 0 && b != 0 ) {
            if ( a > b ) {
                a %= b;
            } else if ( a < b ) {
                b %= a;
            }
        }
        return a == 0 ? b : a;
    }

    public static long mcd( long... v ) {
        long result = v[0];
        for ( int i = 1; i < v.length; i++ ) {
            result = mcd( result, v[i] );
        }
        return result;
    }

    public static long mvp( long a, long b ) {
        return a / mcd( a, b ) * b;
    }

    public static long mvp( long... v ) {
        long result = v[0];
        for ( int i = 1; i < v.length; i++ ) {
            result = mvp( result, v[i] );
        }
        return result;
    }

    public interface ThrowingSupplier<T> {
        T get() throws Throwable;
    }

    public interface ThrowingConsumer<T> {
        void accept( T value ) throws Throwable;
    }
}
