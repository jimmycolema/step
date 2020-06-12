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

package com.google.sps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class FindMeetingQuery {

  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    // Collect parameters of meeting request
    Collection<String> attendees = request.getAttendees();
    Collection<String> optionalAttendees = request.getAttendees();
    int duration = (int) request.getDuration();

    List<TimeRange> viableTimes = new ArrayList<>();
    List<TimeRange> reservedTimes = new ArrayList<>();

    // List of pre-existing events sorted by starting time
    events.stream()
          .sorted((e1, e2) -> e1.getWhen().start() - e2.getWhen().start())
          .filter (e -> !Collections.disjoint(e.getAttendees(), attendees))
          .forEach(e -> {
            reservedTimes.add(e.getWhen());
          });

    viableTimes = calculateViableTimes(reservedTimes, duration);
    return viableTimes;
  }

  public List<TimeRange> calculateViableTimes(List<TimeRange> reservedTimes, long duration) {
    List<TimeRange> viableTimes = new ArrayList<>();
    int potentialStart = TimeRange.START_OF_DAY;

    for (int i = 0; i < reservedTimes.size(); i++) {
      int reservedTimeStart = reservedTimes.get(i).start();
      int reservedTimeEnd = reservedTimes.get(i).end();

      // Add viable block if current scheduled event does not conflict
      int potentialEnd = potentialStart + (int) duration;
      if (potentialEnd <= reservedTimeStart) {
        TimeRange viableTime = TimeRange.fromStartEnd(potentialStart, reservedTimeStart, false);
        viableTimes.add(viableTime);
      }

      potentialStart = reservedTimeEnd;
      // Guard against nested events
      if (i < reservedTimes.size() - 1) {
        int nextReservedTimeStart = reservedTimes.get(i+1);
      }
    }

    int potentialEnd = potentialStart + (int) duration;
    if (potentialEnd < TimeRange.END_OF_DAY) {
      TimeRange viableTime = TimeRange.fromStartEnd(potentialStart, TimeRange.END_OF_DAY, true);
      viableTimes.add(viableTime);
    }

    return viableTimes;
  }
}
