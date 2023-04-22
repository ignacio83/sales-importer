const express = require('express');
const livereload = require("livereload");
const connectLiveReload = require("connect-livereload");

const app = express();
const port = process.env.PORT || 3000

const liveReloadServer = livereload.createServer();
liveReloadServer.watch('public');
liveReloadServer.server.once("connection", () => {
    setTimeout(() => {
        liveReloadServer.refresh("/");
    }, 100);
});

app.use(connectLiveReload());
app.use(express.static('public'))

app.listen(port)

console.log(`Server started at port: ${port}`)