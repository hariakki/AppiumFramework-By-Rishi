package com.twinspires.qa.core.webservices;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.twinspires.qa.core.tests.AbstractTest;
import org.aspectj.weaver.ast.Test;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.qas.qtest.api.services.design.model.TestCase;
import org.qas.qtest.api.services.design.model.TestStep;
import org.qas.qtest.api.services.design.model.transform.TestCaseJsonUnmarshaller;
import org.qas.qtest.api.services.design.model.transform.TestStepJsonUnmarshaller;
import org.qas.qtest.api.services.execution.model.TestCycle;
import org.qas.qtest.api.services.execution.model.transform.TestCycleJsonUnmarshaller;
import org.qas.qtest.api.services.plan.model.Release;
import org.qas.qtest.api.services.plan.model.transform.ReleaseJsonUnmarshaller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kasey.sparkman on 8/16/18.
 */
public class WsQtest extends AbstractWS {

    private Long projectID;
    private String serviceToken;
    private String endpointBase;
    private String endpointProjects;

    public WsQtest(Long projectID, String serviceToken) {
        this.projectID = projectID;
        this.serviceToken = serviceToken;
        endpointBase = "https://twinspires.qtestnet.com/api/v3/";
        endpointProjects = endpointBase + "projects/" + this.projectID + "/";
    }

    private void printParametersExceptionMessage(Exception e, Object... parameters) {
        printExceptionMessage("validate parameters", e, parameters);
    }

    private void printRequestExceptionMessage(Exception e, Object... parameters) {
        printExceptionMessage("submit", e, parameters);
    }

    private void printParsingExceptionMessage(Exception e, Object... parameters) {
        printExceptionMessage("parse", e, parameters);
    }

    private void printExceptionMessage(String attemptVerb, Exception e, Object... parameters) {
        String callingClass = Thread.currentThread().getStackTrace()[3].getClassName();
        String callingMethod = Thread.currentThread().getStackTrace()[3].getMethodName();
        String exceptMessage = e.getMessage();
        String variableVals = "";

        for(int i = 0; i < parameters.length; i++) {
            variableVals += (i == 0) ? "" : ", ";
            variableVals += parameters[i].toString().trim();
        }

        System.out.println("  ::  Unable to " + attemptVerb + " "
                + callingClass + "." + callingMethod  + "(" + variableVals + "): \n"
                + "  ::  " + exceptMessage);
    }

    /**
     * Gets a list of test cases for any approve and the most current minor version of a test case by ID
     * @param testCaseId the test case ID to search
     * @return A list of qTest TestCase objects by version (chronologically)
     */
    public List<TestCase> getListTestCasesAllVersions(Long testCaseId) {
        List<TestCase> allTestCaseVersions = new ArrayList<>();
        JSONArray jsonTestCases;
        String endpoint;

        try {
            // Build endpoint
            endpoint = endpointProjects
                    + "/test-cases/" + testCaseId
                    + "/versions";

            // Sends the WS request
            this.sendRequest(REQ_METHOD_GET,
                    "",
                    endpoint,
                    null,
                    "Authorization: " + serviceToken);
        } catch (Exception e) {
            printRequestExceptionMessage(e, testCaseId);
        }

        try {
            jsonTestCases = parseToJSONArray(getLastResponseBody());
            for(int i = 0; i < jsonTestCases.length(); i++) {
                allTestCaseVersions.add(TestCaseJsonUnmarshaller.getInstance().parse(jsonTestCases.get(i).toString()));
            }
        } catch (Exception e) {
            printParsingExceptionMessage(e, testCaseId);
        }

        return allTestCaseVersions;
    }

    /**
     * Gets the latest approved test case by ID
     * @param testCaseId the test case ID to search
     * @return qTest TestCase of the latest approved version
     */
    public TestCase getTestCaseLatestApproved(Long testCaseId) {
        List<TestCase> allTCVersions = getListTestCasesAllVersions(testCaseId);
        Double curFullVersion;
        Long curDesiredVersion;
        Long desiredVersion;

        for(int i = allTCVersions.size()-1; i >= 0; i--) {
            // Gets the latest Approved version
            curFullVersion = Double.parseDouble(allTCVersions.get(i).getProperty("version").toString());
            curDesiredVersion = curFullVersion.longValue();

            // Get the latest Approved test case, if not already selected
            if(curDesiredVersion.doubleValue() == curFullVersion.doubleValue()) {
                return allTCVersions.get(i);
            }
        }
        return null;
    }

    /**
     * Gets a list of all test cycles under the specified parent
     * @param parentType the parent object type [root | release | test-cycle]
     * @param parentId the id of the parent object
     * @param getDescendants true to pull all the parent's descendants; false to pull only direct descendants
     * @return list of test cycles and suites
     */
    public List<TestCycle> getTestCycles(String parentType, Long parentId, boolean getDescendants) {
        List<TestCycle> allTestCycles = new ArrayList<>();
        JSONArray jsonTestCycles;
        JSONObject requestBody = new JSONObject();
        String endpoint;
        String pType = parentType.toLowerCase().trim();

        // Validate parentType is acceptable [root | release | test-cycle]
        if(!(pType.equalsIgnoreCase("root")
                || pType.equalsIgnoreCase("release")
                || pType.equalsIgnoreCase("test-cycle"))) {
            printParametersExceptionMessage(
                    new Exception("parentType (" + pType + ") not an acceptable value <root|release|test-cycle>"),
                    parentType, parentId, getDescendants);
        }

        try {
            // Build endpoint
            endpoint = endpointProjects + "test-cycles";

            requestBody.put("parentId", "" + parentId);
            requestBody.put("parentType", pType);
            if (getDescendants) requestBody.put("expand", "descendants");

            // Sends the WS request
            this.sendRequest(REQ_METHOD_GET,
                    "",
                    endpoint,
                    requestBody,
                    "Accept: " + CONTENT_APP_JSON,
                    "Authorization: " + serviceToken);
        } catch (Exception e) {
            printRequestExceptionMessage(e, parentId, getDescendants);
        }

        try {
            jsonTestCycles = parseToJSONArray(getLastResponseBody());
            for(int i = 0; i < jsonTestCycles.length(); i++) {
                allTestCycles.add(TestCycleJsonUnmarshaller.getInstance().parse(jsonTestCycles.get(i).toString()));
            }
        } catch (Exception e) {
            printParsingExceptionMessage(e, parentId, getDescendants);
        }

        return allTestCycles;
    }

    public List<Release> getReleases(boolean includeClosed) {
        List<Release> projectReleases = new ArrayList<>();
        JSONArray jsonReleases;
        JSONObject requestBody = new JSONObject();
        String endpoint;

        try {
            // Build endpoint
            endpoint = endpointProjects + "releases";
            requestBody.put("includeClosed", "" + includeClosed);

            // Sends the WS request
            this.sendRequest(REQ_METHOD_GET,
                    "",
                    endpoint,
                    requestBody,
                    "Accept: " + CONTENT_APP_JSON,
                    "Authorization: " + serviceToken);
        } catch (Exception e) {
            printRequestExceptionMessage(e, includeClosed);
        }

        try {
            jsonReleases = parseToJSONArray(getLastResponseBody());
            for(int i = 0; i < jsonReleases.length(); i++) {
                projectReleases.add(ReleaseJsonUnmarshaller.getInstance().parse(jsonReleases.get(i).toString()));
            }
        } catch (Exception e) {
            printParsingExceptionMessage(e, includeClosed);
        }

        return projectReleases;
    }

    /**
     * Gets the list of test steps for the specified version of the test case.
     * @param testCaseId the desired test case ID
     * @param testCaseVerId the desired test case version ID
     * @param expandCalledStep true to expand "called test" steps; false to keep them collapsed. (Should always use true)
     * @return List of qTest TestStep objects
     */
    public List<TestStep> getTestSteps(long testCaseId, long testCaseVerId, boolean expandCalledStep) {
        JSONObject requestBody = new JSONObject();
        JSONArray jsonResponse;
        String endpoint;
        List<TestStep> testSteps = new ArrayList<>();

        try {
            // Build endpoint
            endpoint = endpointProjects
                    + "/test-cases/" + testCaseId
                    + "/versions/" + testCaseVerId
                    + "/test-steps";

            if(expandCalledStep) requestBody.put("expand", "calledteststep");

            // Sends the WS request
            this.sendRequest(REQ_METHOD_GET,
                    "",
                    endpoint,
                    requestBody,
                    "Accept: " + CONTENT_APP_JSON,
                    "Authorization: " + serviceToken);
        } catch (Exception e) {
            printRequestExceptionMessage(e, testCaseId, testCaseVerId, expandCalledStep);
        }

        try {
            // Parse Response
            jsonResponse = parseToJSONArray(getLastResponseBody());
            for(int i = 0; i < jsonResponse.length(); i++) {
                testSteps.add(TestStepJsonUnmarshaller.getInstance().parse(jsonResponse.getJSONObject(i).toString()));
            }

        } catch (Exception e) {
            printParsingExceptionMessage(e, testCaseId, testCaseVerId, expandCalledStep);
        }

        return testSteps;
    }

    /**
     * Updates qTest TestRun fields
     * @param testRunId the TestRun Id which to make the change
     * @param fieldProperties JSONObjects with "field_id" and "field_values" set
     */
    public void updateTestRunFields(Long testRunId, JSONObject... fieldProperties) {
        JSONObject requestBody = new JSONObject();
        JSONArray propertiesList = new JSONArray();
        String endpoint;
        List<TestStep> testSteps = new ArrayList<>();

        try {
            // Build endpoint
            endpoint = endpointProjects + "test-runs/" + testRunId;

            // Add all field properties to list
            for (int i = 0; i < fieldProperties.length; i++) {
                propertiesList.put(fieldProperties[i]);
            }

            // Add field properties to JSON
            requestBody.put("properties", propertiesList);

            // Send update field request
            sendRequest("PUT",
                    CONTENT_APP_JSON,
                    endpoint,
                    requestBody,
                    "Accept: " + CONTENT_APP_JSON,
                    "Authorization: " + serviceToken);

        } catch (Exception e) {
            printRequestExceptionMessage(e, testRunId, fieldProperties);
        }
    }

    /**
     * Utilizes updateTestRunFields to make an update the Feature field on a TestRun
     * @param testRunId the TestRun Id of the Test Run in qTest to update the Feature field
     * @param featureNumber The text to populate in the Feature field
     */
    public void updateTestRunFeature(Long testRunId, String featureNumber){
        JSONObject featureProperty;

        featureProperty = new JSONObject()
                .put("field_id", (long) 5324375)
                .put("field_value", featureNumber);
        updateTestRunFields(testRunId, featureProperty);
    }
}