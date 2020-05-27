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
  // The images directory contains 13 images, so generate a random index between
  // 1 and 13.
  const imageIndex = Math.floor(Math.random() * 4) + 1;
  const imgUrl = 'images/friends/friend-' + imageIndex + '.JPG';

  const imgElement = document.createElement('img');
  imgElement.src = imgUrl;
  imgElement.width = "504";
  imgElement.height = "378";

  const imageContainer = document.getElementById('random-image-container');
  // Remove the previous image.
  imageContainer.innerHTML = '';
  imageContainer.appendChild(imgElement);
}