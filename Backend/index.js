const express = require('express');
const bodyParser = require('body-parser');
const port = process.env.PORT || 3000;
const cors = require('cors');
const app = express();

const corsOptions = {
    origin: '*',
    Credentials: true,
    optionsSuccessStatus: 200
};

app.use(cors(corsOptions));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));

app.get('/w2m', (req, res) => {
    res.json({ message: 'Welcome to the where2meet backend' }); 
});

app.listen(port, () => {
    console.log('Server is running on port: ' + port);
});