package shsangha.springstore.demo.middleware;
import graphql.ExecutionResult;
import graphql.spring.web.servlet.ExecutionResultHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.ResponseEntity;
import shsangha.springstore.demo.services.JWTUtilsService;
import shsangha.springstore.demo.services.TwoCookieAuthApproachHelperService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


@Configuration
@Primary
public class GraphQLExecutionAfterware implements ExecutionResultHandler {


    private JWTUtilsService jwtUtilsService;
    private TwoCookieAuthApproachHelperService twoCookieAuthApproachHelperService;

    public GraphQLExecutionAfterware(JWTUtilsService jwtUtilsService, TwoCookieAuthApproachHelperService twoCookieAuthApproachHelperService) {
        this.jwtUtilsService = jwtUtilsService;
        this.twoCookieAuthApproachHelperService = twoCookieAuthApproachHelperService;
    }

    @Override
    public Object handleExecutionResult(CompletableFuture<ExecutionResult> completableFuture) {

        try {

            Map<String, Object> result = completableFuture.get().toSpecification();

            Map<String,Object> data = (Map<String, Object>) result.get("data");

            if(data != null) {

                Map<String,Object> login = (Map<String, Object>) data.get("login");
                Map<String,Object> signup = (Map<String, Object>) data.get("signup");

                Map<String,Object> authMethod = new HashMap<>();

                if(login !=null){
                   authMethod = login;
                }
                if(signup !=null){
                    authMethod = signup;
                }

                if(authMethod.size() > 0) {

                    return ResponseEntity.ok()
                            .headers(twoCookieAuthApproachHelperService.generateHeaders
                                    (jwtUtilsService.generateToken(authMethod)))
                                    .body(result);
                }
            }

            return result;

        }catch (Exception e){

            // need to add custom graphql error here
            System.out.println(e);

        }
        return null;
    }
}
