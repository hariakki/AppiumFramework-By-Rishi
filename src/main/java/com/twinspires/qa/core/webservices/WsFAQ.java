package com.twinspires.qa.core.webservices;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class WsFAQ extends AbstractWS {
    List<String> categoryIDs;
    List<String> categoryNames;
    List<String> answerSummaries;
    List<String> answerIds;
    String answerQuestion;
    String answerSolution;

    public List<String> getCategoryIDs(){return this.categoryIDs;}
    public List<String> getCategoryNames(){return this.categoryNames;}
    public List<String> getAnswerSummaries(){return this.answerSummaries;}
    public List<String> getAnswerIds(){return this.answerIds;}
    public String getAnswerQuestion(){return this.answerQuestion;}
    public String getAnswerSolution(){return this.answerSolution;}

    public WsFAQ getFAQCategories(){
        String affId = affiliate.getAffId();
        String url = buildEndpoint("/adw/support/categories");
        JSONObject requestBody = new JSONObject();
        JSONArray responseBody = new JSONArray();
        List<String> categories = new ArrayList<>();
        List<String> ids = new ArrayList<>();

        requestBody.put("username", "my_tux");
        requestBody.put("affid", affId);
        requestBody.put("affiliateId", affId);
        requestBody.put("output", "json");
        requestBody.put("ip", "0.0.0.0");

        sendRequest("GET", url, requestBody);
        responseBody = parseToJSONArray(lastResponseBody);
        responseBody.forEach(item -> {
            JSONObject searchCategory = (JSONObject) item;
            categories.add(searchCategory.getString("name"));
            ids.add(String.valueOf(searchCategory.getInt("id")));
        });

        this.categoryIDs = ids;
        this.categoryNames = categories;

        return this;
    }

    public WsFAQ getFAQCategoryAnswers(String categoryId){
        String affId = affiliate.getAffId();
        String url = buildEndpoint("/adw/support/answers");
        JSONObject requestBody = new JSONObject();
        List<String> answers = new ArrayList<>();
        List<String> ids = new ArrayList<>();

        requestBody.put("username", "my_tux");
        requestBody.put("ip", "0.0.0.0");
        requestBody.put("affid", affId);
        requestBody.put("affiliateId", affId);
        requestBody.put("output", "json");
        requestBody.put("categoryId",categoryId);
        requestBody.put("limit","10");
        requestBody.put("offset","0");

        sendRequest("GET", url, requestBody);
        parseToJSONObject(lastResponseBody).getJSONArray("items").forEach(item -> {
            JSONObject searchCategory = (JSONObject) item;
            answers.add(searchCategory.getString("summary"));
            ids.add(String.valueOf(searchCategory.getInt("id")));
        });

        this.answerIds = ids;
        this.answerSummaries = answers;

        return this;
    }

    public WsFAQ getFAQSearchAnswers(String keyword){
        String affId = affiliate.getAffId();
        String url = buildEndpoint("/adw/support/answers");
        JSONObject requestBody = new JSONObject();
        List<String> answers = new ArrayList<>();
        List<String> ids = new ArrayList<>();

        requestBody.put("username", "my_tux");
        requestBody.put("ip", "0.0.0.0");
        requestBody.put("affid", affId);
        requestBody.put("affiliateId", affId);
        requestBody.put("output", "json");
        requestBody.put("categoryId","");
        requestBody.put("keywords",keyword);
        requestBody.put("limit","10");
        requestBody.put("offset","0");

        sendRequest("GET", url, requestBody);
        parseToJSONObject(lastResponseBody).getJSONArray("items").forEach(item -> {
            JSONObject searchCategory = (JSONObject) item;
            answers.add(searchCategory.getString("summary"));
            ids.add(String.valueOf(searchCategory.getInt("id")));
        });

        this.answerIds = ids;
        this.answerSummaries = answers;

        return this;
    }

    public WsFAQ getFAQAnswer(String id){
        String affId = affiliate.getAffId();
        String url = buildEndpoint("/adw/support/answers/"+id);
        JSONObject requestBody = new JSONObject();
        JSONObject responseBody;

        requestBody.put("username", "my_tux");
        requestBody.put("ip", "0.0.0.0");
        requestBody.put("affid", affId);
        requestBody.put("affiliateId", affId);
        requestBody.put("output", "json");

        sendRequest("GET", url, requestBody);
        responseBody = parseToJSONObject(lastResponseBody);

        this.answerSolution = responseBody.getString("solution");
        this.answerQuestion = responseBody.getString("question");

        return this;
    }
}
