

import akka.actor.UntypedActor;
import akka.actor.ActorRef;

/**
* The Actor for the Body Scanner.
* Code is pretty much the same as the Baggage Scanner.
*/
public class BodyScanner extends UntypedActor {

  private final int lineNumber;
  private final ActorRef station;

  //Saves us from needing to recreate the message every time
  private final BodyReady READY = new BodyReady();

  public BodyScanner(int line, ActorRef securityStation) {
    lineNumber = line;
    station = securityStation;
  }

  public void onReceive(Object message) {
    if(message instanceof Passenger) {
      onReceive((Passenger)message);
    } else if(message instanceof StopMessage){
      onReceive((StopMessage)message);
    }
  }

  private void onReceive(StopMessage killCommand){
      System.out.println(getSelf().path().name()+" has received kill command from " + getSender().path().name());
      System.out.println(getSelf().path().name()+ " is sending kill command to " + station.path().name());
      station.tell(killCommand, getSelf());
      this.getContext().stop(getSelf());
  }

  public void onReceive(Passenger passenger) {
    System.out.println("Passenger " +passenger.getName() + " has arrived at " + getSelf().path().name());

    //Check whether the passenger passes the security check.
    ScanReport results = new ScanReport(passenger, (Math.random()*5 < 4));
    if (results.equals(true)){
      System.out.println("Passenger " + passenger.getName() + " has passed his body check.");
    }
    else{
      System.out.println("Passenger " + passenger.getName() + " has failed his body check.");
    }
    station.tell(results,getSelf());

    //Let the line know that the scanner is ready.
    getSender().tell(READY, getSelf());
  }
}
