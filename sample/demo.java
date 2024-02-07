///usr/bin/env jbang "$0" "$@" ; exit $?
//REPOS central,jitpack
//DEPS com.github.maxandersen:github-java:main-SNAPSHOT

import static java.lang.System.*;

import java.util.Optional;

import com.github.api.GitHubClient;
import com.github.api.models.BasicError;

import io.kiota.http.vertx.VertXRequestAdapter;
import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientSession;

public class demo {

    static Optional<String> token(String args[]) {
        if (args!=null && args.length>0) {
            return Optional.ofNullable(args[0]);
        } else {
            return Optional.ofNullable(System.getenv("GITHUB_TOKEN"));
        }
    }

    public static void main(String... args) {
        
        var webSession = WebClientSession
                .create(WebClient.create(Vertx.vertx()));
        
        token(args).ifPresent(x -> webSession.addHeader("Authorization", "Bearer " + x));
        
        var github = new GitHubClient(new VertXRequestAdapter(webSession));
        
         try {
            github.repositories().get().forEach(repo -> {
                System.out.println("%s/%s %s".formatted(repo.getOwner().getName(), repo.getName(), repo.getStargazersCount()));
            });
        } catch (BasicError err) {
            System.out.println(err);
            System.out.println("Error: %s Status: %s".formatted(err, err.getResponseStatusCode()));
        }

        System.exit(0);

    }
}
