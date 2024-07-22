package org.chenile.http.test.subclass;

import org.chenile.core.context.ChenileExchange;
import org.chenile.core.transform.TransformationClassSelector;
import org.chenile.owiz.Command;

public class RoomVehicleBodyTypeSelector implements Command<ChenileExchange> {
    @Override
    public void execute(ChenileExchange exchange) throws Exception {
        String type = exchange.getHeader("type",String.class);
        switch (type){
            case "room":
                exchange.setBodyType(TransformationClassSelector.convertClassToTypeReference(Room.class));
                break;
            case "vehicle":
                exchange.setBodyType(TransformationClassSelector.convertClassToTypeReference(Vehicle.class));
                break;
        }
    }
}
