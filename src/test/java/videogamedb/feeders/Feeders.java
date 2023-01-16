package videogamedb.feeders;

import io.gatling.core.json.Json;
import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import jodd.util.RandomString;
import net.sf.saxon.om.Chain;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class Feeders extends Simulation {

    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://www.videogamedb.uk/api")
            .contentTypeHeader("application/json")
            .acceptHeader("application/json");

    public static LocalDate randomDate(){
        int hundredYears = 100 * 365;
        return LocalDate.ofEpochDay(ThreadLocalRandom.current().nextInt(-hundredYears, hundredYears));
    }

    private static Iterator<Map<String, Object>> customFeeder =
            Stream.generate((Supplier<Map<String, Object>>) () -> {
                Random rand = new Random();
                int gameId = rand.nextInt(1, 11);

                String gameName = RandomStringUtils.randomAlphanumeric(5) + "-gameName";
                String releaseDate = randomDate().toString();
                int reviewScore = rand.nextInt(100);
                String category = RandomStringUtils.randomAlphanumeric(5) + "-category";
                String rating = RandomStringUtils.randomAlphanumeric(4) + "-rating";

                HashMap<String, Object> hmap = new HashMap<String, Object>();
                hmap.put("gameId", gameId);
                hmap.put("gameName", gameName);
                hmap.put("releaseDate", releaseDate);
                hmap.put("reviewScore", reviewScore);
                hmap.put("category", category);
                hmap.put("rating", rating);
                return hmap;
            }
            ).iterator();
    private static ChainBuilder authenticate =
            exec(http("Authenticate")
                    .post("/authenticate")
                    .body(StringBody("{\n" +
                            "  \"password\": \"admin\",\n" +
                            "  \"username\": \"admin\"\n" +
                            "}"))
                    .check(jmesPath("token").saveAs("jwtToken"))
                    .check(status().in(200,210)))
                    .exec(session -> {
                        System.out.println(session.getString("jwtToken"));
                        return session;
                    });

    private static ChainBuilder createNewGame =
            feed(customFeeder)
                    .exec(http("Create a new game - #{gameName}")
                    .post("/videogame")
                    .header("authorization", "Bearer #{jwtToken}")
                    .body(ElFileBody("bodies/newGameTemplate.json")).asJson()
                    .check(bodyString().saveAs("responseBody")))
                    .exec(session -> {
                        System.out.println(session.getString("responseBody"));
                        return session;
                    }
                    );

    private ScenarioBuilder scn = scenario("Main")
            .exec(authenticate)
            .repeat(10).on(
                       exec(createNewGame));

    {
        setUp(
                scn.injectOpen(atOnceUsers(1))
        ).protocols(httpProtocol);
    }

}
