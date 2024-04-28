package org.chenile.core.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Locale;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={SpringTestConfig.class})
public class TestMessageBundle {
    @Autowired
    MessageSource messageSource;

    @Test public void testBundle() {
        String s1 = translate(501,Locale.CHINA,"service1");
        System.out.println("s1 for locale " + Locale.CHINA + " is " + s1);
        s1 = translate(501,Locale.getDefault(),"service1");
        System.out.println("s1 for locale " + Locale.getDefault() + " is " + s1);
        s1 = translate(501,Locale.ROOT,"service1");
        System.out.println("s1 for locale " + Locale.ROOT + " is " + s1);
        s1 = translate(501,Locale.CANADA_FRENCH,"service1");
        System.out.println("s1 for locale " + Locale.CANADA_FRENCH + " is " + s1);
    }

    private String translate(int code, Locale locale, Object... params){
        String defaultMessage = "Message code " + code + " not found in resource bundle";

        try {
            String m = messageSource.getMessage("E" + code,params,defaultMessage,locale);
            if (m == null) m = defaultMessage;
            return m;
        }catch(Exception e) {
            return defaultMessage;
        }
    }

}
