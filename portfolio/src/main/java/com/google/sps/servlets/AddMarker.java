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
import com.google.sps.data.Marker;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/** Servlet that allows user to post the name of their own backpacking haunt */
@WebServlet("/markers")
public class AddMarker extends HttpServlet {
  
  private static final Gson gson = new Gson();
  private static final Query query = new Query("Marker");

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");

    Collection<Marker> markers = getMarkers();
    String json = gson.toJson(markers);

    response.getWriter().println(json);
  }
  
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // Validate submitted marker data is valid
    String content = Jsoup.clean(request.getParameter("content"), Whitelist.none());
    double latitude;
    double longitude;
    try {
      latitude = Double.parseDouble(request.getParameter("latitude"));
      longitude = Double.parseDouble(request.getParameter("longitude"));
    } catch (NumberFormatException e) {
      System.err.println("Could not convert latitude and/or longitude to float");
      return;
    }

    Marker marker = new Marker(latitude, longitude, content);
    storeMarker(marker);
  }

  /*
   * Store marker in Datastore
   */
  public void storeMarker(Marker marker) {
    Entity markerEntity = new Entity("Marker");
    markerEntity.setProperty("content", marker.getContent());
    markerEntity.setProperty("latitude", marker.getLat());
    markerEntity.setProperty("longitude", marker.getLng());

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(markerEntity);
  }

  /*
   * Get the lat, lon, and name of location 
   */
  private Collection<Marker> getMarkers() {
    Collection<Marker> markers = new ArrayList<>();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      String content = (String) entity.getProperty("content");
      double latitude = (double) entity.getProperty("latitude");
      double longitude = (double) entity.getProperty("longitude");

      Marker marker = new Marker(latitude, longitude, content);
      markers.add(marker);
    }

    return markers;
  }
}