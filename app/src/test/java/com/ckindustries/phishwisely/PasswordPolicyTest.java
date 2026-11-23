package com.ckindustries.phishwisely;

import org.junit.Test;
import com.ckindustries.phishwisely.security.Passwords;
import static org.junit.Assert.*;

public class PasswordPolicyTest {
    @Test public void strongPasswordPasses() {
        // passes if password accepted
        assertNull(Passwords.validate("DemoPass123!"));
    }
    @Test public void shortPasswordFails() {
        // passes if password rejected (rule: <8 characters)
        assertNotNull(Passwords.validate("Aa1!"));
    }
    @Test public void missingSymbolFails() {
        // passes if password rejected (rule: missing symbol)
        assertNotNull(Passwords.validate("DemoPass123"));
    }
    @Test public void missingUppercaseFails() {
        // passes if password rejected (rule: missing uppercase)
        assertNotNull(Passwords.validate("demopass123!"));
    }
}
