package videogamedb.feeders;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class Feeders extends Simulation {

    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://www.videogamedb.uk/api")
            .contentTypeHeader("application/json")
            .acceptHeader("application/json");

    private static Iterator<Map<String, Object>> customFeeder =
            Stream.generate((Supplier<Map<String, Object>>) () -> {
                Random rand = new Random();
                int gameId = rand.nextInt(10-1+1) +1;
                return Collections.singletonMap("gameId",gameId);
                    }
            ).iterator();
    private static ChainBuilder getSpecificGames =
            feed(customFeeder)
                    .exec(http("Get a specific game Name - #{gameId}")
                    .get("/videogame/#{gameId}")
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
