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

let map;
let editMarker;

/**
 * Adds a random piece of Uncle Iroh wisdom to the page
 */
function addIrohWisdom() {
  const irohQuotes = 
    ["Sharing tea with a fascinating stranger is one of life's true \
      delights.",
     "Hope is something you give yourself. That is the meaning of \
      inner strength",
     "Destiny is a funny thing. You never know how things are going to work \
      out.", 
     "It is usually best to admit mistakes when they occur, and to seek to \
      restore honor.",
     "While it is always best to believe in onself, a little help from \
      others can be a great blessing.",
     "Pride is not the opposite of shame, but its source. True humility \
      is the only antidote to shame.",
     "Life happens wherever you are, whether you make it or not."];

  // Pick random piece of Iroh wisdom
  const irohQuote = irohQuotes[Math.floor(Math.random() * irohQuotes.length)]

  // Add it to the page
  const irohContainer = document.getElementById('iroh-container');
  irohContainer.innerText = irohQuote;
}

/**
 * Generates a URL for a random image in the images directory and adds an img
 * element with that URL to the page.
 */
function randomizeImage() {
  // The images directory contains 11 images, so generate a random index between
  // 1 and 11.
  const imageIndex = Math.floor(Math.random() * 11) + 1;
  const imgUrl = 'images/friends/friend-' + imageIndex + '.JPG';

  const imgElement = document.createElement('img');
  imgElement.src = imgUrl;
  imgElement.width = "1000";

  const imageContainer = document.getElementById('random-image-container');
  // Remove the previous image.
  imageContainer.innerHTML = '';
  imageContainer.appendChild(imgElement);
}

/**
 * Fetches text from the server ArrayList and adds them to the DOM.
 */
async function displayCommentsToPage() {
  const maxNumComments = document.getElementById("max-num-comments").value;
  const response = await fetch(`/comments?max-num-comments=${maxNumComments}`);
  const textArray = await response.json();

  const arrayTextElement = document.getElementById('array-text-container');
  arrayTextElement.innerHTML = '';
    
  for (let i = 0; i < textArray.length; i++) {
    let comment = textArray[i];
    let commentString = comment.comment;
    let sentimentScore = comment.sentimentScore;

    arrayTextElement.appendChild(
        createListElement(commentString + ' ' + '(' + sentimentScore + ')' + '\n'));
  }
}

/*
 * Creates an <li> element containing text. 
 */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}

async function deleteAllComments() {
  const response = await fetch('/delete-comments', {method: 'POST'});
  displayCommentsToPage();
}

/**
 * Generates two maps: one centered on the United States with markers placed on 
 * specified backpacking locations, the other displaying data pulled from the 
 * internet.
 */
function initMap() {
  const centerUSA = {lat: 37.0902, lng: -95.7129};
  const favoriteSpots = [
    ["<h3>Mt. Ritter</h3> \
      <p>I have this mountain tattood onto my leg!</p> \
      <img src=images/backpacking/bp-2.JPG width=500px>", 37.6894, -119.1990],
    ["<h3>Tuckerman's Ravine</h3> \
      <p>It was on this twelve hour road trip with my dad to backpack \
         the Presidential Traverse that I first listened to Pink Floyd, \
         now my favorite band!</p> \
      <img src=images/backpacking/bp-11.jpg width=300px>", 44.2625, -71.2983],
    ["<h3>Black Forest Trail</h3> \
      <p>I've backpacked this trail three separate times with some very dear \
         friends, and began going to Quaker meeting after the first! I've \
         gone twice in the winter, and have hitch-hiked in an old van with \
         a nice hippie and a dog!</p> \
      <img src=images/friends/friend-2.JPG width=500px>", 41.4724, -77.5017],
    ["<h3>Lake Serene</h3> \
      <p>I biked seven miles along the side of the highway at night with \
         headlamps and friends after taking a bus for three hours to camp \
         here!</p> \
      <img src=images/friends/friend-3.JPG width=500px>", 47.7814, -121.5700],
    ["<h3>Pine Knob Shelter</h3> \
      <p>I've probably slept in this shelter at least 10 times over the the \
         course of my life! This stretch of the AT is where I went on my \
         first ever backpacking trip!</p>", 39.5429, -77.6018],
    ["<h3>Rialto Beach</h3> \
      <p>While watching the stars on the shores of the Olympic, my friends \
         and I discussed how amazingly old the matter that makes up all that \
         we see is, while throwing driftwood back into the ocean. \
      <img src=images/backpacking/bp-10.jpg width=500px>", 47.9173, -124.6394],
    ["<h3>Skyline Drive</h3> \
      <p>I didn't buy the Farmer's Oats flavored Monster Energy drink on \
         this trip!<p> \
      <img src=images/friends/friend-8.JPG width=500px>", 38.0325, -78.8571]];

  const mapOptions = {
    zoom: 3,
    center: centerUSA,
    mapTypeId: 'satellite'
    };
  
  map = new google.maps.Map(document.getElementById('map'), mapOptions);
  // Add Jimmy hard-coded markers to map
  for (let i = 0; i < favoriteSpots.length; i++) {
    let infowindow = new google.maps.InfoWindow({
      content: favoriteSpots[i][0]
    });
    let marker = new google.maps.Marker({
      position: new google.maps.LatLng(favoriteSpots[i][1], favoriteSpots[i][2]),
      map: map
    });

    marker.addListener('click', function() {
      infowindow.open(map, marker);
    });
  }

  map.addListener('click', (event) => {
    createMarkerForEdit(event.latLng.lat(), event.latLng.lng());
  });

  // Add user-entered markers
  fetchMarkers();

  let psaMap = new google.maps.Map(document.getElementById('psa-map'), mapOptions);
}

/*
 * Fetches user markers from datastore and adds them to map
 */
async function fetchMarkers() {
  const response = await fetch('/markers');
  const markers = await response.json();

  for (let i = 0; i < markers.length; i++) {
    marker = markers[i];

    let {content, lat, lng} = marker;
    createMarkerForDisplay(lat, lng, content);
  }
}

/*
 * Creates a marker that shows a read-only info window when clicked. 
 */
function createMarkerForDisplay(latitude, longitude, content) {
  const marker =
      new google.maps.Marker({position: {lat: latitude, lng: longitude}, map: map});

  const infoWindow = new google.maps.InfoWindow({content: content});
  marker.addListener('click', () => {
    infoWindow.open(map, marker);
  });
}
 
/*
 * Sends a marker to the backend for saving. 
 */
function postMarker(latitude, longitude, content) {
  const params = new URLSearchParams();
  params.append('latitude', latitude);
  params.append('longitude', longitude);
  params.append('content', content);

  fetch('/markers', {method: 'POST', body: params});
}

/*
 * Creates a marker that shows a textbox the user can edit. 
 */
function createMarkerForEdit(latitude, longitude) {
  // If we're already showing an editable marker, then remove it.
  if (editMarker) {
    editMarker.setMap(null);
  }

  editMarker =
      new google.maps.Marker({position: {lat: latitude, lng: longitude}, map: map});

  const infoWindow =
      new google.maps.InfoWindow({content: buildInfoWindowInput(latitude, longitude)});

  // When the user closes the editable info window, remove the marker.
  google.maps.event.addListener(infoWindow, 'closeclick', () => {
    editMarker.setMap(null);
  });

  infoWindow.open(map, editMarker);
}

/*
 * Builds and returns HTML elements that show an editable textbox and a submit
 * button.
 */
function buildInfoWindowInput(latitude, longitude) {
  const textBox = document.createElement('textarea');
  const button = document.createElement('button');
  button.appendChild(document.createTextNode('Submit'));

  button.onclick = () => {
    postMarker(latitude, longitude, textBox.value);
    createMarkerForDisplay(latitude, longitude, textBox.value);
    editMarker.setMap(null);
  };

  const containerDiv = document.createElement('div');
  containerDiv.appendChild(textBox);
  containerDiv.appendChild(document.createElement('br'));
  containerDiv.appendChild(button);

  return containerDiv;
}