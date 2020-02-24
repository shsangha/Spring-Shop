package shsangha.springstore.demo.config;

import com.coxautodev.graphql.tools.SchemaParser;
import graphql.GraphQL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import shsangha.springstore.demo.repository.UserRepository;
import shsangha.springstore.demo.resolver.*;

import shsangha.springstore.demo.service.AttendeeService;
import shsangha.springstore.demo.service.SpeakerService;
import shsangha.springstore.demo.service.TalkService;

@Configuration
public class GraphQLSchemaConfig {

    private AttendeeService attendeeService;
    private SpeakerService speakerService;
    private  TalkService talkService;
    private BCryptPasswordEncoder encoder;
    private UserRepository userRepository;

    public GraphQLSchemaConfig(AttendeeService attendeeService, SpeakerService speakerService, TalkService talkService, BCryptPasswordEncoder encoder, UserRepository userRepository) {
        this.attendeeService = attendeeService;
        this.speakerService = speakerService;
        this.talkService = talkService;
        this.encoder = encoder;
        this.userRepository = userRepository;
    }

    @Bean
    public GraphQL graphQL(){
        return new GraphQL.Builder(
                SchemaParser
                .newParser()
                .file("graphql/schema.graphqls")
                .resolvers(
                        new Query(attendeeService,speakerService,talkService),
                        new TalkResolver(speakerService),
                        new SpeakerResolver(talkService),
                        new Mutation(speakerService,userRepository,encoder),
                        new Subscription())
                .build()
                .makeExecutableSchema())
                .build();
    }
}
