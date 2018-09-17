function main(param) {
  console.log('checking param', param);
  return {
    message: 'testing the message!',
    param,
    body: `<html><body><h3>hey man it works!</h3></body></html>`
  }
}
