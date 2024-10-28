package org.chenile.stm.impl;

import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.ExtendedBaseRules;
import org.apache.commons.digester.Rule;
import org.chenile.stm.STMSecurityStrategy;
import org.chenile.stm.StateEntity;
import org.chenile.stm.action.STMAction;
import org.chenile.stm.action.STMAutomaticStateComputation;
import org.chenile.stm.action.STMTransitionAction;
import org.chenile.stm.action.StateEntityRetrievalStrategy;
import org.chenile.stm.exception.STMException;
import org.chenile.stm.model.AutomaticStateDescriptor;
import org.chenile.stm.model.EventInformation;
import org.chenile.stm.model.FlowDescriptor;
import org.chenile.stm.model.StateDescriptor;
import org.chenile.stm.model.StateTagDescriptor;
import org.chenile.stm.model.TransientActionsAwareDescriptor;
import org.chenile.stm.model.Transition;
import org.xml.sax.Attributes;

/**
 * 
 * Provides an xml implementation of the FlowStoreReader. Supports flexible
 * custom xml tags for specific action state components. Custom properties are
 * made available to actions registered. These two features provide a powerful
 * extension making this parser support an xml based DSL.
 * 
 * @author Raja Shankar Kolluru
 */
public class XmlFlowReader extends FlowReaderBase {

	private static final String STATES_ADD_STATE_TAG = "states/add-state-tag";
	private static final String COMPONENT_NAME = "componentName";
	private static final String STATES_FLOW_TAG = "states/flow";
	private static final String STATES_FLOW = "states/flow/";
	private static final String COMPONENT_PROPERTIES = "/component-properties/?";
	private Digester digester;
	public static final String META_PREFIX = "meta-";

	/**
	 * 
	 * @param stmFlowStoreImpl
	 */
	public XmlFlowReader(STMFlowStoreImpl stmFlowStoreImpl) {
		super(stmFlowStoreImpl);
		setUpDigester(); 
		setUpDigesterRules();
	}
	
	private void setUpDigesterRules() {
		for (StateTagDescriptor std: stmFlowStoreImpl.actionTagsMap.values()){
			setRulesForStateTag(std.getTag());
		}
	}

	/**
	 * 
	 * @param filename
	 * @throws Exception
	 */
	public void setFilename(String filename) throws Exception {
		// discover all the streams that correspond to this filename.
		Enumeration<URL> urls = getClass().getClassLoader().getResources(
				filename);
		while (urls.hasMoreElements()) {
			URL u = urls.nextElement();
			parse(u.openStream()); 
		}
	}
	
	/**
	 * 
	 */
	public void init(){
		// does nothing since the work is done by setFilename itself
	}

	/**
	 * 
	 * @param inputStream
	 * @throws Exception
	 */
	public void parse(InputStream inputStream) throws Exception {
		digester.push(stmFlowStoreImpl);
		digester.parse(inputStream);
		digester.clear();
	}
	
	public class AddStateRule extends Rule {
		@SuppressWarnings("unchecked")
		@Override
		public void begin(String namespace, String xmlElementName,
				Attributes attributes) throws Exception {

			StateTagDescriptor std = new StateTagDescriptor();

			std.setComponentName(attributes.getValue("componentName"));
			String dc = attributes.getValue("descriptorClass");
			std.setDescriptorClass((Class<? extends StateDescriptor>)Class.forName(dc));
			String x = attributes.getValue("manualState");
			
			std.setManualState(Boolean.parseBoolean(x));
			std.setTag(attributes.getValue("tag"));
			XmlFlowReader.this.stmFlowStoreImpl.addStateTag(std);
			setRulesForStateTag(std.getTag());
		}
	}

	public void setUpDigester() {
		digester = new Digester();
		digester.setRules(new ExtendedBaseRules());
		// to make sure that we match wild cards such as ? and *


		digester.addRule(STATES_ADD_STATE_TAG, new AddStateRule());

		digester.addCallMethod("states/scripting-strategy",
				"setScriptingStrategy", 1, new Class<?>[] { String.class });
		digester.addCallParam("states/scripting-strategy", 0, COMPONENT_NAME);

		digester.addRule("states/entry-action",
				new AddTransitionActionToTransientActionAwareDescriptorRule());
		digester.addRule("states/exit-action",
				new AddTransitionActionToTransientActionAwareDescriptorRule());
		digester.addCallMethod("states/default-transition-action", "setDefaultTransitionAction",1);
		digester.addCallParam("states/default-transition-action", 0, "componentName");
		digester.addRule("states/event-information", new AddEventInformationRule());
		digester.addRule(STATES_FLOW_TAG,
				new CreateOrUseExistingObjectRule<FlowDescriptor>(
						FlowDescriptor.class, "getFlow"));
		digester.addSetProperties(STATES_FLOW_TAG);

		digester.addRule("states/flow/entry-action",
				new AddTransitionActionToTransientActionAwareDescriptorRule());
		digester.addRule("states/flow/exit-action",
				new AddTransitionActionToTransientActionAwareDescriptorRule());
		digester.addRule("states/flow/retrieval-strategy",
				new AddRetrievalStrategy());
		digester.addRule("states/flow/security-strategy",
				new AddSecurityStrategy());
		
		digester.addSetNext(STATES_FLOW_TAG, "addFlow");
	}

	private void setRulesForStateTag(String tag) {

		digester.addRule(STATES_FLOW + tag,
				new StateDescriptorAttributesRule());

		digester.addRule(STATES_FLOW + tag + "/entry-action",
				new AddTransitionActionToTransientActionAwareDescriptorRule());
		digester.addRule(STATES_FLOW + tag + "/exit-action",
				new AddTransitionActionToTransientActionAwareDescriptorRule());

		digester.addCallMethod(
				STATES_FLOW + tag + COMPONENT_PROPERTIES,
				"addXmlComponentProperty", 2, new Class[] { String.class,
						String.class });
		digester.addCallParamPath(STATES_FLOW + tag
				+ COMPONENT_PROPERTIES, 0);
		digester.addCallParam(STATES_FLOW + tag + COMPONENT_PROPERTIES,
				1);

		digester.addRule(STATES_FLOW + tag + "/on", new AddTransitionRule());
		// digester.addSetNext(STATES_FLOW + tag + "/on", "addTransition");

		digester.addSetNext(STATES_FLOW + tag, "addsd");

	}

	public class AddEventInformationRule extends Rule{
		@Override
		public void begin(String namespace, String xmlElementName,
				Attributes attributes) throws Exception {
			EventInformation eventInformation = new EventInformation();
			eventInformation.setEventId(attributes.getValue("eventId"));
			digester.push(eventInformation);
			processTransitionAction(eventInformation,attributes);
			processMetaAttributes(eventInformation, attributes);
		}
		
		protected void processTransitionAction(EventInformation eventInformation, Attributes attributes) throws Exception {
			if (attributes.getValue(COMPONENT_NAME) == null) return;
			eventInformation.setTransitionAction((STMTransitionAction<?>) stmFlowStoreImpl.
					                 makeTransitionAction(attributes.getValue(COMPONENT_NAME),true));
		}

		protected void processMetaAttributes(EventInformation eventInformation,
				Attributes attributes) {
			for (int i = 0; i < attributes.getLength(); i++) {
				String name = attributes.getLocalName(i);
				if ("".equals(name)) {
					name = attributes.getQName(i);
				}
				String value = attributes.getValue(i);

				if (name.startsWith(META_PREFIX)) {
					String n = name.substring(META_PREFIX.length());
					eventInformation.addMetaData(n, value);
				}
			}
		}
		
		public void end() throws Exception {
			EventInformation eventInformation = (EventInformation) digester.pop(); // pop the Transition from the digester stack.			
			stmFlowStoreImpl.addEventInformation(eventInformation);
		}
	}
	
	
	public class AddTransitionRule extends AddEventInformationRule {
		@Override
		public void begin(String namespace, String xmlElementName,
				Attributes attributes) throws Exception {

			String eventId = attributes.getValue("eventId");
			EventInformation eventInfo = stmFlowStoreImpl.getEventInformation(eventId);
			
			Transition transition;
			if (eventInfo != null) 
				transition = new Transition(eventInfo);	
			else
				transition = new Transition();
			digester.push(transition);

			transition.setEventId(eventId);
			transition.setNewFlowId(attributes.getValue("newFlowId"));
			transition.setNewStateId(attributes.getValue("newStateId"));
			// transition.setAclString(attributes.getValue("acls"));
			String invokableOnlyFromStm = attributes.getValue("invokableOnlyFromStm");
			if (invokableOnlyFromStm != null)
				transition.setInvokableOnlyFromStm(Boolean.parseBoolean(invokableOnlyFromStm));
			String rt = attributes.getValue("retrievalTransition");
			if (rt != null)
				transition.setRetrievalTransition(Boolean.parseBoolean(rt));
			processTransitionAction(transition,attributes);
			processMetaAttributes(transition, attributes);
		}
		
		/**
		 * 
		 */
		public void end() throws Exception {
			Transition transition = (Transition) digester.pop(); // pop the Transition from the digester stack.
			StateDescriptor sd = (StateDescriptor) digester.peek();			
			sd.addTransition(transition);
		}
	}

	/**
	 * Custom digester rule to process Action State Descriptor.
	 * 
	 * @author Raja Shankar Kolluru
	 *
	 */

	public class StateDescriptorAttributesRule extends Rule {

		@Override
		public void begin(String namespace, String xmlElementName,
				Attributes attributes) throws Exception {
			FlowDescriptor fd = (FlowDescriptor) digester.peek();
			StateDescriptor sd;
			boolean isManualState = stmFlowStoreImpl.actionTagsMap.get(
					xmlElementName).isManualState();
			sd = stmFlowStoreImpl.actionTagsMap.get(xmlElementName)
					.getDescriptorClass().getDeclaredConstructor().newInstance();
			sd.setManualState(isManualState);
			digester.push(sd);

			// id and initialState need to be injected into sd as well.
			String id = attributes.getValue("id");
			sd.setId(id);
			sd.setFlowId(fd.getId());

			String initialState = attributes.getValue("initialState");
			if (initialState != null)
				sd.setInitialState(Boolean.parseBoolean(initialState));
			if (sd instanceof AutomaticStateDescriptor)
				setActionDescriptorProperties((AutomaticStateDescriptor) sd,
						attributes, xmlElementName);
			processMetaAttributes(sd, attributes);
		}

		private void processMetaAttributes(StateDescriptor sd,
				Attributes attributes) {
			for (int i = 0; i < attributes.getLength(); i++) {
				String name = attributes.getLocalName(i);
				if ("".equals(name)) {
					name = attributes.getQName(i);
				}
				String value = attributes.getValue(i);

				if (name.startsWith(META_PREFIX)) {
					String n = name.substring(META_PREFIX.length());
					sd.addMetaData(n, value);
				}
			}
		}

		private void setActionDescriptorProperties(
				AutomaticStateDescriptor asd, Attributes attributes,
				String xmlElementName) throws STMException {

			// process componentName either from the attribute or from the
			// actionTagsMap
			String componentName = attributes.getValue(COMPONENT_NAME);
			if (componentName == null) {
				componentName = stmFlowStoreImpl.actionTagsMap.get(
						xmlElementName).getComponentName();
			}
			String enableInlineScripts = attributes
					.getValue("enableInlineScripts");
			boolean enable = true;
			if (enableInlineScripts != null)
				enable = Boolean.getBoolean(enableInlineScripts);

			asd.setComponentName(componentName);
			asd.setComponent((STMAutomaticStateComputation<?>) stmFlowStoreImpl
					.makeAutomaticStateComputation(asd.getComponentName(), enable));

			// add the rest of the attributes to the componentProperties
			for (int i = 0; i < attributes.getLength(); i++) {
				String name = attributes.getLocalName(i);
				if ("".equals(name)) {
					name = attributes.getQName(i);
				}
				String value = attributes.getValue(i);

				if (COMPONENT_NAME.equals(name) || "id".equals(name)
						|| "initialState".equals(name))
					continue; // processed these already.
				else {
					asd.addXmlComponentProperty(name, value);
				}
			}
		} 

		/**
		 * 
		 */
		public void end() throws Exception {
			digester.pop(); // pop the state descriptor from the stack.			
		}

	}

	public class AddTransitionActionToTransientActionAwareDescriptorRule extends Rule {

		@Override
		public void begin(String namespace, String xmlElementName,
				Attributes attributes) throws Exception {

			TransientActionsAwareDescriptor taad = (TransientActionsAwareDescriptor) digester
					.peek();
			String componentName = attributes.getValue(COMPONENT_NAME);
			String enableInlineScripts = attributes
					.getValue("enableInlineScripts");
			boolean enable = true;
			if (enableInlineScripts != null)
				enable = Boolean.getBoolean(enableInlineScripts);
			STMAction<?> transitionAction = (STMAction<?>) stmFlowStoreImpl
					.makeAction(componentName, enable);
			if (xmlElementName != null && "entry-action".equals(xmlElementName))
				taad.setEntryAction(transitionAction);
			else if (xmlElementName != null
					&& ("exit-action").equals(xmlElementName))
				taad.setExitAction(transitionAction);
		}
	}

	
	public class AddRetrievalStrategy extends Rule {

		@Override
		public void begin(String namespace, String xmlElementName,
				Attributes attributes) throws Exception {

			FlowDescriptor fd = (FlowDescriptor) digester.peek();
			String componentName = attributes.getValue(COMPONENT_NAME);

			@SuppressWarnings("unchecked")
			StateEntityRetrievalStrategy<StateEntity> retrievalStrategy = (StateEntityRetrievalStrategy<StateEntity>) stmFlowStoreImpl
					.makeRetrievalStrategy(componentName);
			fd.setRetrievalStrategy(retrievalStrategy);
		}
	}
	
	/**
	 * 
	 * @author Raja
	 *
	 */
	public class AddSecurityStrategy extends Rule {

		@Override
		public void begin(String namespace, String xmlElementName,
				Attributes attributes) throws Exception {

			FlowDescriptor fd = (FlowDescriptor) digester.peek();
			String componentName = attributes.getValue(COMPONENT_NAME);

			STMSecurityStrategy securityStrategy = (STMSecurityStrategy) stmFlowStoreImpl
					.makeSecurityStrategy(componentName);
			fd.setStmSecurityStrategy(securityStrategy);
		}
	}

	/**
	 * 
	 * @return
	 */
	public String toXml() {
		StringBuilder sb = new StringBuilder();

		return sb.toString();
	}

}
