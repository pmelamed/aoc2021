package aoc2021;

import common.AocDay;
import common.Utils;

import java.util.List;
import java.util.stream.Collectors;

public class Day02Y21 implements AocDay<Long, Long> {

    private enum Direction {
        FORWARD,
        UP,
        DOWN;
    }

    private static class Command {
        private final Direction dir;
        private final long scale;

        public Command( String command ) {
            var parts = command.split( " " );
            dir = Direction.valueOf( parts[0].toUpperCase() );
            scale = Long.parseLong( parts[1] );
        }
    }

    private final String name;
    private final List<Command> data;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/Y21D02S1.dat", 150L, 900L );
            executeTasks( "input/Y21D02I.dat", null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay( new Day02Y21( fileName ), expected1, expected2 );
    }

    public Day02Y21( String file ) {
        this.name = file;
        data = Utils.lines( file ).map( Command::new ).collect( Collectors.toList() );
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        long x = 0;
        long d = 0;

        for ( var command : data ) {
            switch ( command.dir ) {
                case FORWARD:
                    x += command.scale;
                    break;
                case UP:
                    d -= command.scale;
                    break;
                case DOWN:
                    d += command.scale;
                    break;
            }
        }
        return x * d;
    }

    public Long task2() {
        long x = 0;
        long d = 0;
        long aim = 0;

        for ( var command : data ) {
            switch ( command.dir ) {
                case FORWARD:
                    x += command.scale;
                    d += aim * command.scale;
                    break;
                case UP:
                    aim -= command.scale;
                    break;
                case DOWN:
                    aim += command.scale;
                    break;
            }
        }
        return x * d;
    }
}
