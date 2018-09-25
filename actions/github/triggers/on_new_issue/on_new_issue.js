require('isomorphic-fetch');

/**
 * The trigger script creates a Github webhook that fires
 * when a new issue is created. It will not create a duplicated
 * webhook if it already exists.
 */
function index() {
  // Loading env variables
  const REPO = process.env.REPO; // Name of the Github user
  const USER = process.env.USER; // Name of the Github repository
  const ACCESS_TOKEN = process.env.ACCESS_TOKEN; // Github personal token; https://help.github.com/articles/creating-a-personal-access-token-for-the-command-line/
  const WEBHOOK_URL = process.env.WEBHOOK_URL; // URL to post the webhook payload
  const body = JSON.stringify({
    "name": "web",
    "active": true,
    "events": [
      "issues"
    ],
    "config": {
      "url": WEBHOOK_URL,
      "content_type": "json"
    }
  });
  fetch(`https://api.github.com/repos/${USER}/${REPO}/hooks?access_token=${ACCESS_TOKEN}`, {
    method: 'POST',
    mode: "cors",
    cache: "no-cache",
    headers: {
      "Content-Type": "application/json",
    },
    body,
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
