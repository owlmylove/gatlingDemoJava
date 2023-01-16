package videogamedb.scriptfundamentals;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class VideoGameDb extends Simulation {

    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://www.videogamedb.uk/api")
            .acceptHeader("application/json");

    private ScenarioBuilder scn = scenario("First Scenario")
            .exec(http("Get all Games - 1st call")
                    .get("/videogame")
                    .check(status().is(200))
                    .check(jsonPath("$[?(@.id==1)].name").is("Resident Evil 4")))
            .pause("5")

            .exec(http("Get a specific game - 2")
                    .get("/videogame/2")
                    .check(status().in(200,201,204)))
            .pause(1,5)

            .exec(http("Get all Games - 2d call")
                    .get("/videogame")
                    .check(status().not(404), status().not(500)))
            .pause(Duration.ofMillis(4000));

    {
        setUp(
                scn.injectOpen(atOnceUsers(1))
        ).protocols(httpProtocol);
    }



}
