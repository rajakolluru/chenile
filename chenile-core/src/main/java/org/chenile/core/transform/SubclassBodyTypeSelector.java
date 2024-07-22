package org.chenile.core.transform;

import com.fasterxml.jackson.core.type.TypeReference;
import org.chenile.base.exception.BadRequestException;
import org.chenile.core.context.ChenileExchange;
import org.chenile.core.errorcodes.ErrorCodes;
import org.chenile.core.model.OperationDefinition;
import org.chenile.owiz.Command;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * SubclassBodyTypeSelector uses the Subclass registry to determine the appropriate subclass based on the type to distinguish
 * between subclasses. The type is expected to be at the top level in the JSON.
 */
public class SubclassBodyTypeSelector implements Command<ChenileExchange> {
    @Autowired
    SubclassRegistry subclassRegistry;
    @Override
    public void execute(ChenileExchange exchange) throws Exception {
        OperationDefinition od = exchange.getOperationDefinition();
        Class<?> bodyType = od.getInput();
        if (exchange.getBodyType() != null){
            TypeReference<?> ref = exchange.getBodyType();
            bodyType = (Class<?>)ref.getType();
        }
        if (!(exchange.getBody() instanceof String body)) return;

        try {
            Class<?> c = subclassRegistry.determineSubClass(body, bodyType);
            if (c != null) exchange.setBodyType(TransformationClassSelector.convertClassToTypeReference(c));
        }catch(Exception e){
            throw new BadRequestException(ErrorCodes.PAYLOAD_CANNOT_BE_PARSED.getSubError(),
                    new Object[] { body, e.getMessage()}, e);
            }

    }
}
