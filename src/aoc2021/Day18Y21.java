package aoc2021;

import common.AocDay;
import common.Utils;

import java.util.ArrayList;
import java.util.List;

public class Day18Y21 implements AocDay<Long, Long> {

    private final String name;

    private static class SnailfishPair {
        private final List<Integer> depth = new ArrayList<>();
        private final List<Integer> value = new ArrayList<>();

        public SnailfishPair( String str ) {
            int curDepth = 0;
            for ( char ch : str.toCharArray() ) {
                switch ( ch ) {
                    case '[':
                        ++curDepth;
                        break;
                    case ']':
                        --curDepth;
                        break;
                    case ',':
                        break;
                    default:
                        depth.add( curDepth );
                        value.add( ch - '0' );
                }
            }
        }

        public SnailfishPair( SnailfishPair lhs, SnailfishPair rhs ) {
            depth.addAll( lhs.depth );
            value.addAll( lhs.value );
            depth.addAll( rhs.depth );
            value.addAll( rhs.value );
            if ( lhs.isNotEmpty() && rhs.isNotEmpty() ) {
                for ( var iter = depth.listIterator(); iter.hasNext(); ) {
                    iter.set( iter.next() + 1 );
                }
            }
            reduce();
        }

        private static SnailfishPair add( SnailfishPair lhs, SnailfishPair rhs ) {
            return new SnailfishPair( lhs, rhs );
        }

        private boolean isNotEmpty() {
            return !depth.isEmpty();
        }

        private void reduce() {
            while ( true ) {
                if ( checkExplode() ) {
                    continue;
                }
                if ( checkSplit() ) {
                    continue;
                }
                break;
            }
        }

        private boolean checkExplode() {
            int length = depth.size() - 1;
            for ( int index = 0; index < length; ++index ) {
                if ( depth.get( index ) == 5 ) {
                    if ( index > 0 ) {
                        value.set( index - 1, value.get( index - 1 ) + value.get( index ) );
                    }
                    if ( index + 2 < depth.size() ) {
                        value.set( index + 2, value.get( index + 2 ) + value.get( index + 1 ) );
                    }
                    depth.set( index, 4 );
                    value.set( index, 0 );
                    depth.remove( index + 1 );
                    value.remove( index + 1 );
                    return true;
                }
            }
            return false;
        }

        private boolean checkSplit() {
            int length = depth.size();
            for ( int index = 0; index < length; ++index ) {
                int v = value.get( index );
                if ( v >= 10 ) {
                    int d = depth.get( index );
                    int half = v / 2;
                    value.set( index, half );
                    depth.set( index, d + 1 );
                    value.add( index + 1, v - half );
                    depth.add( index + 1, d + 1 );
                    return true;
                }
            }
            return false;
        }

        private int magnitude() {
            for ( int level = 4; level > 0; --level ) {
                for ( int index = 0; index < depth.size() - 1; ++index ) {
                    if ( depth.get( index ) == level ) {
                        value.set( index, value.get( index ) * 3 + value.get( index + 1 ) * 2 );
                        depth.set( index, level - 1 );
                        depth.remove( index + 1 );
                        value.remove( index + 1 );
                    }
                }
            }
            return value.get( 0 );
        }
    }

    public static void main( String[] args ) {
        try {
            executeTasks( "input/Y21D18I.dat", null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay(
                new Day18Y21( fileName ),
                expected1,
                expected2
        );
    }

    public Day18Y21( String file ) {
        this.name = file;
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        return (long) Utils.lines( name )
                           .map( SnailfishPair::new )
                           .reduce( new SnailfishPair( "" ), SnailfishPair::add )
                           .magnitude();
    }

    public Long task2() {
        int result = 0;
        SnailfishPair[] numbers = Utils.lines( name ).map( SnailfishPair::new ).toArray( SnailfishPair[]::new );
        for ( SnailfishPair lhs : numbers ) {
            for ( SnailfishPair rhs : numbers ) {
                if ( lhs == rhs ) {
                    continue;
                }
                int magnitude = SnailfishPair.add( lhs, rhs ).magnitude();
                if ( magnitude > result ) {
                    result = magnitude;
                }
            }
        }
        return (long) result;
    }
}
