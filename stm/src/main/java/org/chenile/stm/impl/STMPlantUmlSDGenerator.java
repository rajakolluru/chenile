package org.chenile.stm.impl;

import org.chenile.stm.STMFlowStore;
import org.chenile.stm.model.StateDescriptor;
import org.chenile.stm.model.Transition;

/**
 * Generates a PLANT UML state diagram for the STate Transition Diagram
 */
public class STMPlantUmlSDGenerator {


    private STMFlowStore stmFlowStore ;
    public STMPlantUmlSDGenerator(STMFlowStore flowStore){
        this.stmFlowStore = flowStore;
    }
    private static final String STARTUML = "@startuml\n";
    private static final String ENDUML = "@enduml\n";
    public static final String STATE = "state ";
    private static final String AUTO_STATE = " <<choice>> ";
    public static final String NOTE_RIGHT_OF_ = "note right of ";
    private static final String TERMINAL = "[*]";

    public String toStateDiagram(){
        StringBuilder stringBuilder = new StringBuilder(STARTUML);
        for(StateDescriptor sd: stmFlowStore.getAllStates()) {
            stringBuilder.append(STATE).append(sd.getId());
            if (!sd.isManualState()){
                stringBuilder.append(AUTO_STATE).append("\n");
                stringBuilder.append(NOTE_RIGHT_OF_).append(sd.getId())
                        .append(" : ").append(sd.getId()).append("\n");
            }
            else{
                stringBuilder.append("\n");
            }


        }
        for(StateDescriptor sd: stmFlowStore.getAllStates()) {
            if (sd.isInitialState()){
                stringBuilder.append(TERMINAL).append(" --> ").
                        append(sd.getId()).append("\n");
            }
            if (sd.getTransitions().isEmpty()){
               stringBuilder.append(sd.getId()).append(" --> ").
                       append(TERMINAL).append("\n");
            }
            for(Transition t: sd.getTransitions().values()){
                stringBuilder.append(sd.getId()).append(" --> ").
                        append(t.getNewStateId())
                        .append(" : ")
                        .append(t.getEventId())
                        .append("\n");
            }
        }
        stringBuilder.append(ENDUML);
        return stringBuilder.toString();
    }
}
