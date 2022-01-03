package aoc2019;

import common.AocDay;
import common.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Day10Y19 implements AocDay<Integer, Integer> {
    private static final Comparator<RelativeAsteroid> DISTANCE_COMPARATOR =
            Comparator.comparing( RelativeAsteroid::distance );

    private record Asteroid( int x, int y ) {
    }

    private record RelativeAsteroid( int dx, int dy, double distance ) {
        private RelativeAsteroid( int dx, int dy ) {
            this( dx, dy, Math.sqrt( dx * dx + dy * dy ) );
        }

        private double getComparableAngle() {
            if ( dx >= 0 && dy < 0 ) {
                return 0.0 + dx / distance;
            }
            if ( dy >= 0 && dx > 0 ) {
                return 1.0 + dy / distance;
            }
            if ( dx <= 0 && dy > 0 ) {
                return 2.0 - dx / distance;
            }
            if ( dy <= 0 && dx < 0 ) {
                return 3.0 - dy / distance;
            }
            return 0;
        }
    }

    private record AngleAsteroid( int x, int y, int circle, double angle ) {
        public AngleAsteroid( RelativeAsteroid relative, Asteroid station, int circle ) {
            this(
                    station.x + relative.dx,
                    station.y + relative.dy,
                    circle,
                    relative.getComparableAngle()
            );
        }
    }

    private final String name;
    private final List<Asteroid> asteroids;
    private Asteroid station;

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2019/d10s01.dat", 8, null );
            executeTasks( "input/2019/d10s02.dat", 33, null );
            executeTasks( "input/2019/d10s03.dat", 35, null );
            executeTasks( "input/2019/d10s04.dat", 41, null );
            executeTasks( "input/2019/d10s05.dat", 210, 802 );
            executeTasks( "input/2019/d10i.dat", 296, 204 );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Integer expected1, Integer expected2 ) {
        Utils.executeDay( new Day10Y19( fileName ), expected1, expected2 );
    }

    public Day10Y19( String file ) {
        this.name = file;
        this.asteroids = new ArrayList<>();
        List<String> lines = Utils.readLines( file );
        for ( int y = 0; y < lines.size(); y++ ) {
            char[] line = lines.get( y ).toCharArray();
            for ( int x = 0; x < line.length; ++x ) {
                if ( line[x] == '#' ) {
                    asteroids.add( new Asteroid( x, y ) );
                }
            }
        }
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Integer task1() {
        int maxVisible = 0;
        for ( Asteroid asteroid : asteroids ) {
            int visibleFrom = getVisibleFrom( asteroid );
            if ( maxVisible < visibleFrom ) {
                maxVisible = visibleFrom;
                station = asteroid;
            }
        }
        return maxVisible;
    }

    public Integer task2() {
        if ( asteroids.size() < 201 ) {
            return 0;
        }
        List<AngleAsteroid> grouped = groupByCircle();
        AngleAsteroid asteroid = grouped.get( 199 );
        return asteroid.x * 100 + asteroid.y;
    }

    private int getVisibleFrom( Asteroid asteroid ) {
        int visible = 0;
        ArrayList<RelativeAsteroid> relatives = getRelativeAsteroids( asteroid );
        while ( !relatives.isEmpty() ) {
            RelativeAsteroid target = relatives.remove( 0 );
            visible++;
            relatives.removeIf( ra -> isOnSameLine( target, ra ) );
        }
        return visible;
    }

    private ArrayList<RelativeAsteroid> getRelativeAsteroids( Asteroid asteroid ) {
        return asteroids.stream()
                        .filter( a -> a != asteroid )
                        .map( a -> new RelativeAsteroid(
                                a.x - asteroid.x,
                                a.y - asteroid.y
                        ) )
                        .sorted( DISTANCE_COMPARATOR )
                        .collect( Collectors.toCollection( ArrayList::new ) );
    }

    private List<AngleAsteroid> groupByCircle() {
        ArrayList<RelativeAsteroid> relatives = getRelativeAsteroids( station );
        List<AngleAsteroid> result = new ArrayList<>( relatives.size() );
        while ( !relatives.isEmpty() ) {
            RelativeAsteroid target = relatives.remove( 0 );
            result.add( new AngleAsteroid( target, station, 1 ) );
            Iterator<RelativeAsteroid> iterator = relatives.iterator();
            int circle = 2;
            while ( iterator.hasNext() ) {
                RelativeAsteroid checked = iterator.next();
                if ( !isOnSameLine( target, checked ) ) {
                    continue;
                }
                iterator.remove();
                result.add( new AngleAsteroid( target, station, circle ) );
                circle++;
            }
        }
        result.sort( Comparator.comparing( AngleAsteroid::circle ).thenComparing( AngleAsteroid::angle ) );
        return result;
    }

    private boolean isOnSameLine( RelativeAsteroid front, RelativeAsteroid checked ) {
        if ( checked.dx * front.dx < 0 || checked.dy * front.dy < 0 ) {
            return false;
        }
        if ( front.dx == 0 ) {
            return checked.dx == 0;
        }
        if ( front.dy == 0 ) {
            return checked.dy == 0;
        }
        int div = getLeastDivider( Math.abs( front.dx ), Math.abs( front.dy ) );
        int dx = front.dx / div;
        int dy = front.dy / div;
        return checked.dx % dx == 0 && checked.dy % dy == 0 && checked.dx / dx == checked.dy / dy;
    }

    private int getLeastDivider( int a, int b ) {
        while ( a != b ) {
            if ( a > b ) {
                a %= b;
                if ( a == 0 ) {
                    return b;
                }
            } else {
                b %= a;
                if ( b == 0 ) {
                    return a;
                }
            }
        }
        return a;
    }
}
