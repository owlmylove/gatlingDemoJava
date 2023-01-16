package videogamedb.scriptfundamentals;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.time.Duration;
import java.util.List;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class VideoGameDb extends Simulation {

    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://www.videogamedb.uk/api")
            .acceptHeader("application/json");

    private ScenarioBuilder scn = scenario("First Scenario")

            .exec(http("Get a specific game - 1")
                    .get("/videogame/1")
                    .check(status().in(200,201,204))
                    .check(jmesPath("name").is("Resident Evil 4")))
            .pause(1,10)

            .exec(http("Get all Games")
                    .get("/videogame")
                    .check(status().not(404), status().not(500))
                    .check(jmesPath("[1].id").saveAs("gameId")))
            .pause(Duration.ofMillis(4000))
            .exec(
                    session -> {
                        System.out.println(session);
                        System.out.println("gameId set to " + session.getString("gameId"));
                        return session;
                    }
            )

            .exec(http("Get a specific game - #{gameId}")
                    .get("/videogame/#{gameId}")
                    .check(jmesPath("name").is("Gran Turismo 3"))
                    .check(bodyString().saveAs("responseBody")))
            .exec(
                    session -> {
                        System.out.println("Response Body is " + session.getString("responseBody"));
                        return session;
                     }
                    );

    {
        setUp(
                scn.injectOpen(atOnceUsers(1))
        ).protocols(httpProtocol);
    }



}
