package aoc2022;

import common.AocDay;
import common.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.function.LongUnaryOperator;
import java.util.stream.Collectors;

public class Day11Y22 implements AocDay.DayLong {
    private final String fileName;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2022/Y22D11I.DAT", 76728L, 21553910156L );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String name, Long expected1, Long expected2 ) {
        Utils.executeDay( new Day11Y22( name ), expected1, expected2 );
    }

    public Day11Y22( String fileName ) {
        this.fileName = fileName;
    }

    @Override
    public String sampleName() {
        return fileName;
    }

    public Long task1() {
        return executeTurns( 20, true );
    }

    public Long task2() {
        return executeTurns( 10_000, false );
    }

    private long executeTurns( int turns, boolean reduceWorry ) {
        List<String> commands = Utils.readLines( fileName );
        List<Monkey> monkeys = new ArrayList<>();
        for ( int lineIndex = 0; lineIndex < commands.size(); lineIndex += 7 ) {
            monkeys.add( new Monkey(
                    commands.get( lineIndex + 1 ),
                    commands.get( lineIndex + 2 ),
                    commands.get( lineIndex + 3 ),
                    commands.get( lineIndex + 4 ),
                    commands.get( lineIndex + 5 )
            ) );
        }
        long mvp = monkeys.stream()
                          .mapToLong( Monkey::getDivider )
                          .reduce( 1L, ( a, b ) -> a * b );
        for ( int turn = 0; turn < turns; turn++ ) {
            for ( Monkey monkey : monkeys ) {
                Queue<Item> items = monkey.getItems();
                while ( !items.isEmpty() ) {
                    Item item = items.remove();
                    monkeys.get( monkey.inspectItem( item, reduceWorry, mvp ) ).addItem( item );
                }
            }
        }
        return monkeys.stream()
                      .sorted( Comparator.comparing( Monkey::getInspects ).reversed() )
                      .limit( 2 )
                      .mapToLong( Monkey::getInspects )
                      .reduce( 1, ( b1, b2 ) -> b1 * b2 );
    }

    private static class Item {
        private long level;

        private Item( String level ) {
            this.level = Long.parseLong( level );
        }

        private long getLevel() {
            return level;
        }

        private void setLevel( long level ) {
            this.level = level;
        }
    }

    private static class Monkey {
        private final LongUnaryOperator operation;
        private final long divider;
        private final int targetTrue;
        private final int targetFalse;
        private long inspects = 0;
        private final Queue<Item> items;

        private Monkey(
                String startingStr,
                String operationStr,
                String dividerStr,
                String targetTrueStr,
                String targetFalseStr
        ) {
            items = Arrays.stream( startingStr.substring( "  Starting items: ".length() ).split( ", " ) )
                          .map( Item::new )
                          .collect( Collectors.toCollection( LinkedList::new ) );
            String opCode = operationStr.substring( "  Operation: new = old ".length() );
            long opArg = Optional.of( opCode.substring( 2 ) )
                                 .filter( argCode -> !argCode.equals( "old" ) )
                                 .map( Long::parseLong )
                                 .orElse( -1L );
            operation = switch ( opCode.charAt( 0 ) ) {
                case '+' -> opArg >= 0L ? l -> l + opArg : l -> l + l;
                case '*' -> opArg >= 0L ? l -> l * opArg : l -> l * l;
                default -> throw new RuntimeException( "Bad operation " + opCode );
            };
            divider = Long.parseLong( dividerStr.substring( "  Test: divisible by ".length() ) );
            targetTrue = Integer.parseInt( targetTrueStr.substring( "    If true: throw to monkey ".length() ) );
            targetFalse = Integer.parseInt( targetFalseStr.substring( "    If false: throw to monkey ".length() ) );
        }

        private int inspectItem( Item item, boolean reduceWorry, long mvp ) {
            inspects++;
            item.setLevel( operation.applyAsLong( item.getLevel() ) / ( reduceWorry ? 3 : 1 ) % mvp );
            return item.getLevel() % divider == 0 ? targetTrue : targetFalse;
        }

        private void addItem( Item item ) {
            items.add( item );
        }

        private long getInspects() {
            return inspects;
        }

        private Queue<Item> getItems() {
            return items;
        }

        public long getDivider() {
            return divider;
        }
    }
}

