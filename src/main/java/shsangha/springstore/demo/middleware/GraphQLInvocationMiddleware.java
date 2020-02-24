package shsangha.springstore.demo.middleware;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.spring.web.servlet.GraphQLInvocation;
import graphql.spring.web.servlet.GraphQLInvocationData;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;
import graphql.ExecutionInput;
import shsangha.springstore.demo.services.JWTUtilsService;
import shsangha.springstore.demo.services.TwoCookieAuthApproachHelperService;
import java.net.HttpCookie;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Component
@Primary
public class GraphQLInvocationMiddleware implements GraphQLInvocation  {



    private GraphQL graphQL;
    private JWTUtilsService jwtUtilsService;
    private TwoCookieAuthApproachHelperService twoCookieAuthApproachHelperService;

    public GraphQLInvocationMiddleware(GraphQL graphQL, JWTUtilsService jwtUtilsService, TwoCookieAuthApproachHelperService twoCookieAuthApproachHelperService) {
        this.graphQL = graphQL;
        this.jwtUtilsService = jwtUtilsService;
        this.twoCookieAuthApproachHelperService = twoCookieAuthApproachHelperService;
    }

    @Override
    public CompletableFuture<ExecutionResult> invoke(GraphQLInvocationData graphQLInvocationData, WebRequest webRequest) {


        Map<String,Object>  context = new HashMap<>();



        String cookies = webRequest.getHeader("cookie");
        if(cookies !=null){

            String[] cookieArray = cookies.split(" ");

            Map<String,String> httpCookies = new HashMap<>();

            for (String cookie : cookieArray) {
                HttpCookie cookieAtIndex = HttpCookie.parse(cookie).get(0);
                    httpCookies.put(cookieAtIndex.getName(),cookieAtIndex.getValue());
            }


            String headerPayloadTokenValues = httpCookies.get("payload");
            String httpOnlyTokenValue = httpCookies.get("hiddenSecret");

            if(headerPayloadTokenValues != null && httpOnlyTokenValue !=null) {

                String fullToken = twoCookieAuthApproachHelperService.concatToken(headerPayloadTokenValues,httpOnlyTokenValue);

                if(jwtUtilsService.tokenValid(fullToken)){
                        context.put("userId", jwtUtilsService.extractTokenSubject(fullToken));
                        context.put("isAdmin",  jwtUtilsService.extractTokenIsAdmin(fullToken));
                }
            }
        }

        System.out.println(context);

        ExecutionInput input = ExecutionInput.newExecutionInput().query(graphQLInvocationData.getQuery())
                .operationName(graphQLInvocationData.getOperationName())
                .variables(graphQLInvocationData.getVariables())
                .context(context)
                .build();


        return graphQL.executeAsync(input);

    }
}
