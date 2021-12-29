package aoc2021;

import common.AocDay;
import common.Utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

public class Day19Y21 implements AocDay<Long, Long> {

    private static record PointsMatchResult(Point srcPoint, Point dstPoint, Map<Point, long[]> diffs) {
    }

    private static class Point {
        private int x;
        private int y;
        private int z;

        public Point( int x, int y, int z ) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        private static Point valueOf( String str ) {
            var components = str.split( "," );
            return new Point(
                    Integer.parseInt( components[0] ),
                    Integer.parseInt( components[1] ),
                    Integer.parseInt( components[2] )
            );
        }

        private static long pack( int x, int y, int z ) {
            return ( ( (long) z + 2000 ) * 4000 + y + 2000 ) * 4000 + x + 2000;
        }

        private static long diff( Point lhs, Point rhs ) {
            return pack( rhs.x - lhs.x, rhs.y - lhs.y, rhs.z - lhs.z );
        }

        private static long distance( Point lhs, Point rhs ) {
            return Math.abs( lhs.x - rhs.x ) + Math.abs( lhs.y - rhs.y ) + Math.abs( lhs.z - rhs.z );
        }

        private void rotateX() {
            int tmpY = -z;
            z = y;
            y = tmpY;
        }

        private void rotateY() {
            int tmpX = -x;
            x = z;
            z = tmpX;
        }

        private void rotateZ() {
            int tmpY = -x;
            x = y;
            y = tmpY;
        }

        private void translate( int dx, int dy, int dz ) {
            x += dx;
            y += dy;
            z += dz;
        }

        private long packed() {
            return pack( x, y, z );
        }

    }

    private static class ScannerData {
        private Point[] points;

        public ScannerData( List<Point> points ) {
            this.points = points.toArray( Point[]::new );
        }

        private void rotateX() {
            for ( Point point : points ) {
                point.rotateX();
            }
        }

        private void rotateY() {
            for ( Point point : points ) {
                point.rotateY();
            }
        }

        private void rotateZ() {
            for ( Point point : points ) {
                point.rotateZ();
            }
        }

        private void translate( int dx, int dy, int dz ) {
            for ( Point point : points ) {
                point.translate( dx, dy, dz );
            }
        }

        private long[] buildPointDiffs( Point start, Point[] all ) {
            long[] result = new long[all.length - 1];
            int index = 0;
            for ( Point point : all ) {
                if ( point == start ) {
                    continue;
                }
                result[index++] = Point.diff( point, start );
            }
            Arrays.sort( result );
            return result;
        }

        private Map<Point, long[]> buildAllDiffs() {
            IdentityHashMap<Point, long[]> result = new IdentityHashMap<>();
            for ( Point point : points ) {
                result.put( point, buildPointDiffs( point, points ) );
            }
            return result;
        }
    }

    private final String name;

    private final List<Point> scannerPositions = new ArrayList<>();

    public static void main( String[] args ) {
        try {
            executeTasks( "input/2021/Y21D19S1.dat", 79L, 3621L );
            executeTasks( "input/2021/Y21D19I.dat", 496L, null );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String fileName, Long expected1, Long expected2 ) {
        Utils.executeDay(
                new Day19Y21( fileName ),
                expected1,
                expected2
        );
    }

    public Day19Y21( String file ) {
        this.name = file;
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        Iterator<String> lines = Utils.readLines( name ).iterator();
        List<ScannerData> scanners = new ArrayList<>();
        while ( lines.hasNext() ) {
            scanners.add( loadScannerData( lines ) );
        }
        Set<Long> beacons = new TreeSet<>();
        addAllBeacons( beacons, scanners.get( 0 ) );
        Queue<ScannerData> aligned = new ArrayDeque<>();
        aligned.add( scanners.get( 0 ) );
        List<ScannerData> unaligned = new LinkedList<>( scanners );
        while ( !aligned.isEmpty() && !unaligned.isEmpty() ) {
            ScannerData src = aligned.poll();
            Map<Point, long[]> srcDiffs = src.buildAllDiffs();
            Iterator<ScannerData> dstIterator = unaligned.iterator();
            while ( dstIterator.hasNext() ) {
                ScannerData dst = dstIterator.next();
                PointsMatchResult match = matchScanners( srcDiffs, dst );
                if ( match == null ) {
                    continue;
                }
                int dx = match.srcPoint.x - match.dstPoint.x;
                int dy = match.srcPoint.y - match.dstPoint.y;
                int dz = match.srcPoint.z - match.dstPoint.z;
                dst.translate( dx, dy, dz );
                addAllBeacons( beacons, dst );
                dstIterator.remove();
                aligned.offer( dst );
                scannerPositions.add( new Point( dx, dy, dz ) );
            }
        }
        if ( !unaligned.isEmpty() ) {
            throw new IllegalStateException( "Not all scanners matched" );
        }
        return (long) beacons.size();
    }

    public Long task2() {
        long result = 0;
        int scannersCount = scannerPositions.size();
        for ( int index1 = 0; index1 < scannersCount - 1; ++index1 ) {
            for ( int index2 = index1; index2 < scannersCount; ++index2 ) {
                long distance = Point.distance( scannerPositions.get( index1 ), scannerPositions.get( index2 ) );
                if ( result < distance ) {
                    result = distance;
                }
            }
        }
        return result;
    }

    private ScannerData loadScannerData( Iterator<String> data ) {
        List<Point> points = new ArrayList<>();
        data.next();
        String line;
        do {
            line = data.hasNext() ? data.next() : "";
            if ( !line.isEmpty() ) {
                points.add( Point.valueOf( line ) );
            }
        } while ( !line.isEmpty() );
        return new ScannerData( points );
    }

    private PointsMatchResult matchScanners( Map<Point, long[]> srcPatterns, ScannerData dest ) {
        for ( int rx = 0; rx < 4; ++rx ) {
            for ( int ry = 0; ry < 4; ++ry ) {
                for ( int rz = 0; rz < 4; ++rz ) {
                    Map<Point, long[]> dstPatterns = dest.buildAllDiffs();
                    for ( var srcEntry : srcPatterns.entrySet() ) {
                        for ( var dstEntry : dstPatterns.entrySet() ) {
                            if ( matchPoint( srcEntry.getValue(), dstEntry.getValue() ) ) {
                                return new PointsMatchResult(
                                        srcEntry.getKey(),
                                        dstEntry.getKey(),
                                        dstPatterns
                                );
                            }
                        }
                    }
                    dest.rotateZ();
                }
                dest.rotateY();
            }
            dest.rotateX();
        }
        return null;
    }

    private boolean matchPoint( long[] srcPattern, long[] dstPattern ) {
        int srcIndex = 0;
        int dstIndex = 0;
        int count = 0;
        while ( count < 11 && srcIndex < srcPattern.length && dstIndex < dstPattern.length ) {
            if ( srcPattern[srcIndex] < dstPattern[dstIndex] ) {
                ++srcIndex;
            } else if ( srcPattern[srcIndex] > dstPattern[dstIndex] ) {
                ++dstIndex;
            } else {
                ++srcIndex;
                ++dstIndex;
                ++count;
            }
        }
        return count == 11;
    }

    private void addAllBeacons( Set<Long> beacons, ScannerData scanner ) {
        for ( Point point : scanner.points ) {
            beacons.add( point.packed() );
        }
    }
}
