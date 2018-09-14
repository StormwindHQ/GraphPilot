const httpProxy = require('http-proxy');

const { TARGET_PROTOCOL, TARGET_HOST, TARGET_PORT, SRC_PORT, SECURE } = process.env;

const proxyTargetOptions = {
    secure: SECURE === 'true',
    target: `${TARGET_PROTOCOL}://${TARGET_HOST}:${TARGET_PORT}`
};

const srcPort = parseInt(SRC_PORT);

console.log('Init proxy target options', proxyTargetOptions);
console.log('Init proxy src port', srcPort);

httpProxy.createProxyServer(proxyTargetOptions).listen(srcPort);
