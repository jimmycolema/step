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