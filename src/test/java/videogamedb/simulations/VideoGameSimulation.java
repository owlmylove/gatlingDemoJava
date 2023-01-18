package videogamedb.simulations;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class VideoGameSimulation extends Simulation {

    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://www.videogamedb.uk/api")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    private static final int USER_COUNT = Integer.parseInt(System.getProperty("USERS", "5"));
    private static final int RAMP_DURATION = Integer.parseInt(System.getProperty("RAMP_DURATION", "10"));
    private static final int TEST_DURATION = Integer.parseInt(System.getProperty("TEST_DURATION", "20"));

    @Override
    public void before(){
        System.out.printf("Running test with %d users%n", USER_COUNT);
        System.out.printf("Running users over %d seconds%n", RAMP_DURATION);
        System.out.printf("Total test duration: %d seconds%n", TEST_DURATION);

    }


    private static ChainBuilder getAllGames =
            exec(http("Get all games")
                    .get("/videogame"));

    private ScenarioBuilder scn = scenario("Get all games")
            .forever().on(
                    exec(getAllGames)
            );

    {
        setUp(
           scn.injectOpen(
                   nothingFor(5),
                   rampUsers(USER_COUNT).during(RAMP_DURATION)
    ).protocols(httpProtocol)
        ).maxDuration(TEST_DURATION);
    }
}

// Run locally in terminal
// mvn gatling:test -Dgatling.simulationClass=videogamedb.simulations.VideoGameSimulation
// Locally with test parameters
// mvn gatling:test -Dgatling.simulationClass=videogamedb.simulations.VideoGameSimulation -DUSERS=10 -DRAMP_DURATION=15 -DTEST_DURATION=30
