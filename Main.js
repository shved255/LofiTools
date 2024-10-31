const mineflayer = require('mineflayer');
const fs = require('fs');
const readline = require('readline');
let bots = {};
let commandInterface = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

function startBot(version, username, host, port) {
    const bot = mineflayer.createBot({
        host: host,
        port: port,
        username: username,
        version: version
    });
    bots[username] = bot;
    bot.on('kicked', (reason) => {
        console.log(`kicked:${username}: ${reason}`);
    });

    bot.on('message', (message) => {
        const msg = message.toString();
        if (msg.includes('auth') || msg.includes('register') || msg.includes('login') || msg.includes('/')) {
            console.log(` mess:${username}:${msg}`);
        }
    });
}

function stopBot(username) {
    if (bots[username]) {
        bots[username].quit();
        delete bots[username];
        console.log(`${username} stopped.`);
    } else {
        console.log(`Bot ${username} not found.`);
    }
}

commandInterface.on('line', (input) => {
    const parts = input.split(' ');
    const command = parts[0];
    if (command === 'start') {
        const version = parts[1];
        const username = parts[2];
        const host = parts[3];
        const port = parseInt(parts[4]);
        startBot(version, username, host, port);
    } else if (command === 'stop') {
        const username = parts[1];
        stopBot(username);
    } else {
        console.log('Unknown command');
    }
});

