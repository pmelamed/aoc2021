package aoc2019;

import common.AocDay;
import common.Utils;

import java.util.Map;
import java.util.function.Consumer;

public class Day13Y19 implements AocDay<Integer, Integer> {

    public static final int TYPE_BLOCK = 2;
    public static final int TYPE_PADDLE = 3;
    public static final int TYPE_BALL = 4;

    private static class GameData {
        private int score = 0;
        private int ballX = 0;
        private int paddleX = 0;
    }

    private final String name;
    private final IntComputer.Ram ram;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2019/d13i.dat", 335, 15706 );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Integer expected1, Integer expected2 ) {
        Utils.executeDay(
                new Day13Y19( fileName ),
                expected1,
                expected2
        );
    }

    public Day13Y19( String file ) {
        this.name = file;
        ram = new IntComputer.Ram( Utils.readLines( file ).get( 0 ) );
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Integer task1() throws InterruptedException {
        int[] count = { 0 };
        IntComputer.fromRam( ram )
                   .interpret(
                           IntComputer.nullInput(),
                           new OutputCollector( ( x, y, d ) -> {
                               if ( d == TYPE_BLOCK ) {
                                   count[0]++;
                               }
                           } )
                   );
        return count[0];
    }

    public Integer task2() throws InterruptedException {
        GameData gameData = new GameData();
        OutputCollector collector = new OutputCollector( ( x, y, d ) -> {
            if ( x == -1 && y == 0 ) {
                gameData.score = d;
            } else {
                if ( d == TYPE_PADDLE ) {
                    gameData.paddleX = x;
                }
                if ( d == TYPE_BALL ) {
                    gameData.ballX = x;
                }
            }
        } );
        IntComputer.fromRam( ram )
                   .fixMemory( Map.of( 0, 2L ) )
                   .interpret(
                           () -> (long) Long.compare( gameData.ballX, gameData.paddleX ),
                           collector
                   );
        return (int) gameData.score;
    }

    private static class OutputCollector implements Consumer<Long> {
        private final int[] output = new int[3];
        private int ptr;
        private final CoordsHandler handler;

        public OutputCollector( CoordsHandler handler ) {
            this.handler = handler;
        }

        @Override
        public void accept( Long value ) {
            output[ptr++] = value.intValue();
            if ( ptr == 3 ) {
                handler.handle( output[0], output[1], output[2] );
                ptr = 0;
            }
        }
    }

    private interface CoordsHandler {
        void handle( int x, int y, int value );
    }
}
