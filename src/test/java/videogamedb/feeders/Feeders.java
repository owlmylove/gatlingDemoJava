package videogamedb.feeders;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class Feeders extends Simulation {

    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://www.videogamedb.uk/api")
            .contentTypeHeader("application/json")
            .acceptHeader("application/json");

    private static FeederBuilder.FileBased<Object> jsonFile = jsonFile("data/gameJsonFile.json").circular();
    private static ChainBuilder getSpecificGames =
            feed(jsonFile)
                    .exec(http("Get a specific game Name - #{name}")
                    .get("/videogame/#{id}")
                            .check(jmesPath("name").isEL("#{name}"))
                            .check(status().in(200, 210)));

    private ScenarioBuilder scn = scenario("Main")
            .repeat(10).on(
            exec(getSpecificGames)
                    .pause(1)
            );

    {
        setUp(
                scn.injectOpen(atOnceUsers(1))
        ).protocols(httpProtocol);
    }

}
