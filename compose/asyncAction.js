function main(params) {
    return new Promise(function(resolve, reject) {
        setTimeout(function() {
            resolve({ message: "Hello world after 2000 millisec" });
        }, 2000);
    });
}