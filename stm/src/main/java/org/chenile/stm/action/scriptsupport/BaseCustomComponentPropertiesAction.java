package org.chenile.stm.action.scriptsupport;

import org.chenile.stm.StateEntity;
import org.chenile.stm.action.ComponentPropertiesAware;
import org.chenile.stm.impl.ComponentPropertiesHelper;
import org.chenile.stm.impl.STMActionBase;

/**
 * Extend this action if it is desired to obtain access to properties that have been set up per state descriptor in the STD.
 * @author Raja Shankar Kolluru
 *
 * @param <StateEntityType>
 */
public class BaseCustomComponentPropertiesAction<StateEntityType extends StateEntity> extends STMActionBase<StateEntityType> implements  ComponentPropertiesAware{
	protected ComponentPropertiesHelper componentPropertiesHelper = new ComponentPropertiesHelper();
	protected boolean enableInlineScriptsInProperties = true;
	
	public boolean isEnableInlineScriptsInProperties() {
		return enableInlineScriptsInProperties;
	}

	public void setEnableInlineScriptsInProperties(
			boolean enableInlineScriptsInProperties) {
		this.enableInlineScriptsInProperties = enableInlineScriptsInProperties;
	}

	public void setComponentPropertiesHelper(ComponentPropertiesHelper cph) {
		this.componentPropertiesHelper = cph;	
	}
	
	public String getComponentProperty(StateEntityType stateEntity, String propertyName) {
		try {
			return componentPropertiesHelper.getComponentProperty(stateEntity, propertyName, enableInlineScriptsInProperties);
		} catch (Exception e) {
			return null;
		}
	}
	
	protected String getComponentProperty(StateEntityType stateEntity,String propertyName,
			String defaultValue) {
		try {
			String x =  getComponentProperty(stateEntity,propertyName);
			return (null == x)? defaultValue: x;
		}catch(Exception e) {
			return defaultValue;
		}
	}

}
