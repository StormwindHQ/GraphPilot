const httpProxy = require('http-proxy');

httpProxy.createProxyServer({
    secure: false,
    target: 'https://localhost:443'
}).listen(8000);
