(function () {
  // VARIABLES
  const IMAGE_COUNT = 50;
  const PAGINATE_BY = 10;

  //@COMMENT These DOM elemnts below are fixed constants that don't get reassigned later, so we can use the
  // UPPERCASE_FORMAT here as well to help differentiate these from other standard non-constant variables.

  //@COMMENT See comment right below "FUNCTIONS" section below for additional details. These elements are related to
  // DOM-manipulation logic and should be moved into a separate `DogBreedsView` class.
  const dogBreedsList = document.querySelector('#dogBreedsList');
  const thumbnailContainerElement = document.querySelector(
    '#thumbnailContainerElement'
  );
  const dogBreedInput = document.querySelector('#dogBreedInput');
  const paginationElement = document.querySelector('#paginationElement');
  const imageModal = document.querySelector('#imageModal');
  const imageModalDisplay = document.querySelector('#imageModalDisplay');

  // API FUNCTIONS
  //@COMMENT The 2 "fetch" calls below followed by a call to response.json() should be moved into a separate object that
  // could be named something like "DogApiService.js". This will isolate the API logic so that it becomes abstract to
  // us, the user of the API, and allows us to avoid needing to process the logic of the actual API call while trying to
  // understand the rest of the code here.
  // (Isolating this will also make unit-testing the code much easier since we can mock these calls during the test)

  //@COMMENT The 2 API URL strings should be pulled out into dedicated string constants - these constants could be
  // declared in the same DogApiService mentioned previously. This makes it easy to group and view all the apis being
  // invoked by our app at a glance and update them as needed without searching through the file.

  //@COMMENT It may not be necessary for the app as it is now since it's pretty small, but it may be worth considering to
  // create a DogService.js that sits on top of DogApiService.js. This way, the DogApiService behaves as a sort of "DAO"
  // layer and DogService can take the results of the API call and modify/massage it as needed before returning the
  // results to the main file here.

  async function getDogBreeds() {
    // API call to dog.ceo
    // Returns all the possible breeds in a list

    //@COMMENT It's likely that the results of this api call will not change much during a normal year, so it
    // should be safe to cache the results of this api call in the browser's localStorage and retrieve it from there on
    // page load if it exists. This caching logic could be implemented in the DogService mentioned above so that it
    // remains abstract to the DogApiService which is dedicated to calling the API.
    // Caching the results will help the page's breeds list input-options load faster and avoid unnecessary network
    // traffic.

    //@COMMENT We can use const instead of let here since no modifications are done to the return value.
    let response = await fetch('https://dog.ceo/api/breeds/list/all');
    let breeds = await response.json();
    return Object.keys(breeds.message);
  }

  //@COMMENT See comments above `getDogBreeds` for refactoring that would be applicable here as well.
  async function getDogImages(breed, count) {
    // API call to dog.ceo
    // Returns a maximum count images in a list of a specific breed

    //@COMMENT Since this is currently a template string, refactoring it to a String constant within DogApiService would
    // prevent us from using standard template-literal variable interpolation. There are a few options we could do here:
    // 1. Make an exception for this url and don't extract it to a string constant
    // 2. Split up the strings around the interpolation into separate constants and concatenate them together
    // 3. Make our own "String.format" method to substitute the relevant placeholders using something like regex
    // 4. Use a library like Prototype.js to provide a function for string interpolation.
    // If we expect this site to grow and have more urls in the future, it would be best to define our own format
    // function or go the library route since that will keep the code structure cleaner.

    //@COMMENT We can use const instead of let here since no modifications are done to the return value.
    let response = await fetch(
      `https://dog.ceo/api/breed/${breed}/images/random/${count}`
    );
    let images = await response.json();
    return images.message;
  }


  // FUNCTIONS

  //@COMMENT All of the functions below within this "FUNCTIONS" section do 2 things within the same method:
  // - perform business logic for manipulating the dog information we get from the API
  // - update the HTML of the actual DOM to reflect the changes we want to render based on the business logic
  //
  // This causes complexity in understanding and maintaining the code because we have to separate what part of the code
  // is doing business logic from what part of the code is just updating the DOM. To help alleviate this issue, we
  // should separate out the logic for updating the actual DOM into a separate object/class, such as "DogBreedsView".
  // Then, as an example, if we wanted to add the results of the dog-breed-list API call to its input options, we could
  // call a method like `DogBreedsView.addBreeds(<string[]>)` and it would contain the required logic for appending the
  // appropriate option elements to the DOM
  //
  // As an side benefit, This will cause our code to follow the "MVC" design pattern, where our "Controller" handles
  // business logic for working with the dog-breed data while the "View" takes care of updating the actual DOM.

  async function setupDogBreedOptions() {
    //@COMMENT If the user has a slow network connection, this API call could take some time. We should show a loading
    // indicator of some form on the page so the user is aware that something is loading and its not our site that's
    // frozen.

    //@COMMENT If this api call fails, we'll want to show a banner to the user that there was network problems and we're
    // unable to load the expected data at this time. This would align with the UX principles mentioned above so the
    // user is not confused why the input box has no selection options

    const breeds = await getDogBreeds();
    // Calls API for list of breeds
    // Populates breeds into datalist for users to select from

    for (const breed of breeds) {
      //@COMMENT See comment right below "FUNCTIONS" section for additional details. The DOM-manipulation logic here
      // should be extracted to a DogBreedsView class, where it would be invoked like: `DogBreedsView.addBreed(breed)`
      const optionElement = document.createElement('option');
      optionElement.setAttribute('value', breed);
      dogBreedsList.appendChild(optionElement);
    }
  }

  function populateThumbnails(images, start) {
    //@COMMENT See comment right below "FUNCTIONS" section for additional details. The DOM-manipulation logic here
    // should be extracted to a DogBreedsView class, where it would be invoked like: `DogBreedsView.clearThumbnails()`
    thumbnailContainerElement.innerHTML = '';

    for (const image of images.slice(start, start + PAGINATE_BY)) {
      //@COMMENT See comment right below "FUNCTIONS" section for additional details. The DOM-manipulation logic here
      // should be extracted to a method like: `DogBreedsView.addThumbnail(imageUrl)`
      const imageElement = document.createElement('div');
      imageElement.innerHTML = `<img src=${image}>`;
      thumbnailContainerElement.appendChild(imageElement);
      //@COMMENT This onClick logic can be extracted to a method setOnClickHandler(imageElement, <callback>) and then we
      // can invoke the ImageModalView method described in later comments below
      imageElement.addEventListener('click', function () {
        displayModal(image);
      });
    }
  }

  async function changeSelectedBreed(breed) {
    //@COMMENT A UX improvement that could be done here: After adding the relevant img elements to the DOM,
    // the browser starts loading the individual images. However, since the images aren't all the same size, the entire
    // row of images will shift up or down based on the actual size of the loaded image which is disconcerting for a
    // viewer since they see random flashes and jumps at random areas of the site.
    // To remediate, we should pre-define a fixed size for each image to render in, and provide a smooth fade-in
    // animation when a particular image is successfully loaded by the browser. (This can be triggered via the <img>
    // `load` eventListener)

    //@COMMENT Like with the previous function, we should show a loading indicator for users with slow networks.
    // We should also show a loading indicator within an individual image's box since it's possible that even though
    // this API call passes, the individual image url may fail to load for some reason. (If the image fails to load, we
    // should show a substitute message such as "image failed to load")
    const images = await getDogImages(breed, IMAGE_COUNT);

    //@COMMENT See comment right below "FUNCTIONS" section for additional details. The DOM-manipulation logic here
    // should be extracted to a method such as: `DogBreedsView.clearPageButtons()`
    paginationElement.innerHTML = '';

    for (let pageNumber = 0; pageNumber < images.length / 10; pageNumber += 1) {
      //@COMMENT See comment right below "FUNCTIONS" section for additional details. The DOM-manipulation logic here
      // should be extracted to a method such as: `DogBreedsView.addPageButton(pageNumber, images)`
      // Also, rather than passing in the entire images array, we can pass in a slice of the relevant URLs for this
      // particular button to render so each button's logic is encapsulated with only the data that it needs to be aware
      // of.
      const pageNumberElement = document.createElement('button');
      if (pageNumber === 0) {
        //@COMMENT The logic here for modifying the active class of the relevant pageButton should be extracted to a
        // method such as `DogBreedsView.setActivePageButton(pageNumber)`
        pageNumberElement.classList.add('active');
      }
      pageNumberElement.innerHTML = `${pageNumber + 1}`;
      //@COMMENT This onClick handler can be extracted as a separate function like `setOnClickHandler`. Then, after
      // `DogBreedsView.addPageButton` is invoked, that would return a reference to the new DOM element. The main file
      // (ie the Controller), can then call setOnClickHandler(pageButtonElement, <callback>) and supply the appropriate
      // call(s) to DogBreedsView for modifying the CSS.
      pageNumberElement.addEventListener('click', function (event) {
        let oldActivePage = document.querySelector(
          '#paginationElement button.active'
        );
        if (oldActivePage) {
          oldActivePage.classList.remove('active');
        }
        event.target.classList.add('active');
        populateThumbnails(images, pageNumber * 10);
      });
      paginationElement.appendChild(pageNumberElement);
    }
    populateThumbnails(images, 0);
  }

  //@COMMENT The two modal functions below serve a separate, special purpose compared to the "standard" DogBreeds
  // thumbnail views, so it makes sense to extract both of these into a separate "View" class such as
  // `ImageModalView`. This service can then be invoked by the main.js file
  function displayModal(image) {
    // Sets the src to the image URL that was passed and makes it visible

    //@COMMENT If the image fails to load, we should set up a handler to show an alternate message to the user so that
    // they are not seeing a blank modal on click. We could make a new function like `ImageModalView.showErrorModal` and
    // set up a method in main.js `setErrorCallbackOnFailedImageLoad` with the appropriate logic to invoke the method.
    imageModalDisplay.setAttribute('src', image);
    imageModal.style.visibility = 'visible';
  }

  function hideModal() {
    // returns modal to default view
    imageModal.style.visibility = 'hidden';
  }

  // EVENT HANDLERS
  //@COMMENT It would be better to rename the function to something related to what it's doing. Eg
  // `updateImageGalleryForNewDogBreed`
  function onDogBreedSelected(event) {
    const breed = event.target.value;
    changeSelectedBreed(breed);
  }

  // INIT

  function init() {
    setupDogBreedOptions();

    //@COMMENT This is minor since the app isn't large, but it may be more optimal to extract these to a
    // function that describes what they're doing. Eg:
    // `setOnChangeCallbackFor(DogBreedsView.getDogBreedSelector(), updateImageGalleryForNewDogBreed);
    // I could foresee later that we might want to show a heading or special text elsewhere on the page for the breed
    // the user selected aside from the input itself, which would add extra logic to this function and justify cleaning
    // it up ahead-of-time.
    dogBreedInput.addEventListener('change', onDogBreedSelected);
    imageModal.addEventListener('click', hideModal);
  }

  init();
})();
//@COMMENT Currently, all the logic in this code has been added directly into this immediately-invoked function within
// main.js. This makes the individual business logic of each function and code-component very difficult to unit-test,
// which means the only testing we could do easily would be integration tests. To remedy this, we should extract the
// functions into separate js files as described above, and write tests for each function as relevant and mock the other
// function/API calls that are irrelevant to what's being tested.

//@COMMENT As an aside, if we're planning to scale this site and add more functionality like authentication,
// analytics, multiple routes, etc., it would be best to switch our code to use a professional web development
// framework such as React, Angular, Vue, etc. so that we're not spending time trying to manually manipulate the DOM.
// That time can be shifted to building business logic instead, and we let the framework deal with updating the DOM
// appropriately.

