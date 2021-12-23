package aoc2021;

import common.AocDay;
import common.Utils;

import java.util.ArrayList;
import java.util.List;

public class Day23Y21 implements AocDay<Long, Long> {

    private static final int[] ENERGY = { 1, 10, 100, 1000 };
    private static final int[] ROOM_COORDS = { 2, 4, 6, 8 };

    private static record Amphipod(int type, int x, int depth) {
        public Amphipod( Amphipod pod ) {
            this( pod.type, pod.x, pod.depth );
        }
    }

    private static class Room {
        private final int depth;
        private final int type;
        private final List<Amphipod> habits;

        public Room( int type, int top, int bottom ) {
            this.depth = 2;
            habits = new ArrayList<>( 2 );
            this.type = type;
            habits.add( new Amphipod( bottom, ROOM_COORDS[type], 2 ) );
            habits.add( new Amphipod( top, ROOM_COORDS[type], 1 ) );
        }

        public Room( int type, int top, int mid1, int mid2, int bottom ) {
            this.depth = 4;
            habits = new ArrayList<>( 4 );
            this.type = type;
            habits.add( new Amphipod( bottom, ROOM_COORDS[type], 4 ) );
            habits.add( new Amphipod( mid2, ROOM_COORDS[type], 3 ) );
            habits.add( new Amphipod( mid1, ROOM_COORDS[type], 2 ) );
            habits.add( new Amphipod( top, ROOM_COORDS[type], 1 ) );
        }

        public Room( Room saved ) {
            this.depth = saved.depth;
            habits = new ArrayList<>( this.depth );
            this.type = saved.type;
            for ( Amphipod pod : saved.habits ) {
                this.habits.add( new Amphipod( pod ) );
            }
        }

        private boolean isFull() {
            return habits.size() == depth;
        }

        private boolean needPop() {
            return habits.stream().anyMatch( a -> a.type != type );
        }

        private Amphipod pop() {
            return habits.remove( habits.size() - 1 );
        }

        public int push() {
            int roomSteps = depth - habits.size();
            habits.add( new Amphipod( type, ROOM_COORDS[type], roomSteps ) );
            return roomSteps;
        }

        private boolean canAccept() {
            return habits.isEmpty() || habits.stream().allMatch( a -> a.type == type );
        }

        private char getPodChar( int podDepth ) {
            int index = depth - podDepth;
            if ( index >= habits.size() ) {
                return '.';
            } else {
                return (char) ( 'A' + habits.get( index ).type );
            }
        }

    }

    private static class State {
        private final Room[] rooms;
        private Amphipod moving;
        private int direction = 0;
        private int energy = 0;
        private final Amphipod[] hall = new Amphipod[11];

        public State( String setup, int depth ) {
            if ( depth == 2 ) {
                this.rooms = new Room[]{
                        new Room( 0, setup.charAt( 0 ) - 'A', setup.charAt( 1 ) - 'A' ),
                        new Room( 1, setup.charAt( 2 ) - 'A', setup.charAt( 3 ) - 'A' ),
                        new Room( 2, setup.charAt( 4 ) - 'A', setup.charAt( 5 ) - 'A' ),
                        new Room( 3, setup.charAt( 6 ) - 'A', setup.charAt( 7 ) - 'A' )
                };
            } else {
                this.rooms = new Room[]{
                        new Room( 0, setup.charAt( 0 ) - 'A', 3, 3, setup.charAt( 1 ) - 'A' ),
                        new Room( 1, setup.charAt( 2 ) - 'A', 2, 1, setup.charAt( 3 ) - 'A' ),
                        new Room( 2, setup.charAt( 4 ) - 'A', 1, 0, setup.charAt( 5 ) - 'A' ),
                        new Room( 3, setup.charAt( 6 ) - 'A', 0, 2, setup.charAt( 7 ) - 'A' )
                };
            }
        }

        public State( State saved ) {
            this.rooms = new Room[]{
                    new Room( saved.rooms[0] ),
                    new Room( saved.rooms[1] ),
                    new Room( saved.rooms[2] ),
                    new Room( saved.rooms[3] )
            };
            for ( int index = 0; index < hall.length; ++index ) {
                Amphipod pod = saved.hall[index];
                this.hall[index] = pod == null ? null : new Amphipod( pod );
                if ( saved.moving == pod ) {
                    this.moving = this.hall[index];
                }
            }
            this.direction = saved.direction;
            this.energy = saved.energy;
        }

        private boolean isFulfilled() {
            for ( Room room : rooms ) {
                if ( !room.isFull() ) {
                    return false;
                }
            }
            return true;
        }

        private long getFullEnergy() {
            return energy;
        }

        private boolean canMove() {
            if ( moving == null ) {
                return false;
            }
            int x = moving.x + direction;
            for ( int room : ROOM_COORDS ) {
                if ( x == room ) {
                    x += direction;
                    break;
                }
            }
            return x >= 0 && x < hall.length && hall[x] == null;
        }

        private boolean canPop( int roomIndex, int dir ) {
            return rooms[roomIndex].needPop() && hall[ROOM_COORDS[roomIndex] + dir] == null;
        }

        private boolean canPush( int hallCoord ) {
            Amphipod pod = hall[hallCoord];
            if ( pod == null ) {
                return false;
            }
            if ( !rooms[pod.type].canAccept() ) {
                return false;
            }
            int targetX = ROOM_COORDS[pod.type];
            int dir = Integer.compare( targetX, hallCoord );
            for ( int x = hallCoord + dir; x != targetX; x += dir ) {
                if ( hall[x] != null ) {
                    return false;
                }
            }
            return true;
        }

        private void move() {
            int x = moving.x + direction;
            int cost = 1;
            for ( int room : ROOM_COORDS ) {
                if ( x == room ) {
                    x += direction;
                    ++cost;
                    break;
                }
            }
            hall[moving.x] = null;
            hall[x] = moving = new Amphipod( moving.type, x, 0 );
            energy += ENERGY[moving.type] * cost;
        }

        private void pop( int roomIndex, int dir ) {
            Amphipod pod = rooms[roomIndex].pop();
            int x = ROOM_COORDS[roomIndex] + dir;
            hall[x] = moving = new Amphipod( pod.type, x, 0 );
            direction = dir;
            energy += ENERGY[moving.type] * ( pod.depth + 1 );
        }

        private void push( int hallCoord ) {
            Amphipod pod = hall[hallCoord];
            hall[hallCoord] = null;
            moving = null;
            int hallSteps = Math.abs( ROOM_COORDS[pod.type] - hallCoord );
            int roomSteps = rooms[pod.type].push();
            energy += ENERGY[pod.type] * ( hallSteps + roomSteps );
        }

        @Override
        public String toString() {
            StringBuilder result = new StringBuilder( "#############\n#" );
            for ( int hallCoord = 0; hallCoord < 11; ++hallCoord ) {
                result.append( hall[hallCoord] == null ? '.' : (char) ( 'A' + hall[hallCoord].type ) );
            }
            result.append( "#\n###" );
            for ( int room = 0; room < 4; ++room ) {
                result.append( rooms[room].getPodChar( 1 ) ).append( '#' );
            }
            result.append( "##\n  #" );
            for ( int room = 0; room < 4; ++room ) {
                result.append( rooms[room].getPodChar( 2 ) ).append( '#' );
            }
            result.append( "  \n  #########  " );
            return result.toString();
        }
    }

    private final String name;


    public static void main( String[] args ) {
        try {
            executeTasks( "BACDBCDA", 12521L, 44169L );
            executeTasks( "ABCDCADB", 13066L, 47328L );
        } catch ( Throwable e ) {
            e.printStackTrace();
        }
    }

    public static void executeTasks( String combination, Long expected1, Long expected2 ) {
        Utils.executeDay(
                new Day23Y21( combination ),
                expected1,
                expected2
        );
    }

    public Day23Y21( String file ) {
        this.name = file;
    }

    @Override
    public String sampleName() {
        return name;
    }

    public Long task1() {
        State state = new State( name, 2 );
        return doMove( state, Long.MAX_VALUE );
    }

    public Long task2() {
        State state = new State( name, 4 );
        return doMove( state, Long.MAX_VALUE );
    }

    private long doMove( State state, long min ) {
        if ( state.getFullEnergy() > min ) {
            return Long.MAX_VALUE;
        }
        long minEnergy = min;
        State newState;
        for ( int hallCoord = 0; hallCoord < 11; ++hallCoord ) {
            if ( state.canPush( hallCoord ) ) {
                newState = new State( state );
                newState.push( hallCoord );
                if ( newState.isFulfilled() ) {
                    return Math.min( minEnergy, newState.getFullEnergy() );
                } else {
                    minEnergy = Math.min( minEnergy, doMove( newState, minEnergy ) );
                }
            }
        }
        for ( int room = 0; room < 4; ++room ) {
            if ( state.canPop( room, 1 ) ) {
                newState = new State( state );
                newState.pop( room, 1 );
                minEnergy = Math.min( minEnergy, doMove( newState, minEnergy ) );
            }
            if ( state.canPop( room, -1 ) ) {
                newState = new State( state );
                newState.pop( room, -1 );
                minEnergy = Math.min( minEnergy, doMove( newState, minEnergy ) );
            }
        }
        if ( state.canMove() ) {
            newState = new State( state );
            newState.move();
            minEnergy = Math.min( minEnergy, doMove( newState, minEnergy ) );
        }
        return minEnergy;
    }
}
