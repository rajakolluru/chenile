package org.chenile.stm.cli;

import org.chenile.stm.STMFlowStore;
import org.chenile.stm.State;
import org.chenile.stm.dummy.DummyStore;
import org.chenile.stm.impl.STMActionsInfoProvider;
import org.chenile.stm.impl.STMFlowStoreImpl;
import org.chenile.stm.impl.STMPlantUmlSDGenerator;
import org.chenile.stm.impl.XmlFlowReader;
import picocli.CommandLine;
import static picocli.CommandLine.*;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

@Command(name = "stm-cli", mixinStandardHelpOptions = true, version = "stm-cli 1.0",
        description = "Reads a State Definition file and allows a few operations on it. STM is not created")
public class CLI implements Runnable {
    @Parameters(index = "0", description = "The XML filename to read.")
    private File file;
    @Option(names = {"-s", "--uml-state-diagram"}, description = "Generate a UML state diagram")
    private boolean umlStateDiagram;
    @Option(names = {"-a", "--allowed-actions"}, description = "Return allowed actions for a state")
    private String stateForAllowedActions;
    @Spec
    Model.CommandSpec spec;

    public static void main(String... args) {
        System.exit(new CommandLine(new CLI()).execute(args));
    }

    @Override
    public void run() {
        try {
            if (umlStateDiagram) {
                renderStateDiagram();
            } else if (stateForAllowedActions != null && !stateForAllowedActions.isEmpty()) {
                allowedActions();
            } else {
                System.err.println("Missing option: at least one of the " +
                        "-s or -a options must be specified");
                spec.commandLine().usage(System.err);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void allowedActions() throws Exception {
        try (InputStream inputStream = Files.newInputStream(file.toPath())) {
            StoreProcessors sp = processStream(inputStream);
            String defaultFlowId = sp.stmFlowStore.getDefaultFlow();
            State state = new State(stateForAllowedActions, defaultFlowId);
            List<String> allowedActions = sp.infoProvider.getAllowedActions(state);
            String s = allowedActions.toString();
            System.out.println(s);
        }
    }

    private void renderStateDiagram() throws Exception {
        try (InputStream inputStream = Files.newInputStream(file.toPath())) {
            StoreProcessors sp = processStream(inputStream);
            String s = sp.generator.toStateDiagram();
            System.out.println(s);
        }
    }

    public StoreProcessors processStream(InputStream stream) throws Exception {
        STMFlowStoreImpl stmFlowStoreImpl = new DummyStore();
        XmlFlowReader xfr = new XmlFlowReader(stmFlowStoreImpl);
        xfr.parse(stream);
        return obtainProcessors(stmFlowStoreImpl);
    }

    private StoreProcessors obtainProcessors(STMFlowStoreImpl stmFlowStoreImpl) {
        StoreProcessors processors = new StoreProcessors();
        processors.generator = new STMPlantUmlSDGenerator(stmFlowStoreImpl);
        processors.infoProvider = new STMActionsInfoProvider(stmFlowStoreImpl);
        processors.stmFlowStore = stmFlowStoreImpl;
        return processors;
    }

    public static class StoreProcessors {
        public STMPlantUmlSDGenerator generator;
        public STMActionsInfoProvider infoProvider;
        public STMFlowStore stmFlowStore;
    }
}
