package aoc2021;

import common.AocDay;
import common.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day22Y21 implements AocDay<Long, Long> {
    private static final Comparator<Cell> CELL_COMPARATOR = Comparator.comparing( Cell::packed );

    private static final int FIRST = 1;
    private static final int BOTH = 3;

    private static record Cell(int x, int y, int z, long packed) {
        public Cell( int x, int y, int z ) {
            this( x, y, z, ( ( x + 100000L ) * 200000L + ( y + 100000 ) ) * 200000L + ( z + 100000L ) );
        }
    }

    private static record Region(int xmin, int xmax, int ymin, int ymax, int zmin, int zmax) {
        public Region( Line x, Line y, Line z ) {
            this( x.min, x.max, y.min, y.max, z.min, z.max );
        }

        public Region( Command cmd ) {
            this( cmd.xmin, cmd.xmax, cmd.ymin, cmd.ymax, cmd.zmin, cmd.zmax );
        }

        private long volume() {
            return (long) ( xmax - xmin + 1 ) * ( ymax - ymin + 1 ) * ( zmax - zmin + 1 );
        }

        @Override
        public String toString() {
            return "%d..%d,%d..%d,%d..%d".formatted( xmin, xmax, ymin, ymax, zmin, zmax );
        }

    }

    private static record Line(int min, int max, int belongs) {
        @Override
        public boolean equals( Object obj ) {
            if ( obj instanceof Line l ) {
                return min == l.min && max == l.max;
            }
            return false;
        }
    }

    private static record Command(boolean action, int xmin, int xmax, int ymin, int ymax, int zmin, int zmax) {
        public Command( String[] parsed ) {
            this(
                    "on".equals( parsed[0] ),
                    Integer.parseInt( parsed[1] ),
                    Integer.parseInt( parsed[2] ),
                    Integer.parseInt( parsed[3] ),
                    Integer.parseInt( parsed[4] ),
                    Integer.parseInt( parsed[5] ),
                    Integer.parseInt( parsed[6] )
            );
        }

        public Command( String cmdLine ) {
            this( cmdLine.split( "[ ,][xyz]=|\\.\\." ) );
        }
    }

    private final String name;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2021/Y21D22S1.dat", 39L, 39L );
            executeTasks( "input/2021/Y21D22S2.dat", 590784L, null );
            executeTasks( "input/2021/Y21D22S3.dat", 474140L, 2758514936282235L );
            executeTasks( "input/2021/Y21D22I.dat", null, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay(
                new Day22Y21( fileName ),
                expected1,
                expected2
        );
    }

    public Day22Y21( String file ) {
        this.name = file;
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        Set<Cell> firedCells = new TreeSet<>( CELL_COMPARATOR );
        Utils.lines( name ).map( Command::new ).forEach( cmd -> {
            int xmin = Math.max( cmd.xmin, -50 );
            int xmax = Math.min( cmd.xmax, 50 );
            int ymin = Math.max( cmd.ymin, -50 );
            int ymax = Math.min( cmd.ymax, 50 );
            int zmin = Math.max( cmd.zmin, -50 );
            int zmax = Math.min( cmd.zmax, 50 );
            if ( xmin > xmax || ymin > ymax || zmin > zmax ) {
                return;
            }
            for ( int x = xmin; x <= xmax; ++x ) {
                for ( int y = ymin; y <= ymax; ++y ) {
                    for ( int z = zmin; z <= zmax; ++z ) {
                        if ( cmd.action ) {
                            firedCells.add( new Cell( x, y, z ) );
                        } else {
                            firedCells.remove( new Cell( x, y, z ) );
                        }
                    }
                }
            }
        } );
        return (long) firedCells.size();
    }


    public Long task2() {
        List<Region> state = Utils.lines( name )
                                  .map( Command::new )
                                  .reduce( new ArrayList<>(), Day22Y21::apply, ( l1, l2 ) -> l1 );
        return getVolume( state );
    }

    private static long getVolume( List<Region> results ) {
        return results.stream().mapToLong( Region::volume ).sum();
    }

    private static List<Region> apply( List<Region> state, Command cmd ) {
        return cmd.action ? applyOn( state, new Region( cmd ) ) : applyOff( state, new Region( cmd ) );
    }

    private static List<Region> applyOn( List<Region> state, Region on ) {
        state.addAll( getNonOverlapping( on, state ) );
        return state;
    }

    private static List<Region> applyOff( List<Region> state, Region off ) {
        return getNonOverlapping( state, off );
    }

    private static List<Region> getNonOverlapping( Region source, List<Region> dest ) {
        List<Region> generation = List.of( source );
        for ( Region destRegion : dest ) {
            generation = getNonOverlapping( generation, destRegion );
            if ( generation.isEmpty() ) {
                return List.of();
            }
        }
        return generation;
    }

    private static List<Region> getNonOverlapping( List<Region> source, Region dest ) {
        return source.stream()
                     .flatMap( region -> getNotOverlapped( region, dest ).stream() )
                     .collect( Collectors.toList() );
    }

    private static List<Region> getNotOverlapped( Region region1, Region region2 ) {
        Line[] xlines = splitLine( region1.xmin, region1.xmax, region2.xmin, region2.xmax );
        if ( xlines == null ) {
            return List.of( region1 );
        }
        Line[] ylines = splitLine( region1.ymin, region1.ymax, region2.ymin, region2.ymax );
        if ( ylines == null ) {
            return List.of( region1 );
        }
        Line[] zlines = splitLine( region1.zmin, region1.zmax, region2.zmin, region2.zmax );
        if ( zlines == null ) {
            return List.of( region1 );
        }
        List<Region> result = new ArrayList<>();
        for ( Line xline : xlines ) {
            for ( Line yline : ylines ) {
                for ( Line zline : zlines ) {
                    if ( ( xline.belongs & yline.belongs & zline.belongs ) == FIRST ) {
                        result.add( new Region( xline, yline, zline ) );
                    }
                }
            }
        }
        return result;
    }

    private static Line[] splitLine( int min1, int max1, int min2, int max2 ) {
        if ( min1 > max2 || min2 > max1 ) {
            return null;
        }
        int[] points = IntStream.of( min1, max1, min2, max2 )
                                .sorted()
                                .toArray();
        Line[] lines = new Line[3];
        int count = 0;
        if ( points[0] != points[1] && points[0] == min1 ) {
            lines[count++] = new Line( points[0], points[1] - 1, FIRST );
        }
        lines[count++] = new Line( points[1], points[2], BOTH );
        if ( points[2] != points[3] && points[3] == max1 ) {
            lines[count++] = new Line( points[2] + 1, points[3], FIRST );
        }
        return count == 3 ? lines : Arrays.copyOf( lines, count );
    }
}
