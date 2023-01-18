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

    private ScenarioBuilder scn = scenario("Get all games")
            .exec(http("Get all games")
                    .get("/videogame")
                    .check(status().in(200,210)));

    {
        setUp(
           scn.injectOpen(
                   nothingFor(5),
                   rampUsersPerSec(1).to(3).during(10).randomized()
           )
    ).protocols(httpProtocol);
    }
}
