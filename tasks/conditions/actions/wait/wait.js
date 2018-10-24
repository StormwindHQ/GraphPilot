function main (params) {
  // Must be in miliseconds
  const delay = params.delay;
  return new Promise((resolve) => {
    setTimeout(() => resolve(params), delay);
  });
}
