package com.audriga.jmap.gson;

import static org.junit.jupiter.api.Assertions.*;

import com.audriga.jmap.common.entity.AccountCapability;
import com.audriga.jmap.common.entity.capability.MailAccountCapability;
import com.audriga.jmap.common.entity.capability.SubmissionAccountCapability;
import com.audriga.jmap.common.entity.capability.VacationResponseAccountCapability;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class AccountCapabilitiesDeserializerTest extends AbstractGsonTest {
    private static final Type TYPE =
            new TypeToken<Map<Class<? extends AccountCapability>, AccountCapability>>() {}.getType();

    @Test
    public void mailAccountCapability() throws Exception {
        Map<Class<? extends AccountCapability>, AccountCapability> accountCapabilities =
                parseFromResource("account-capability/mail.json", TYPE);

        assertTrue(accountCapabilities.containsKey(MailAccountCapability.class));
        AccountCapability accountCapability = accountCapabilities.get(MailAccountCapability.class);
        assertEquals(MailAccountCapability.class, accountCapability.getClass());
        MailAccountCapability mailAccountCapability = (MailAccountCapability) accountCapability;
        assertEquals(Long.valueOf(20), mailAccountCapability.maxMailboxesPerEmail());
        assertEquals(Long.valueOf(10), mailAccountCapability.maxMailboxDepth());
        assertEquals(200, mailAccountCapability.maxSizeMailboxName());
        assertEquals(50_000_000, mailAccountCapability.maxSizeAttachmentsPerEmail());
        assertEquals(List.of("receivedAt"), mailAccountCapability.emailQuerySortOptions());
        assertTrue(mailAccountCapability.mayCreateTopLevelMailbox());
    }

    @Test
    public void submissionAccountCapability() throws Exception {
        Map<Class<? extends AccountCapability>, AccountCapability> accountCapabilities =
                parseFromResource("account-capability/submission.json", TYPE);

        assertTrue(accountCapabilities.containsKey(SubmissionAccountCapability.class));
        AccountCapability accountCapability = accountCapabilities.get(SubmissionAccountCapability.class);
        assertEquals(SubmissionAccountCapability.class, accountCapability.getClass());
        SubmissionAccountCapability submissionAccountCapability = (SubmissionAccountCapability) accountCapability;
        assertEquals(0, submissionAccountCapability.maxDelayedSend());
        assertEquals(Map.of("SIZE", List.of("50000000")), submissionAccountCapability.submissionExtensions());
    }

    @Test
    public void vacationResponseAccountCapability() throws Exception {
        Map<Class<? extends AccountCapability>, AccountCapability> accountCapabilities =
                parseFromResource("account-capability/vacation-response.json", TYPE);

        assertTrue(accountCapabilities.containsKey(VacationResponseAccountCapability.class));
        assertEquals(
                VacationResponseAccountCapability.class,
                accountCapabilities.get(VacationResponseAccountCapability.class).getClass());
    }

    @Test
    public void allSupportedCapabilitiesAndUnknownCapability() throws Exception {
        Map<Class<? extends AccountCapability>, AccountCapability> accountCapabilities =
                parseFromResource("account-capability/all.json", TYPE);

        assertTrue(accountCapabilities.containsKey(MailAccountCapability.class));
        assertTrue(accountCapabilities.containsKey(SubmissionAccountCapability.class));
        assertTrue(accountCapabilities.containsKey(VacationResponseAccountCapability.class));
        assertEquals(3, accountCapabilities.size());
    }
}
