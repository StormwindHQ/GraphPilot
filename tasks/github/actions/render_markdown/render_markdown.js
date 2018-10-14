require('isomorphic-fetch');

/**
 * The trigger script creates a Github webhook that fires
 * when a new issue is created. It will not create a duplicated
 * webhook if it already exists.
 */
const main = (param) => new Promise((resolve, reject) => {
  const BODY = JSON.stringify({
    "text": "Hello world github/linguist#1 **cool**, and #1!",
    "mode": "gfm",
    "context": "github/gollum"
  });
  fetch('https://api.github.com/markdown', {
    method: 'POST',
    mode: "cors",
    cache: "no-cache",
    headers: {
      "Content-Type": "application/json",
    },
    body: BODY
  })
    .then((response) => {
      console.log(response);
      resolve(response);
    })
    .catch((err) => {
      console.error(err);
      reject(err);
    });
});

exports.main = main;
