package org.chenile.stm.impl;

import org.chenile.stm.STMFlowStore;
import org.chenile.stm.model.AutomaticStateDescriptor;
import org.chenile.stm.model.StateDescriptor;
import org.chenile.stm.model.Transition;

import java.util.HashMap;
import java.util.Map;

/**
 * Generates a PLANT UML state diagram for the STate Transition Diagram
 */
public class STMPlantUmlSDGenerator {


    private STMFlowStore stmFlowStore ;
    public STMPlantUmlSDGenerator(STMFlowStore flowStore){
        this.stmFlowStore = flowStore;
        findIncomingForAllStates();
    }
    private static final String STARTUML = "@startuml\n";
    private static final String ENDUML = "@enduml\n";
    public static final String STATE = "state ";
    private static final String AUTO_STATE = " <<choice>> ";
    public static final String NOTE_RIGHT_OF_ = "note right of ";
    private static final String TERMINAL = "[*]";

    public String toStateDiagram(){
        StringBuilder stringBuilder = new StringBuilder(STARTUML);

        stringBuilder.append("skinparam state {\n").
            append("BackgroundColor<<orphaned>> Orange\n").
            append("}\n");
        for(StateDescriptor sd: stmFlowStore.getAllStates()) {
            stringBuilder.append(STATE).append(sd.getId());
            if (!notOrphaned.get(sd.getId())){
                stringBuilder.append(" <<orphaned>> ").append("\n");
            }
            if (!sd.isManualState()){
                stringBuilder.append(AUTO_STATE).append("\n");
                stringBuilder.append(NOTE_RIGHT_OF_).append(sd.getId())
                        .append(" : **").append(sd.getId()).append("**");
                Map<String, String> metadata = sd.getMetadata();
                if (metadata != null && !metadata.isEmpty()){
                    for (Map.Entry<String,String> md: metadata.entrySet()){
                        stringBuilder.append("\\n").append(md.getKey()).append(":").append(md.getValue());
                    }
                }
                printComponentProperties(stringBuilder,sd);
            }
            stringBuilder.append("\n");
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
            StringBuilder selfNote = new StringBuilder();
            for(Transition t: sd.getTransitions().values()){
                if(t.getNewStateId().equals(sd.getId())){
                    selfNote.append(t.getEventId()).append("\\n");
                }else {
                    stringBuilder.append(sd.getId()).append(" --> ").
                            append(t.getNewStateId())
                            .append(" : ")
                            .append(t.getEventId())
                            .append("\n");
                }
            }
            if (!selfNote.isEmpty()){
                stringBuilder.append(sd.getId()).append(" --> ").append(sd.getId()).append("\n");
                stringBuilder.append("note on link : ").append(selfNote.toString()).append("\n");
            }
        }
        stringBuilder.append(ENDUML);
        return stringBuilder.toString();
    }

    Map<String,Boolean> notOrphaned = new HashMap<>();
    private void findIncomingForAllStates(){

        for(StateDescriptor sd: stmFlowStore.getAllStates()) {
            if (sd.isInitialState())
                notOrphaned.put(sd.getId(),true);
            else
                notOrphaned.put(sd.getId(),false);
        }
        for(StateDescriptor sd: stmFlowStore.getAllStates()) {
            for(Transition t: sd.getTransitions().values()){
                if (!t.getNewStateId().equals(sd.getId())){
                    notOrphaned.put(t.getNewStateId(),true);
                }
            }
        }
    }

    private void printComponentProperties(StringBuilder stringBuilder, StateDescriptor sd){
        if( sd instanceof AutomaticStateDescriptor asd){
            Map<String, Object> props = asd.getComponentProperties();
            for (Map.Entry<String,Object> prop: props.entrySet()){
                stringBuilder.append("\\n**").append(prop.getKey()).append(":**").append(prop.getValue());
            }
        }
    }
}
