import akka.actor.*;
import akka.remote.EndpointManager;

import java.util.ArrayList;

public class Driver{
    private static final int LINE_COUNT = 3;
    private static final int PASSENGERS = 10;

    private static final ArrayList<ActorRef> Queues = new ArrayList<>();
    private static final ArrayList<ActorRef> Bags = new ArrayList<>();
    private static final ArrayList<ActorRef> Bodies = new ArrayList<>();
    private static final ArrayList<ActorRef> SecStations = new ArrayList<>();
    private static final ArrayList<Passenger> Passengers = new ArrayList<>();

    public static void main(String[] args){


        final ActorSystem system = ActorSystem.create();
        //Initialize system to create ActorRef
        final ActorRef Jail =  system.actorOf(Props.create(Jail.class, LINE_COUNT), "Jail");
        //Create the jail ActorRef

        for (int i = 0; i < LINE_COUNT; i += 1){
            String ID = Integer.toString(i);
            SecStations.add(system.actorOf(Props.create(SecurityStation.class, i, Jail), "SS-" + ID));
            Bags.add(system.actorOf(Props.create(BaggageScanner.class, i, SecStations.get(i)), "BagScan-" + ID));
            Bodies.add(system.actorOf(Props.create(BodyScanner.class, i, SecStations.get(i)), "BodyScan-" + ID));
            Queues.add(system.actorOf(Props.create(ScanQueue.class, i,Bodies.get(i), Bags.get(i)), "Queue-" + ID));
        }
        final ActorRef DCheck = system.actorOf(Props.create(DocumentCheck.class, Queues), "DCheck");

        for (int i = 0; i < PASSENGERS; i+= 1){
            Passengers.add(new Passenger(Integer.toString(i)));
        }

        for(int i = 0; i < PASSENGERS; i += 1){
            DCheck.tell(Passengers.get(i), ActorRef.noSender());
            //print statement
            System.out.println("Passenger " + Passengers.get(i).getName() + " is being sent to the Document Check.");
        }
        StopMessage killCommand = new StopMessage();
        System.out.println("Sending Kill Command to Document Check");
        DCheck.tell(killCommand, ActorRef.noSender());

    }

}
