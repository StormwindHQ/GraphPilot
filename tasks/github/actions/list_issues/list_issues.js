require('isomorphic-fetch');

/**
 * The trigger script creates a Github webhook that fires
 * when a new issue is created. It will not create a duplicated
 * webhook if it already exists.
 */
function index() {
  // Loading env variables
  const ACCESS_TOKEN = process.env.ACCESS_TOKEN; // Github personal token; https://help.github.com/articles/creating-a-personal-access-token-for-the-command-line/

  fetch(`https://api.github.com/issues?access_token=${ACCESS_TOKEN}`, {
    method: 'GET',
    mode: "cors",
    cache: "no-cache",
    headers: {
      "Content-Type": "application/json",
    },
  })
    .then((response) => {
      if (response.status >= 400) {
        throw new Error(`${response.status} failed to create a webhook! - ${response.statusText}`);
      }
      return response.json();
    })
    .then(function(stories) {
      console.log(stories);
    })
    .catch((err) => console.log(err));
}

index();
