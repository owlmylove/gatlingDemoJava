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
                   rampUsersPerSec(1).to(3).during(10).randomized()
    ).protocols(httpProtocol)
        ).maxDuration(60);
    }
}
