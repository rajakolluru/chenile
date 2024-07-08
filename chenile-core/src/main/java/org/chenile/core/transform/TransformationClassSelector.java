package org.chenile.core.transform;

import com.fasterxml.jackson.core.type.TypeReference;
import org.chenile.base.exception.ServerException;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.errorcodes.ErrorCodes;
import org.chenile.core.interceptors.BaseChenileInterceptor;
import org.chenile.core.model.ChenileServiceDefinition;
import org.chenile.core.model.OperationDefinition;
import org.chenile.owiz.Command;

import java.lang.reflect.Type;

/**
 * Determines the target type for the body of the ChenileExchange and populates that into
 * {@link ChenileExchange#getBodyType()}
 * Uses registered bodyTypeSelectors either at {@link ChenileServiceDefinition} or {@link OperationDefinition}
 * @author Raja Shankar Kolluru
 */

public class TransformationClassSelector extends BaseChenileInterceptor {

    @Override
    public void doPreProcessing(ChenileExchange exchange) {
        // Has a body type selector been registered against the service definition or operation definition
        // if so invoke it
        Command<ChenileExchange> bts = obtainBodyTypeSelector(exchange);
        if (bts != null) {
            try {
                bts.execute(exchange);
            } catch (Exception e) {
                throw new ServerException(ErrorCodes.BODY_TYPE_SELECTOR_ERROR.getSubError(), new Object[]{});
            }
        } else
            setDefaultBodyType(exchange);
    }

    private Command<ChenileExchange> obtainBodyTypeSelector(ChenileExchange exchange) {
        ChenileServiceDefinition sd = exchange.getServiceDefinition();
        OperationDefinition od = exchange.getOperationDefinition();
        if (od.getBodyTypeSelector() != null)
            return od.getBodyTypeSelector();
        else
            return sd.getBodyTypeSelector();
    }

    private void setDefaultBodyType(ChenileExchange exchange) {
        OperationDefinition od = exchange.getOperationDefinition();
        if (od.getInput() != null) {
            TypeReference<?> ref = convertClassToTypeReference(od.getInput());
            exchange.setBodyType(ref);
        }
    }

    public static TypeReference<?> convertClassToTypeReference(Class<?> clazz) {
        return new TypeReference<Object>() {
            @Override
            public Type getType() {
                return clazz;
            }
        };
    }
}
