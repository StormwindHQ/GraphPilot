const Discord = require('discord.js');

const main = (params) => new Promise((resolve, reject) => {
  const BOT_TOKEN = params.BOT_TOKEN;

  const client = new Discord.Client();

  client.on('ready', () => {
    console.log(`Logged in as ${client.user.tag}!`);
    const general = client.channels.find('name', 'general');
    Promise.all([
      general.send('Testing GraphWhisk!'),
    ]).then(() => {
      client.destroy().then(() => {
        resolve();
      });
    });

  });

  client.login(BOT_TOKEN);
});

exports.main = main;
