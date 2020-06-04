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
 * Generates a map centered on the United States with markers placed on 
 * specified locations. 
 */
function initMap() {
  const centerUSA = {lat: 37.0902, lng: -95.7129};
  const favoriteSpots = [
    ["Mt. Ritter", 37.6894, -119.1990],
    ["Tuckerman's Ravine", 44.2625, -71.2983],
    ["Black Forest Trail", 41.4724, -77.5017],
    ["Lake Serene", 47.7814, -121.5700],
    ["Pine Knob Shelter", 39.5429, -77.6018],
    ["Rialto Beach", 47.9173, -124.6394],
    ["Skyline Drive", 38.0325, -78.8571]];

  const mapOptions = {
    zoom: 3,
    center: centerUSA,
    mapTypeId: 'satellite'
    };
  let map = new google.maps.Map(document.getElementById('map'), 
    mapOptions);
  for (let i = 0; i < favoriteSpots.length; i++) {
    let marker = new google.maps.Marker({
      position: new google.maps.LatLng(favoriteSpots[i][1], favoriteSpots[i][2]),
      label: favoriteSpots[i][0],
      map: map
    });
  }
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
    arrayTextElement.appendChild(
        createListElement(textArray[i] + '\n'));
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