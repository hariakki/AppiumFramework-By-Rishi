package com.twinspires.qa.core.util;

import com.twinspires.qa.core.enums.Affiliate;
import com.twinspires.qa.core.enums.TestEnv;
import com.twinspires.qa.core.enums.TestStatus;
import com.twinspires.qa.core.webservices.WsQtest;
import org.qas.api.ClientConfiguration;
import org.qas.qtest.api.auth.PropertiesQTestCredentials;
import org.qas.qtest.api.auth.QTestCredentials;
import org.qas.qtest.api.internal.model.ArtifactLevel;
import org.qas.qtest.api.services.design.TestDesignServiceClient;
import org.qas.qtest.api.services.design.model.TestCase;
import org.qas.qtest.api.services.design.model.TestStep;
import org.qas.qtest.api.services.execution.TestExecutionServiceClient;
import org.qas.qtest.api.services.execution.model.*;
import org.qas.qtest.api.services.plan.model.Release;
import org.qas.qtest.api.services.project.ProjectServiceClient;
import java.io.*;
import java.util.*;

/**
 * Created by dalwinder.singh on 7/10/17.
 */
public class QTestService {

    private static String SERVICE_ENDPOINT = "twinspires.qtestnet.com:443";
    private static String SERVICE_TOKEN = "dHdpbnNwaXJlc3x0ZXN0QHR3aW5zcGlyZXMuY29tOjE1NjE4MzIzMTE4MDQ6MTJlMjZiYmY3ZjVhM" +
            "DQxYTk1NmJiNmU3ZGM5OWRhOGY";
    private static long projectID = 37843;
    private static long releaseIdIteProd = 199114; // "Automation Results" Release under Test Execution for ITE/Prod
    private QTestCredentials credentials;
    private ProjectServiceClient projectService;
    private TestDesignServiceClient testDesignService;
    private TestExecutionServiceClient testExecutionService;
    private InputStream readCredentials;
    private WsQtest wsQtest;
    List<TestStep> expectedStepsLog;
    List<TestStepLog> builtStepsLog;
    List<TestRun> suiteTestRuns;
    TestCycle environmentCycle;
    TestCycle affiliateCycle;
    TestCycle projectCycle;
    TestSuite methodSuite;
    TestRun testRun;
    TestCase testCase;
    TestEnv exeTestEnv;
    Affiliate exeAffiliate;
    String exeProject;
    String exeGroup;
    String exeClass;
    long finalStatus;
    int stepsCounter = 0;

    public QTestService() {
        try {
            readCredentials = QTestService.class.getResourceAsStream("/qtest.properties");
            credentials = new PropertiesQTestCredentials(readCredentials);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to connect to Twinspires Qtest");
        }
        //Initialising the variables
        projectService = new ProjectServiceClient(credentials);
        projectService.setEndpoint(SERVICE_ENDPOINT);
        testDesignService = new TestDesignServiceClient(credentials);
        testDesignService.setEndpoint(SERVICE_ENDPOINT);
        testExecutionService = new TestExecutionServiceClient(credentials);
        testExecutionService.setEndpoint(SERVICE_ENDPOINT);
        testExecutionService.setConfiguration(new ClientConfiguration()
                .withSocketTimeout((3)*(60)*(1000))
                .withMaxErrorRetry(2));
        expectedStepsLog = new ArrayList<>();
        builtStepsLog = new ArrayList<>();
        wsQtest = new WsQtest(projectID, SERVICE_TOKEN);
    }

    public TestLog createTestLog(TestCase testCase, Date startTime, Date endTime, ExecutionStatus status,
                                 List<TestStepLog> testStepLogs, boolean reportSteps){
        TestLog testLog;
        ExecutionStatus incomplete = new ExecutionStatus().setId(TestStatus.INCOMPLETE.getStatus());

        testLog= new TestLog()
                .withTestCase(testCase)
                .withExecutionStartDate(startTime)
                .withExecutionEndDate(endTime)
                .withStatus(status);

        // If pre-screened to report individual steps
        if(reportSteps) {
            int actSteps = testStepLogs.size()-1;
            int expSteps = testCase.getTestSteps().size()-1;
            // Assume proper mapping and fill in missing steps with Incomplete statuses
            if(actSteps < expSteps) {
                for(actSteps = actSteps+1; actSteps <= expSteps; actSteps++) {
                    testStepLogs.add(createTestStepLog(testCase.getTestSteps().get(actSteps).getId(), incomplete));
                }
            }
            testLog.withTestStepLogs(testStepLogs);
        }

        return testLog;
    }

    public TestLog createTestLogWithoutSteps(TestCase testCase, Date startTime, Date endTime, ExecutionStatus status){
        TestLog testLog = new TestLog()
                .withTestCase(testCase)
                .withExecutionStartDate(startTime)
                .withExecutionEndDate(endTime)
                .withStatus(status);
        return testLog;
    }

    public TestStepLog createTestStepLog(Long testStepID, ExecutionStatus status){
        TestStepLog testStepLog = new TestStepLog()
                .withTestStepId(testStepID)
                .withStatus(status);
        return  testStepLog;
    }

    public Long getFinalStatus() {
        return finalStatus;
    }

    public int getStepsCounter() {
        return stepsCounter;
    }

    public Long getTestCaseID() throws Exception{
        try {
            return testCase.getId();
        } catch (Exception e) {}
        try {
            return testRun.getTestCase().getId();
        } catch (Exception e) {
            throw new Exception("Unable to find test case ID by TestCase or TestRun");
        }
    }

    public Long getTestCaseVersionID() {
        return testCase.getTestCaseVersionId();
    }

    public TestCase getTestCase() {
        return testCase;
    }

    public Long getTestRunID() {
        return testRun.getId();
    }

    public Long getTestStepID(int stepNumber){
        if(expectedStepsLog.size() > 0) {
            return expectedStepsLog.get(stepNumber).getId();
        }
        else {
            return null;
        }
    }

    public List<TestStepLog> getBuiltStepsLog() {
        return builtStepsLog;
    }

    public void incrementStepsCounter(){
        stepsCounter++;
    }

    public void resetStepCounterAndArray(){
        expectedStepsLog.clear();
        builtStepsLog.clear();
        finalStatus = 0;
        stepsCounter = 0;
    }

    public void setFinalStatus(Long overallStatus) {
        finalStatus = overallStatus;
    }

    public String setupByTestRunName(String testCaseName) {
        long testCaseID = (long) 0;
        long testRunID = (long) 0;

        for(int i = 0; i < suiteTestRuns.size(); i++) {
            if (suiteTestRuns.get(i).getName().replaceAll("\\W", "").toLowerCase()
                    .equalsIgnoreCase(testCaseName.replaceAll("\\W", "").toLowerCase())) {
                testRun = suiteTestRuns.get(i);
                testRunID = testRun.getId();
                testCaseID = testRun.getTestCase().getId();
                break;
            }
        }
        System.out.println("Test Case Id: " + testCaseID);
        System.out.println("Run Id: " + testRunID);

        // Found matching Test Case ID was found
        if(testCaseID != (long) 0) {
            // Determine the Test Case's latest approved version and acquire the TestCase object
            testCase = wsQtest.getTestCaseLatestApproved(testCaseID);
            setTestStepsIDs(testCaseID);
            return "";
        } else {
            return "  qTest Setup: Search for Method reporting TestRun failed [" + testCaseName + "] " +
                    "under the suite '" + methodSuite.getName() + "' (" + methodSuite.getId() + ")\n";
        }
    }

    /**
    * This method gets all the test runs for suite under test from Qtest and
    * store test run IDs (Value) and test case names (Key) in a Hash map
    **/
    public void setTestRunsInfoUnderSuite(long suiteID) {
        ListTestRunRequest listTestRunRequest = new ListTestRunRequest()
                .withProjectId(projectID)
                .withArtifactId(suiteID)
                .withArtifactLevel(ArtifactLevel.TEST_SUITE);

        suiteTestRuns = testExecutionService.listTestRun(listTestRunRequest);
    }

    public void setTestStepsIDs(long testCaseID) {
        expectedStepsLog = wsQtest.getTestSteps(testCaseID, getTestCaseVersionID(), true);
    }

    public void submitTestRunResult(Long testRunID, TestLog testLog){
        SubmitTestLogRequest submitTestLogRequest =
                new SubmitTestLogRequest()
                        .withProjectId(projectID)
                        .withTestRunId(testRunID)
                        .withTestLog(testLog);

        testExecutionService.submitTestLog(submitTestLogRequest);
    }

    public void updateTestRunFeature(Long testRunID, String featureNumber){
        wsQtest.updateTestRunFeature(testRunID, featureNumber);
    }

    public String getExecutionVariableMethod(String testMethodName) {
        return "       METHOD: " + testMethodName + "\n";
    }

    public String setExecutionVariables(TestEnv testEnv, Affiliate affiliate, String project,
                                      String group, String className, boolean printDebug) {
        String debugMessage = "";
        exeTestEnv = testEnv;
        exeAffiliate = affiliate;
        exeProject = project;
        exeGroup = group;
        exeClass = className;

        debugMessage =  "  ENVIRONMENT: " + exeTestEnv + "\n"
                     +  "    AFFILIATE: " + "(" + exeAffiliate.getText() + ")\n"
                     +  "      PROJECT: " + exeProject + "\n"
                     +  "        GROUP: " + exeGroup + "\n"
                     +  "        CLASS: " + exeClass + "\n";
        if(printDebug) System.out.println(debugMessage);
        return debugMessage;
    }

    /**
     * Searches correct mapping locations by Environment > Affiliate > Project and saves necessary mapping details
     *   exeTestEnv the Environment under which to map results
     *   exeAffiliate the Affiliate for which to map results (ignored if Boss|Brisnet|Tournaments|Extranet)
     *   exeProject the Project under which to map results
     *   exeGroup determines some special mapping cases (ex. Extranet)
     * @return error messages
     */
    public String setReportCycles() {
        long releaseId = 0;
        List<TestCycle> projCycleList;
        String affiliateSearch = "";
        String projectSearch = "";

        // Get Release
        releaseId = getReleaseId(exeTestEnv);
        if(releaseId == 0)
            return "  qTest Setup: Search for reporting Release failed (" + exeTestEnv + ")\n";

        // Set Env Cycle
        if(setEnvironmentCycle(exeTestEnv, releaseId) == null)
            return "  qTest Setup: Search for Environment reporting Cycle failed [" + exeTestEnv + "] " +
                    "under the Release (" + releaseId + ")\n";

        // Define exeAffiliate and project cycle search terms
        if(exeProject.equalsIgnoreCase("boss")) {
            affiliateSearch = "boss";
            projectSearch = affiliateSearch;
        } else if (exeProject.equalsIgnoreCase("brisnet")) {
            affiliateSearch = "brisnet";
            projectSearch = affiliateSearch;
        } else if (exeGroup.toLowerCase().contains("extranet")
                || exeClass.toLowerCase().contains("extranet")) {
            affiliateSearch = "extranet";
            projectSearch = affiliateSearch;
        } else if (exeProject.equalsIgnoreCase("tournaments")) {
            affiliateSearch = "tournaments";
            projectSearch = affiliateSearch;
        } else {
            affiliateSearch = "(" + exeAffiliate.getText().toLowerCase() + ")";
            projectSearch = exeProject;
        }

        // Set Affiliates Cycle
        if(setAffiliateCycle(affiliateSearch) == null)
            return "  qTest Setup: Search for Affiliate reporting Cycle failed [" + affiliateSearch + "] " +
                    "under the cycle '" + environmentCycle.getName() + "' (" + environmentCycle.getId() + ") \n";

        // Set Project Cycle
        if(setProjectCycle(projectSearch) == null)
            return "  qTest Setup: Search for Project reporting Cycle failed [" + projectSearch + "] " +
                    "under the cycle '" + affiliateCycle.getName() + "' (" + affiliateCycle.getId() + ") \n";

        return "";
    }

    /**
     * Checks Project Cycles for suites
     *   exeClass name of the class under execution (should map to a similarly titled qTest Test Suite)
     */
    public String setReportSuites() {
        List<TestCycle> jenkinsCycles;
        List<TestSuite> suiteList;
        long suiteId = 0;
        String expName;
        String suiteName;

        // Format suite search term by stripping "Test" from class name and lowercasing
        expName = exeClass.toLowerCase().substring(0, exeClass.length()-4).trim();
        // Strip "TS" prefix from twinspires project class names
        expName = (exeProject.equalsIgnoreCase("twinspires")) ? expName.substring(2) : expName;

        // Check Project Cycle test suites
        suiteList = projectCycle.getTestSuites();
        if(suiteList != null) {
            for (int i = 0; i < suiteList.size(); i++) {
                suiteName = suiteList.get(i).getName().replaceAll("\\W", "").toLowerCase();
                if (suiteName.startsWith(expName)) {
                    methodSuite = suiteList.get(i);
                    suiteId = suiteList.get(i).getId();
                    break;
                }
            }
        }

        // Matching Class Suite not found directly under the Project Cycle
        if(suiteId == 0) {
            // Search through Jenkins Cycles for suites
            jenkinsCycles = projectCycle.getTestCycles();
            if(jenkinsCycles != null) {
                for (int i = 0; i < jenkinsCycles.size(); i++) {

                    // Search thorugh a Jenkins Cycle's Suites
                    suiteList = jenkinsCycles.get(i).getTestSuites();
                    if (suiteList != null) {
                        for (int j = 0; j < suiteList.size(); j++) {

                            // Remove whitespace and lowercase for class name comparison
                            suiteName = suiteList.get(j).getName().replaceAll("\\W", "").toLowerCase();
                            if (suiteName.startsWith(expName)) {
                                methodSuite = suiteList.get(j);
                                suiteId = suiteList.get(j).getId();
                                break;
                            }
                        }
                    }
                }
            }
        }

        if(suiteId == 0) {
            return "  qTest Setup: Search for Class reporting Suite failed [" + expName + "] " +
                    "under the cycle '" + projectCycle.getName() + "' (" + projectCycle.getId() + ")\n";
        } else {
            // Uses Suite ID to get list of the Suite's Test Runs
            setTestRunsInfoUnderSuite(suiteId);
            return "";
        }
    }

    /**
     * Used within setReportCycles
     */
    private long getReleaseId(TestEnv testEnv) {
        List<Release> allReleases;
        String fieldName;
        String fieldValue;

        try { // Get the release based on ENV
            if (testEnv.equals(TestEnv.STE)) {
                // For STE, searches for a Completed status Release/Iteration
                allReleases = wsQtest.getReleases(false);

                for (int i = 0; i < allReleases.size(); i++) {
                    for(int f = 0; f < allReleases.get(i).getFieldValues().size(); f++) {
                        fieldName = allReleases.get(i).getFieldValues().get(0).getProperty("field_name").toString();
                        fieldValue = allReleases.get(i).getFieldValues().get(0).getProperty("field_value_name").toString();
                        if(fieldName.equalsIgnoreCase("Status")) {
                            if(fieldValue.equalsIgnoreCase("Completed")
                                    && allReleases.get(i).getName().matches("R\\d*\\.\\d?\\s?.*")) {
                                return allReleases.get(i).getId();
                            }
                        }
                    }
                }

            // For ITE/PROD, hard-codes to "Automation Results" Release
            } else { //if(testEnv.equals(TestEnv.ITE) || testEnv.equals(TestEnv.PROD)) {
                return releaseIdIteProd;
            }
        } catch (Exception e) {
            System.out.println("Couldn't find the release");
        }
        return 0;
    }

    /**
     * Used within setReportCycles
     */
    private TestCycle setEnvironmentCycle(TestEnv testEnv, long releaseId) {
        List<TestCycle> releaseCycleList;

        try { // Search Release for corresponding Env Cycle
            releaseCycleList = wsQtest.getTestCycles("release", releaseId, false);
            for (int i = 0; i < releaseCycleList.size(); i++) {
                if(releaseCycleList.get(i).getName().toUpperCase().startsWith(testEnv.getText())) {
                    environmentCycle = releaseCycleList.get(i);
                    return environmentCycle;
                }
            }
        } catch (Exception e) {
            System.out.println("Couldn't Find the Environment Cycle");
        }
        return null;
    }

    /**
     * Used within setReportCycles
     */
    private TestCycle setAffiliateCycle(String searchTerm) {
        List<TestCycle> affCycleList;

        try {
            // Boss|Brisnet|Tournaments|Extranet projects' Affiliate Cycle is the same level as Environment Cycle
            if (searchTerm.equalsIgnoreCase("boss")
                    || searchTerm.equalsIgnoreCase("brisnet")
                    || searchTerm.equalsIgnoreCase("extranet")
                    || searchTerm.equalsIgnoreCase("tournaments")) {
                affiliateCycle = environmentCycle;
                return affiliateCycle;

                // Find and set Affiliate Cycle TS|KS|CB|OK|MB|RB|BH
            } else {
                affCycleList = wsQtest.getTestCycles("test-cycle", environmentCycle.getId(), false);
                for (int i = 0; i < affCycleList.size(); i++) {
                    if (affCycleList.get(i).getName().toLowerCase().trim().contains(searchTerm)) {
                        affiliateCycle = affCycleList.get(i);
                        return affiliateCycle;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Couldn't Find the Affiliate Cycle");
        }
        return null;
    }

    /**
     * Used within setReportCycles
     */
    private TestCycle setProjectCycle(String searchTerm) {
        List<TestCycle> projCycleList;
        String alteredSearchTerm = "";
        String alteredProjName = "";

        if (searchTerm.equalsIgnoreCase("twinspires")) {
            alteredSearchTerm = "desktop";
        } else {
            alteredSearchTerm = searchTerm.replace("_", "").replace(" ", "").toLowerCase().trim();
        }

        try {
            // Find and set Project Cycle
            projCycleList = wsQtest.getTestCycles("test-cycle", affiliateCycle.getId(), true);
            for (int i = 0; i < projCycleList.size(); i++) {
                alteredProjName = projCycleList.get(i).getName().replace("_", "").toLowerCase().trim();
                if (alteredProjName.contains(alteredSearchTerm)) {

                    // Ensures "Tux" project doesn't report to mobile
                    if(alteredSearchTerm.equalsIgnoreCase("tux")
                        && alteredProjName.contains("mobile")) {
                        continue;
                    }

                    // Desired project cycle found
                    projectCycle = projCycleList.get(i);
                    return projectCycle;
                }
            }
        } catch (Exception e) {
            System.out.println("Couldn't Find the Project Cycle");
        }
        return null;
    }
}


