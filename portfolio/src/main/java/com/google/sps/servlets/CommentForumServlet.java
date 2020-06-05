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
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/comments")
public class CommentForumServlet extends HttpServlet {

  private static final Query query = new Query("Comment");
  private static final Gson gson = new Gson();
  
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    int maxNumComments = getMaxNumComments(request);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    // Get all messages stored on Datastore
    ArrayList<String> messages = new ArrayList<String>();
    for (Entity entity : results.asIterable(FetchOptions.Builder.withLimit(maxNumComments))) {
      String comment = (String) entity.getProperty("comment");
      messages.add(comment);
    }

    response.setContentType("text/html;");
    String json = convertToJson(messages);
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Parse input from the form
    String comment = getParameter(request, "text-input", "");
    boolean upperCase = Boolean.parseBoolean(getParameter(request, "upper-case", "false"));
    boolean lowerCase = Boolean.parseBoolean(getParameter(request, "lower-case", "false"));
    long timestamp = System.currentTimeMillis();

    if (upperCase && lowerCase) {
      upperCase = false;
      lowerCase = false;
    }
    if (upperCase) {
      comment = comment.toUpperCase();
    }
    if (lowerCase) {
      comment = comment.toLowerCase();
    }

    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("comment", comment);
    commentEntity.setProperty("timestamp", timestamp);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    response.sendRedirect("/index.html");
  }

  private String convertToJson(ArrayList<String> messages) {
    String json = gson.toJson(messages);
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
    if (maxNumComments < 0) {
      maxNumComments = 0;
    }
    if (maxNumComments > 100) {
      maxNumComments = 100;
    }

    return maxNumComments;
  }
}
