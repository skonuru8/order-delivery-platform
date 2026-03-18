package com.platform.main.shared.exception;

import graphql.GraphQLError;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

@Component
public class GraphQLExceptionHandler extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex,
                                                DataFetchingEnvironment env) {
        if (ex instanceof ProductNotFoundException) {
            return GraphQLError.newError()
                    .errorType(ErrorType.NOT_FOUND)
                    .message(ex.getMessage())
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .extensions(java.util.Map.of("errorCode", "PRODUCT_NOT_FOUND"))
                    .build();
        }

        if (ex instanceof OrderNotFoundException) {
            return GraphQLError.newError()
                    .errorType(ErrorType.NOT_FOUND)
                    .message(ex.getMessage())
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .extensions(java.util.Map.of("errorCode", "ORDER_NOT_FOUND"))
                    .build();
        }

        if (ex instanceof InsufficientStockException) {
            return GraphQLError.newError()
                    .errorType(ErrorType.BAD_REQUEST)
                    .message(ex.getMessage())
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .extensions(java.util.Map.of("errorCode", "INSUFFICIENT_STOCK"))
                    .build();
        }

        if (ex instanceof InvalidOrderStateException) {
            return GraphQLError.newError()
                    .errorType(ErrorType.BAD_REQUEST)
                    .message(ex.getMessage())
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .extensions(java.util.Map.of("errorCode", "INVALID_ORDER_STATE"))
                    .build();
        }

        if (ex instanceof UnauthorizedOrderAccessException) {
            return GraphQLError.newError()
                    .errorType(ErrorType.FORBIDDEN)
                    .message(ex.getMessage())
                    .path(env.getExecutionStepInfo().getPath())
                    .location(env.getField().getSourceLocation())
                    .extensions(java.util.Map.of("errorCode", "UNAUTHORIZED_ACCESS"))
                    .build();
        }

        // Unhandled exceptions — generic error, don't leak internals
        return GraphQLError.newError()
                .errorType(ErrorType.INTERNAL_ERROR)
                .message("An internal error occurred")
                .path(env.getExecutionStepInfo().getPath())
                .location(env.getField().getSourceLocation())
                .extensions(java.util.Map.of("errorCode", "INTERNAL_ERROR"))
                .build();
    }
}