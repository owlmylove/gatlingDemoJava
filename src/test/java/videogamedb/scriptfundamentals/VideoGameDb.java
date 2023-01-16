package videogamedb.scriptfundamentals;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import net.sf.saxon.om.Chain;

import java.time.Duration;
import java.util.List;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class VideoGameDb extends Simulation {

    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://www.videogamedb.uk/api")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    private static ChainBuilder authenticate =
            exec(http("Authentication")
            .post("/authenticate")
            .body(StringBody("{\n" +
                    "  \"password\": \"admin\",\n" +
                    "  \"username\": \"admin\"\n" +
                    "}"))
            .check(jmesPath("token").saveAs("jwtToken"))
            .check(status().is(200)));

    private static ChainBuilder createNewGame =
            exec(http("Create new game")
                    .post("/videogame")
                    .header("Authorization", "Bearer #{jwtToken}")
                    .body(StringBody("{\n" +
                            "  \"category\": \"Platform\",\n" +
                            "  \"name\": \"Mario\",\n" +
                            "  \"rating\": \"Mature\",\n" +
                            "  \"releaseDate\": \"2012-05-04\",\n" +
                            "  \"reviewScore\": 85\n" +
                            "}"))
                    .check(status().is(200)));

    private ScenarioBuilder scn = scenario("First Scenario")
            .exec(authenticate)
            .exec(createNewGame);
    {
        setUp(
                scn.injectOpen(atOnceUsers(1))
        ).protocols(httpProtocol);
    }



}
