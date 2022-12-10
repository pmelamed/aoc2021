package aoc2022;

import common.AocDay;
import common.Utils;

import java.util.List;

public class Day10Y22 implements AocDay.DayInt {
    private final String fileName;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2022/Y22D10I.DAT", 15260, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String name, Integer expected1, Integer expected2 ) {
        Utils.executeDay( new Day10Y22( name ), expected1, expected2 );
    }

    public Day10Y22( String fileName ) {
        this.fileName = fileName;
    }

    @Override
    public String sampleName() {
        return fileName;
    }

    public Integer task1() {
        List<String> commands = Utils.readLines( fileName );
        int cycle = 0;
        int sum = 0;
        int x = 1;
        for ( String command : commands ) {
            sum = registerSignal( ++cycle, x, sum );
            if ( command.startsWith( "addx" ) ) {
                sum = registerSignal( ++cycle, x, sum );
                x += Integer.parseInt( command.substring( 5 ) );
            }
        }
        return sum;
    }

    public Integer task2() {
        System.out.println();
        List<String> commands = Utils.readLines( fileName );
        int cycle = 0;
        int x = 1;
        for ( String command : commands ) {
            drawPixel( cycle++, x );
            if ( command.startsWith( "addx" ) ) {
                drawPixel( cycle++, x );
                x += Integer.parseInt( command.substring( 5 ) );
            }
        }
        System.out.println( "PGHFGLUG" );
        return null;
    }

    private int registerSignal( int cycle, int x, int sum ) {
        return ( cycle - 20 ) % 40 == 0 ? sum + cycle * x : sum;
    }

    private void drawPixel( int pixel, int x ) {
        int column = pixel % 40;
        System.out.print( column >= x - 1 && column <= x + 1 ? '#' : '.' );
        if ( column == 39 ) {
            System.out.println();
        }
    }
}

