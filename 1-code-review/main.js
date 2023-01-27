(function () {
  // VARIABLES
  const IMAGE_COUNT = 50;
  const PAGINATE_BY = 10;

  const dogBreedsList = document.querySelector('#dogBreedsList');
  const thumbnailContainerElement = document.querySelector(
    '#thumbnailContainerElement'
  );
  const dogBreedInput = document.querySelector('#dogBreedInput');
  const paginationElement = document.querySelector('#paginationElement');
  const imageModal = document.querySelector('#imageModal');
  const imageModalDisplay = document.querySelector('#imageModalDisplay');

  // API FUNCTIONS

  async function getDogBreeds() {
    // API call to dog.ceo
    // Returns all the possible breeds in a list
    let response = await fetch('https://dog.ceo/api/breeds/list/all');
    let breeds = await response.json();
    return Object.keys(breeds.message);
  }

  async function getDogImages(breed, count) {
    // API call to dog.ceo
    // Returns a maximum count images in a list of a specific breed
    let response = await fetch(
      `https://dog.ceo/api/breed/${breed}/images/random/${count}`
    );
    let images = await response.json();
    return images.message;
  }

  // FUNCTIONS

  async function setupDogBreedOptions() {
    const breeds = await getDogBreeds();
    // Calls API for list of breeds
    // Populates breeds into datalist for users to select from
    for (const breed of breeds) {
      const optionElement = document.createElement('option');
      optionElement.setAttribute('value', breed);
      dogBreedsList.appendChild(optionElement);
    }
  }

  function populateThumbnails(images, start) {
    thumbnailContainerElement.innerHTML = '';

    for (const image of images.slice(start, start + PAGINATE_BY)) {
      const imageElement = document.createElement('div');
      imageElement.innerHTML = `<img src=${image}>`;
      thumbnailContainerElement.appendChild(imageElement);
      imageElement.addEventListener('click', function () {
        displayModal(image);
      });
    }
  }

  async function changeSelectedBreed(breed) {
    const images = await getDogImages(breed, IMAGE_COUNT);

    paginationElement.innerHTML = '';

    for (let pageNumber = 0; pageNumber < images.length / 10; pageNumber += 1) {
      const pageNumberElement = document.createElement('button');
      if (pageNumber === 0) {
        pageNumberElement.classList.add('active');
      }
      pageNumberElement.innerHTML = `${pageNumber + 1}`;
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

  function displayModal(image) {
    // Sets the src to the image URL that was passed and makes it visible
    imageModalDisplay.setAttribute('src', image);
    imageModal.style.visibility = 'visible';
  }

  function hideModal() {
    // returns modal to default view
    imageModal.style.visibility = 'hidden';
  }

  // EVENT HANDLERS

  function onDogBreedSelected(event) {
    const breed = event.target.value;
    changeSelectedBreed(breed);
  }

  // INIT

  function init() {
    setupDogBreedOptions();

    dogBreedInput.addEventListener('change', onDogBreedSelected);
    imageModal.addEventListener('click', hideModal);
  }

  init();
})();
