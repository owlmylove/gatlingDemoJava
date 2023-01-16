package videogamedb.scriptfundamentals;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.time.Duration;
import java.util.List;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class VideoGameDb extends Simulation {

    private static ChainBuilder getAllVideoGames =
            exec(http("Get all videogames")
                    .get("/videogame")
                    .check(status().not(400), status().not(500)));

    private static ChainBuilder getSpecificGame =
            exec(http("Get a specific game")
                    .get("/videogame/1")
                    .check(status().in(200, 210)));

    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://www.videogamedb.uk/api")
            .acceptHeader("application/json");

    private ScenarioBuilder scn = scenario("First Scenario")

            .exec(getAllVideoGames)
            .pause(5)
            .exec(getSpecificGame)
            .pause(1, 5)
            .exec(getAllVideoGames);

    {
        setUp(
                scn.injectOpen(atOnceUsers(1))
        ).protocols(httpProtocol);
    }



}
