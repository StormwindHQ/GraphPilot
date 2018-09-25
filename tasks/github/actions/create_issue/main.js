require('isomorphic-fetch');

/**
 * The trigger script creates a Github webhook that fires
 * when a new issue is created. It will not create a duplicated
 * webhook if it already exists.
 */
const main = (param) => new Promise((resolve, reject) => {
  // Loading env variables
  const REPO = param.REPO; // Name of the Github user
  const USER = param.USER; // Name of the Github repository
  const ACCESS_TOKEN = param.ACCESS_TOKEN; // Github personal token; https://help.github.com/articles/creating-a-personal-access-token-for-the-command-line/
  const BODY = JSON.stringify({
    "title": "Found a bug",
    "body": "I'm having a problem with this.",
    "assignees": [
      "JasonShin"
    ],
    "state": "open",
    "labels": [
      "bug"
    ]
  });
  console.log('checking params', REPO, USER, ACCESS_TOKEN, BODY);
  console.log('fetch', fetch);
  fetch(`https://api.github.com/repos/${USER}/${REPO}/issues?access_token=${ACCESS_TOKEN}`, {
    method: 'POST',
    mode: "cors",
    cache: "no-cache",
    headers: {
      "Content-Type": "application/json",
    },
    body: BODY
  })
    .then((response) => {
      if (response.status >= 400) {
        throw new Error(`${response.status} failed to create a webhook! - ${response.statusText}`);
      }
      return response.json();
    })
    .then(function(result) {
      console.log(result);
      return resolve(result)
    })
    .catch((err) => reject(err));
});

exports.main = main;
