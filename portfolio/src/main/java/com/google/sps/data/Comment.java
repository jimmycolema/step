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

package com.google.sps.data;

/*
 * Represents a comment entered by a user with a sentiment analysis score
 * represented numerically.
 */
public class Comment {

  private final String comment;
  private final String userName;
  private final double sentimentScore;
  private final long timestamp_ms;

  public Comment(String comment, String userName, double sentimentScore, double timestamp) {
    this.comment = comment;
    this.userName = userName;
    this.sentimentScore = sentimentScore;
    this.timestamp_ms = (long) timestamp_ms;
  }

  public String getComment() {
    return comment;
  }

  public String getUserName() {
    return userName;
  }

  public double getSentimentScore() {
    return sentimentScore;
  }

  public double getTimestampMs() {
    return timestamp_ms;
  }
}
