package mutlu.ticketingapp.common;

public enum VehicleType {
    PLANE(189),
    BUS(45);

    private final int capacity;
    VehicleType(int capacity) {
        this.capacity = capacity;
    }

    public int getCapacity(){
        return this.capacity;
    }
}
