package org.chenile.owiz.impl.ognl;

import junit.framework.TestCase;

import org.chenile.owiz.config.model.AttachmentDescriptor;
import org.chenile.owiz.config.model.CommandDescriptor;
import org.chenile.owiz.impl.BaseContext;
import org.chenile.owiz.impl.MockCommand;
import org.chenile.owiz.impl.Router;
import org.chenile.owiz.impl.ognl.OgnlRouter;

public class OgnlRouterTest extends TestCase{
	Router<BaseContext> router ;

	static final String ROUTE_EXPRESSION = "mainRoute";
	public void setUp(){
		router = new OgnlRouter<BaseContext>();
		CommandDescriptor<BaseContext> routerCommandDescriptor = new CommandDescriptor<BaseContext>();
		routerCommandDescriptor.addComponentProperty(OgnlRouter.EXPRESSION,ROUTE_EXPRESSION);
		router.setCommandDescriptor(routerCommandDescriptor);
		
		MockCommand mockCommand = new MockCommand(100);
		CommandDescriptor<BaseContext> command = new CommandDescriptor<BaseContext>();
		mockCommand.setCommandDescriptor(command);
		AttachmentDescriptor<BaseContext> attachmentDescriptor = new AttachmentDescriptor<BaseContext>();
		attachmentDescriptor.put(Router.ROUTE, "100");
		command.addAttachmentDescriptor(attachmentDescriptor);
		command.setCommand(mockCommand);
		router.attachCommand(attachmentDescriptor, command);
		
		mockCommand = new MockCommand(101);
		command = new CommandDescriptor<BaseContext>();
		mockCommand.setCommandDescriptor(command);
		attachmentDescriptor = new AttachmentDescriptor<BaseContext>();
		attachmentDescriptor.put(Router.ROUTE, "101");
		command.addAttachmentDescriptor(attachmentDescriptor);
		command.setCommand(mockCommand);
		router.attachCommand(attachmentDescriptor, command);
		
		mockCommand = new MockCommand(105);
		command = new CommandDescriptor<BaseContext>();
		mockCommand.setCommandDescriptor(command);
		attachmentDescriptor = new AttachmentDescriptor<BaseContext>();
		attachmentDescriptor.put(Router.ROUTE, Router.DEFAULT);
		command.addAttachmentDescriptor(attachmentDescriptor);
		command.setCommand(mockCommand);
		router.attachCommand(attachmentDescriptor, command);
	
	}
	
	public void testRoute() throws Exception{
		BaseContext baseContext = new BaseContext();
		baseContext.put(ROUTE_EXPRESSION,"100");
		
		
		router.execute(baseContext);
		assertEquals(new Integer(100),baseContext.getListOfIndexes().get(0));
	}
	
	public void testSecondRoute() throws Exception{
		BaseContext baseContext = new BaseContext();
		baseContext.put(ROUTE_EXPRESSION,"101");
		
		router.execute(baseContext);
		assertEquals(new Integer(101),baseContext.getListOfIndexes().get(0));
	}
	
	public void testDefault() throws Exception{
		BaseContext baseContext = new BaseContext();
		baseContext.put(ROUTE_EXPRESSION,"1000");
		
		
		router.execute(baseContext);
		assertEquals(new Integer(105),baseContext.getListOfIndexes().get(0));
	}
	
	
}
