// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that deletes all comments from home page of website. */
@WebServlet("/comments")
public class CommentForumServlet extends HttpServlet {

  private static final Gson gson = new Gson();
  private static final Query query = new Query("Comment")
    .addSort("timestamp-ms", SortDirection.DESCENDING);
  private static final int MAX_NUM_COMMENTS = 100;
  private static final int MIN_NUM_COMMENTS = 0;
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    int maxNumComments = getMaxNumComments(request);

    List<Comment> comments = getComments(maxNumComments);
    String json = gson.toJson(comments);

    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Parse input from the form
    String commentString = getParameter(request, "text-input", "");
    String userName = getParameter(request, "user-name", "Anonymous");
    boolean upperCase = Boolean.parseBoolean(getParameter(request, "upper-case", "false"));
    boolean lowerCase = Boolean.parseBoolean(getParameter(request, "lower-case", "false"));
    long timestampMs = System.currentTimeMillis();

    if (upperCase && lowerCase) {
      upperCase = false;
      lowerCase = false;
    }
    if (upperCase) {
      commentString = commentString.toUpperCase();
    }
    if (lowerCase) {
      commentString = commentString.toLowerCase();
    }

    Document doc =
        Document.newBuilder().setContent(commentString).setType(Document.Type.PLAIN_TEXT).build();
    LanguageServiceClient languageService = LanguageServiceClient.create();
    Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
    float score = sentiment.getScore();
    languageService.close();

    System.out.println("Sentiment Analysis Score: " + score);

    Comment comment = new Comment(commentString, userName, score, timestampMs);
    storeComment(comment);

    response.sendRedirect("/index.html");
  }

  private String convertToJson(List<Comment> comments) {
    String json = gson.toJson(comments);
    return json;
  }

  /*
   * @return the request parameter, or the default value if the parameter
   *         was not specified by the client
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }

  private int getMaxNumComments(HttpServletRequest request) {
    String maxNumCommentsString = request.getParameter("max-num-comments");
    // Convert the input to an int.
    int maxNumComments;
    try {
      maxNumComments = Integer.parseInt(maxNumCommentsString);
    } catch (NumberFormatException e) {
      System.err.println("Could not convert to int: " + maxNumCommentsString);
      // If conversion fails, set default value
      maxNumComments = 5;
    }

    // Enforce bounds on number of comments displayed
    if (maxNumComments < MIN_NUM_COMMENTS) {
      maxNumComments = MIN_NUM_COMMENTS;
    }
    if (maxNumComments > MAX_NUM_COMMENTS) {
      maxNumComments = MAX_NUM_COMMENTS;
    }

    return maxNumComments;
  }

  public void storeComment(Comment comment) {
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("comment", comment.getCommentString());
    commentEntity.setProperty("user-name", comment.getUserName());
    commentEntity.setProperty("sentiment-score", comment.getSentimentScore());
    commentEntity.setProperty("timestamp-ms", comment.getTimestampMs());

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);
  }

  private List<Comment> getComments(int maxNumComments) {
    List<Comment> comments = new ArrayList<>();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    // Get all comments stored on Datastore
    for (Entity entity : results.asIterable(FetchOptions.Builder.withLimit(maxNumComments))) {
      String commentString = (String) entity.getProperty("comment");
      String userName = (String) entity.getProperty("user-name");
      double sentimentScore = (double) entity.getProperty("sentiment-score");
      double timestampMs = (double) entity.getProperty("timestamp-ms");

      Comment comment = new Comment(commentString, userName, sentimentScore, timestampMs);
      comments.add(comment);
    }

    return comments;
  }
}
