package org.sdu.bachelor;

import graphql.language.StringValue;
import graphql.schema.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import java.time.ZonedDateTime;

@Configuration
public class GraphQLConfig {
    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder.scalar(zonedDateTimeScalar);
    }

    private static GraphQLScalarType zonedDateTimeScalar = GraphQLScalarType.newScalar()
            .name("ZonedDateTime")
            .description("Custom scalar type representing ZonedDateTime")
            .coercing(new Coercing<ZonedDateTime, String>() {
                @Override
                public String serialize(Object input) {
                    if (input instanceof ZonedDateTime) {
                        return ((ZonedDateTime) input).toString();
                    }
                    throw new CoercingSerializeException("Invalid ZonedDateTime value: " + input);
                }

                @Override
                public ZonedDateTime parseValue(Object input) {
                    if (input instanceof String) {
                        return ZonedDateTime.parse((String) input);
                    }
                    throw new CoercingParseValueException("Invalid ZonedDateTime value: " + input);
                }

                @Override
                public ZonedDateTime parseLiteral(Object input) {
                    if (input instanceof StringValue) {
                        return ZonedDateTime.parse(((StringValue) input).getValue());
                    }
                    throw new CoercingParseLiteralException("Invalid ZonedDateTime value: " + input);
                }
            })
            .build();
}
