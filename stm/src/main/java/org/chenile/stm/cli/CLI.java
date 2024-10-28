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

import java.io.*;
import java.nio.file.Files;
import java.util.List;

@Command(name = "stm-cli", mixinStandardHelpOptions = true, version = "stm-cli 1.0",
        description = "Reads a State Definition file and allows a few operations on it. STM is not created. Hence components don't have to be in the class path.")
public class CLI implements Runnable {
    @Parameters(index = "0", paramLabel = "<XML File name>", description = "The XML filename to read. Must be a valid states XML. Component names in file will be ignored.")
    private File xmlFileName;
    @Option(names = {"-s", "--uml-state-diagram"}, description = "Generate a UML state diagram")
    private boolean umlStateDiagram;
    @Option(names = {"-a", "--allowed-actions"},paramLabel = "state", description = "Return allowed actions for a state")
    private String stateForAllowedActions;
    @Option(names = {"-o", "--output"},paramLabel = "output-file", description = "Writes output to the specified file")
    private String outputFile;
    @Spec
    Model.CommandSpec spec;

    public static void main(String... args) {
        System.exit(new CommandLine(new CLI()).execute(args));
    }
    @Override
    public void run() {
        try {
            if (umlStateDiagram)
                renderStateDiagram();
            else if (stateForAllowedActions != null && !stateForAllowedActions.isEmpty())
                allowedActions();
            else {
                System.err.println("Missing option: at least one of the " +
                        "-s or -a options must be specified");
                spec.commandLine().usage(System.err);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void writeFile(String s)
            throws IOException {
        FileWriter fileWriter = new FileWriter(outputFile);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.print(s);
        printWriter.close();
    }

    private void out(String s) throws IOException{
        if (outputFile != null && !outputFile.isEmpty()){
            writeFile(s);
        }else {
            System.out.println(s);
        }
    }
    private void allowedActions() throws Exception {
        processStream();
        String defaultFlowId = this.stmFlowStore.getDefaultFlow();
        State state = new State(stateForAllowedActions, defaultFlowId);
        List<String> allowedActions = this.infoProvider.getAllowedActions(state);
        String s = allowedActions.toString();
        out(s);
    }
    private void renderStateDiagram() throws Exception {
        processStream();
        String s = this.generator.toStateDiagram();
        out(s);
    }
    public void processStream() throws Exception {
        try (InputStream inputStream = Files.newInputStream(xmlFileName.toPath())) {
            STMFlowStoreImpl stmFlowStoreImpl = new DummyStore();
            XmlFlowReader xfr = new XmlFlowReader(stmFlowStoreImpl);
            xfr.parse(inputStream);
            initProcessors(stmFlowStoreImpl);
        }
    }
    private void initProcessors(STMFlowStoreImpl stmFlowStoreImpl) {
        this.generator = new STMPlantUmlSDGenerator(stmFlowStoreImpl);
        this.infoProvider = new STMActionsInfoProvider(stmFlowStoreImpl);
        this.stmFlowStore = stmFlowStoreImpl;
    }
    private STMPlantUmlSDGenerator generator;
    private STMActionsInfoProvider infoProvider;
    private STMFlowStore stmFlowStore;
}
