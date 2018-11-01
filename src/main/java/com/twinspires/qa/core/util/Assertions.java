package com.twinspires.qa.core.util;


import java.util.Map;

import com.twinspires.qa.core.enums.TestStatus;
import org.qas.qtest.api.services.execution.model.ExecutionStatus;
import org.testng.asserts.IAssert;
import org.testng.asserts.SoftAssert;
import org.testng.collections.Maps;

/**
 * When an assertion fails, don't throw an exception but record the failure.
 * Calling assertAll() will cause an exception to be thrown if at
 * least one assertion failed.
 */
public class Assertions extends SoftAssert {

    // LinkedHashMap to preserve the order of failures
    Map<AssertionError, IAssert> m_errors = Maps.newLinkedHashMap();
    QTestService qTestService;
    Long testStepID;
    String qTestReporting = System.getProperty("reportToQTest", "true");

    @Override
    public void doAssert(IAssert assertion) {
        onBeforeAssert(assertion);
        TestStatus customTestStatus;
        ExecutionStatus status = new ExecutionStatus();
        String alteredMessage = "";
        String thisStep = "*Step " + (qTestService.getStepsCounter()+1) + ") ";

        // Set initial overall test status
        if(qTestService.getFinalStatus() == 0) {
            qTestService.setFinalStatus(TestStatus.PASSED.getStatus());
        }

        // Check for and handle custom statuses
        customTestStatus = TestStatus.hasStatusFlag(assertion.getMessage());
        if(customTestStatus != null) {
            onAssertSuccess(assertion);
            alteredMessage = assertion.getMessage().replace(customTestStatus.getStatusFlag(), "");
            Util.printLine(thisStep + customTestStatus.toString() + " - " + alteredMessage + "*");
            status.setId(customTestStatus.getStatus());
            onAfterAssert(assertion);

        } else {
            try {
                assertion.doAssert();
                onAssertSuccess(assertion);
                Util.printLine(thisStep + "PASSED - " + assertion.getMessage() + "*");
                status.setId(TestStatus.PASSED.getStatus());

            } catch (AssertionError error) {
                onAssertFailure(assertion, error);
                m_errors.put(error, assertion);
                Util.printLine(thisStep + "FAILED - " + assertion.getMessage() + "*");
                Util.takeScreenShot("Screenshot -" + assertion.getMessage());
                status.setId(TestStatus.FAILED.getStatus());
                qTestService.setFinalStatus(TestStatus.FAILED.getStatus());
            } finally {
                onAfterAssert(assertion);
            }
        }

        if (qTestReporting.equalsIgnoreCase("true")) {
            try {
                testStepID = qTestService.getTestStepID(qTestService.getStepsCounter());
            } catch (Exception e) {
                // No more steps exist in the test case.  Still log the test step
                testStepID = (long) 0;
            }
            try {
                qTestService.getBuiltStepsLog().
                        add(qTestService.createTestStepLog(testStepID, status));
            } catch (Exception e) {
                Util.printLine(">> Error creating test step log: " + e.getMessage());
            }
        }
        qTestService.incrementStepsCounter();
    }

    @Override
    public void assertAll() {
        Util.printLine(System.getProperty("line.separator"));
        if (!m_errors.isEmpty()) {

            StringBuilder sb = new StringBuilder("The following assertions failed:");
            boolean first = true;

            for (Map.Entry<AssertionError, IAssert> errors : m_errors.entrySet()) {

                if (first) {
                    first = false;
                } else {
                    sb.append(",");
                }

                sb.append("\n\t");
                sb.append("FAILED - " + errors.getKey().getMessage());
            }

            throw new AssertionError(sb.toString());
        }
    }

    public void setQTestServiceInstance(QTestService qTestService) {
        this.qTestService = qTestService;
    }

    /**
     * Provides the ability to perform a custom Workaround (Disabled) status
     * @param message the message to be displayed (the disabled step assertion message)
     */
    public void assertWorkaround(String message) {
        fail(TestStatus.WORKAROUND.getStatusFlag() + message);
    }

    public void assertIncomplete(String message) {
        fail(TestStatus.INCOMPLETE.getStatusFlag() + message);
    }

}
