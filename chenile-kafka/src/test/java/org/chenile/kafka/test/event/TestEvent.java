package org.chenile.kafka.test.event;

public class TestEvent {
	public static final String EVENTID ="testEvent";
	public String key;
	public String value;
	public TestEvent(String k, String v) {
		this.key = k; this.value = v;
	}
	
	public TestEvent() {}
}
