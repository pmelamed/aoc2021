package aoc2019;

import common.AocDay;
import common.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Day17Y19 implements AocDay<Integer, Integer> {

    private final String name;
    private final IntComputer.Ram ram;
    private char[][] scaffolding;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2019/d17i.dat", 10632, 1356191 );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Integer expected1, Integer expected2 ) {
        Utils.executeDay(
                new Day17Y19( fileName ),
                expected1,
                expected2
        );
    }

    private Day17Y19( String file ) {
        this.name = file;
        ram = new IntComputer.Ram( Utils.readLines( file ).get( 0 ) );
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Integer task1() throws InterruptedException {
        StringBuilder builder = new StringBuilder();
        IntComputer.fromRam( ram ).interpret(
                IntComputer.nullInput(),
                value -> builder.append( (char) value.intValue() )
        );
        scaffolding = Arrays.stream( builder.toString().split( "\n" ) )
                            .map( String::toCharArray )
                            .toArray( char[][]::new );
        int rows = scaffolding.length;
        int cols = scaffolding[0].length;
        int result = 0;
        for ( int row = 1; row < rows - 1; ++row ) {
            for ( int col = 1; col < cols - 1; ++col ) {
                if ( scaffolding[row][col] == '#'
                        && scaffolding[row - 1][col] == '#'
                        && scaffolding[row + 1][col] == '#'
                        && scaffolding[row][col - 1] == '#'
                        && scaffolding[row][col + 1] == '#' ) {
                    result += row * col;
                }
            }
        }
//        System.out.println();
//        System.out.println( builder );
        return result;
    }

    public Integer task2() throws InterruptedException {
//        int robotRow = -1;
//        int robotCol = 0;
//        int robotDirCol = 0;
//        int robotDirRow = 0;
//        for ( int row = 0; row < scaffolding.length && robotRow < 0; row++ ) {
//            char[] scafRow = scaffolding[row];
//            for ( int col = 0; col < scafRow.length && robotRow < 0; col++ ) {
//                switch ( scafRow[col] ) {
//                    case '<':
//                        robotDirCol = -1;
//                        break;
//                    case '>':
//                        robotDirCol = 1;
//                        break;
//                    case '^':
//                        robotDirRow = -1;
//                        break;
//                    case 'v':
//                        robotDirRow = 1;
//                        break;
//                    default:
//                        continue;
//                }
//                robotCol = col;
//                robotRow = row;
//            }
//        }
//        int count = 0;
//        int tmpDirCol;
//        List<String> commands = new ArrayList<>();
//        do {
//            if ( !isScaffolding( robotRow + robotDirRow, robotCol + robotDirCol ) ) {
//                if ( count > 0 ) {
//                    commands.add( Integer.toString( count ) );
//                }
//                count = 0;
//                if ( isScaffolding( robotRow + robotDirCol, robotCol - robotDirRow ) ) {
//                    // R: x = -y; y =  x
//                    commands.add( "R" );
//                    tmpDirCol = robotDirCol;
//                    robotDirCol = -robotDirRow;
//                    robotDirRow = tmpDirCol;
//                } else if ( isScaffolding( robotRow - robotDirCol, robotCol + robotDirRow ) ) {
//                    // L: x =  y; y = -x
//                    commands.add( "L" );
//                    tmpDirCol = robotDirCol;
//                    robotDirCol = robotDirRow;
//                    robotDirRow = -tmpDirCol;
//                } else {
//                    break;
//                }
//            } else {
//                count++;
//                robotRow += robotDirRow;
//                robotCol += robotDirCol;
//            }
//        } while ( true );
//        System.out.println();
//        System.out.println( String.join( ",", commands ) );
        char[] commandLine = ( """
                A,B,A,C,A,A,C,B,C,B
                L,12,L,8,R,12
                L,10,L,8,L,12,R,12
                R,12,L,8,L,10
                n
                """ ).toCharArray();
        long[] input = new long[commandLine.length];
        for ( int index = 0; index < commandLine.length; index++ ) {
            input[index] = commandLine[index];
        }
        List<Long> dust = new ArrayList<>();
        IntComputer.fromRam( ram )
                   .fixMemory( Map.of( 0, 2L ) )
                   .interpret(
                           IntComputer.arrayInput( input ),
                           IntComputer.listOutput( dust )
                   );
//        System.out.println();
//        System.out.println( dust.stream()
//                                .limit( dust.size() - 1 )
//                                .map( v -> (char) v.intValue() )
//                                .map( Object::toString )
//                                .collect( Collectors.joining() ) );
        return dust.get( dust.size() - 1 ).intValue();
    }

    private boolean isScaffolding( int row, int col ) {
        return row >= 0 && row < scaffolding.length
                && col >= 0 && col < scaffolding[0].length
                && scaffolding[row][col] == '#';
    }
}
